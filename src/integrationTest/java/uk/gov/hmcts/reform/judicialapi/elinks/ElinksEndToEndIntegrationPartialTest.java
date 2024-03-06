package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.JudicialRoleType;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;


class ElinksEndToEndIntegrationPartialTest extends ElinksEnabledIntegrationTest {

    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @MockBean
    ElinkTopicPublisher elinkTopicPublisher;

    @Autowired
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @BeforeEach
    void before() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks end to end success scenario")
    @Test
    void test_elinks_end_to_end_success_scenario_with_partial_success_return_status_200()
            throws JOSEException, JsonProcessingException {

        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled", true);
        ReflectionTestUtils.setField(publishSidamIdService, "elinkTopicPublisher", elinkTopicPublisher);

        dataloadSchedulerJobRepository.deleteAll();
        elinksApiJobScheduler.loadElinksJob();

        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();
        DataloadSchedulerJob jobDetails = audits.get(0);

        //assserting scheduler data
        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isNotNull();
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),jobDetails.getPublishingStatus());

        // asserting location data
        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();
        Map<String, Object> locationResponse = elinksReferenceDataClient.getLocations();
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) locationResponse.get("body");
        ElinkDataSchedularAudit locationAuditEntry = elinksAudit.get(0);

        assertThat(locationResponse).containsEntry("http_status", "200 OK");
        assertEquals(BASE_LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());
        assertEquals(LOCATIONAPI,locationAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), locationAuditEntry.getStatus());


        List<Location> locationsList = locationRepository.findAll();
        assertEquals(11, locationsList.size());
        assertEquals("1", locationsList.get(1).getRegionId());
        assertEquals("London", locationsList.get(1).getRegionDescEn());


        //asserting baselocation data
        validateBaseLocation(elinksAudit);

        //asserting people data
        validatePeopleData(elinksAudit);


        //asserting userprofile data for leaver api
        validateLeaverData(elinksAudit);

        //asserting userprofile data for deleted api
        validateDeletedData(elinksAudit);

        //assert elastic search api
        idamSetUp();
        validateElasticSearch(audits);

        // asserting SIDAM publishing
        validateSidamPublish();

        List<ElinkDataExceptionRecords> elinksException = elinkDataExceptionRepository.findAll();
        assertThat(elinksException).hasSize(5);

    }

    private void validateSidamPublish() {
        Map<String, Object> idamResponse = elinksReferenceDataClient.publishSidamIds();
        doNothing().when(elinkTopicPublisher).sendMessage(anyList(),anyString());
        assertThat(idamResponse).containsEntry("http_status", "200 OK");
        HashMap publishSidamIdsResponse = (LinkedHashMap)idamResponse.get("body");

        assertThat(publishSidamIdsResponse.get("publishing_status")).isNotNull();

        List<ElinkDataExceptionRecords> elinksException = elinkDataExceptionRepository.findAll();
        assertThat(elinksException).isNotNull();
    }

    private void validateElasticSearch(List<DataloadSchedulerJob> audits) {
        Map<String, Object> idamResponses = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals("200 OK",idamResponses.get("http_status"));
        List<IdamResponse> idamResponseVal = (ArrayList<IdamResponse>) idamResponses.get("body");
        assertEquals(2,idamResponseVal.size());

        List<UserProfile> userprofileAfterSidamresponse = profileRepository.findAll();
        UserProfile sidamID = userprofileAfterSidamresponse.get(0);

        assertEquals(2, userprofileAfterSidamresponse.size());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae",
            userprofileAfterSidamresponse.get(1).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e972",
            userprofileAfterSidamresponse.get(1).getSidamId());

        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), audits.get(0).getPublishingStatus());
    }

    private void validateDeletedData(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> deletedResponse = elinksReferenceDataClient.getDeleted();
        ElinkDeletedWrapperResponse deletedProfiles = (ElinkDeletedWrapperResponse) deletedResponse.get("body");
        ElinkDataSchedularAudit deletedAudit = elinksAudit.get(3);

        assertThat(deletedResponse).containsEntry("http_status", "200 OK");
        assertEquals("Deleted users Data Loaded Successfully", deletedProfiles.getMessage());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),deletedAudit.getStatus());

        List<UserProfile> deletedUserProfile = profileRepository.findAll();
        assertEquals(2, deletedUserProfile.size());
        assertEquals("4913085", deletedUserProfile.get(0).getPersonalCode());
        assertEquals(true, deletedUserProfile.get(0).getDeletedFlag());
        assertEquals(false, deletedUserProfile.get(0).getActiveFlag());
        assertEquals("2023-07-13",deletedUserProfile.get(0).getDeletedOn().toLocalDate().toString());
        assertNotNull(deletedUserProfile.get(0).getLastLoadedDate());


        ElinkDataSchedularAudit auditEntry = elinksAudit.get(3);
        assertThat(auditEntry.getId()).isPositive();
        assertEquals(DELETEDAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    private void validateLeaverData(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        ElinkLeaversWrapperResponse leaversProfiles = (ElinkLeaversWrapperResponse) leaversResponse.get("body");
        ElinkDataSchedularAudit leaversAuditEntry = elinksAudit.get(2);

        assertThat(leaversResponse).containsEntry("http_status", "200 OK");
        assertEquals("Leavers Data Loaded Successfully", leaversProfiles.getMessage());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),leaversAuditEntry.getStatus());

        List<UserProfile> leaverUserProfile = profileRepository.findAll();
        assertEquals(2, leaverUserProfile.size());
        assertEquals("4913085", leaverUserProfile.get(0).getPersonalCode());
        assertEquals(true, leaverUserProfile.get(0).getActiveFlag());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", leaverUserProfile.get(0).getObjectId());
        assertEquals("2026-07-23",leaverUserProfile.get(0).getLastWorkingDate().toString());
        assertNotNull(leaverUserProfile.get(0).getLastLoadedDate());


        ElinkDataSchedularAudit auditEntry = elinksAudit.get(2);
        assertThat(auditEntry.getId()).isPositive();
        assertEquals(LEAVERSAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    private void validatePeopleData(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> peopleResponse = elinksReferenceDataClient.getPeoples();
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse) peopleResponse.get("body");
        ElinkDataSchedularAudit peopleAuditEntry = elinksAudit.get(1);

        assertThat(peopleResponse).containsEntry("http_status", "200 OK");
        assertEquals("People data loaded successfully", profiles.getMessage());
        assertEquals(PEOPLEAPI,peopleAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS.getStatus(), peopleAuditEntry.getStatus());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(2, userprofile.size());
        assertEquals("4913085", userprofile.get(0).getPersonalCode());
        assertEquals("Rachel", userprofile.get(0).getKnownAs());
        assertEquals("Jones", userprofile.get(0).getSurname());
        assertEquals("District Judge Rachel Jones", userprofile.get(0).getFullName());
        assertEquals(null, userprofile.get(0).getPostNominals());
        assertEquals("DJ.Rachel.Jones@ejudiciary.net",
                userprofile.get(0).getEmailId());
        assertEquals("2026-07-23",
            userprofile.get(0).getRetirementDate().toString());
        assertTrue(userprofile.get(0).getActiveFlag());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", userprofile.get(0).getObjectId());
        assertNull(userprofile.get(0).getSidamId());
        assertEquals("RJ",userprofile.get(0).getInitials());

        //asserting Judiciary additonal roles data
        List<JudicialRoleType> roleRequest = judicialRoleTypeRepository.findAll();
        assertEquals(2, roleRequest.size());
        assertEquals("Course Director for COP (JC)", roleRequest.get(0).getTitle());
        assertEquals("4913085", roleRequest.get(0).getPersonalCode());
        assertEquals("427", roleRequest.get(0).getJurisdictionRoleId());
        assertEquals("fee", roleRequest.get(0).getJurisdictionRoleNameId());

    }

    private void validateBaseLocation(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        ElinkBaseLocationWrapperResponse baseLocations =
                (ElinkBaseLocationWrapperResponse) baseLocationResponse.get("body");
        ElinkDataSchedularAudit baseLocationAuditEntry = elinksAudit.get(0);

        assertThat(baseLocationResponse).containsEntry("http_status", "200 OK");
        assertEquals(BASE_LOCATION_DATA_LOAD_SUCCESS, baseLocations.getMessage());
        assertEquals(LOCATION, baseLocationAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), baseLocationAuditEntry.getStatus());


        List<BaseLocation> baseLocationList = baseLocationRepository.findAll();
        assertEquals(8, baseLocationList.size());
        assertEquals("Aberconwy",baseLocationList.get(0).getName());
        assertEquals("3",baseLocationList.get(1).getBaseLocationId());
        assertEquals("1742",baseLocationList.get(1).getParentId());
    }


    private void idamSetUp() {

        final String clientId = "234342332";
        final String redirectUri = "http://idam-api.aat.platform.hmcts.net";
        //The authorization and clientAuth is the dummy value which we can evaluate using BASE64 encoder.
        final String authorization = "ZHVtbXl2YWx1ZUBobWN0cy5uZXQ6SE1DVFMxMjM0";
        final String clientAuth = "cmQteHl6LWFwaTp4eXo=";
        final String url = "http://127.0.0.1:5000";
        tokenConfigProperties.setClientId(clientId);
        tokenConfigProperties.setClientAuthorization(clientAuth);
        tokenConfigProperties.setAuthorization(authorization);
        tokenConfigProperties.setRedirectUri(redirectUri);
        tokenConfigProperties.setUrl(url);

    }
}
