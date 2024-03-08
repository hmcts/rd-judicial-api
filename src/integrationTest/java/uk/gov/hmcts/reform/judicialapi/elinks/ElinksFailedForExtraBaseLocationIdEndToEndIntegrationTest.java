package uk.gov.hmcts.reform.judicialapi.elinks;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.JudicialRoleTypeRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkDeletedWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;

class ElinksFailedForExtraBaseLocationIdEndToEndIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    BaseLocationRepository baseLocationRepository;
    @Autowired
    AuthorisationsRepository authorisationsRepository;
    @Autowired
    AppointmentsRepository appointmentsRepository;
    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;

    @Autowired
    JudicialRoleTypeRepository judicialRoleTypeRepository;

    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @Autowired
    private ElinksApiJobScheduler elinksApiJobScheduler;

    @Autowired
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @MockBean
    ElinkTopicPublisher elinkTopicPublisher;

    @Autowired
    ElinkDataExceptionRepository elinkDataExceptionRepository;

    @BeforeEach
    void setUp() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks end to end success scenario")
    @Test
    void test_elinks_end_to_end_partial_success_scenario()
            throws JOSEException, JsonProcessingException,IOException {

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

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();

        // asserting location data
        validateLocationApi(elinksAudit);

        //asserting baselocation data
        validateBaseLocationApi(elinksAudit);

        //asserting people data
        validatePeopleApi(elinksAudit);

        //asserting userprofile data for leaver api
        validateLeaverApi(elinksAudit);

        //asserting userprofile data for deleted api
        validateDeletedApi(elinksAudit);

        //assert elastic search api
        idamSetUp();

        validateElasticSearchApi(audits);

        // asserting SIDAM publishing
        validateSidamPublish();

    }

    private void validateDeletedApi(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> deletedResponse = elinksReferenceDataClient.getDeleted();
        ElinkDeletedWrapperResponse deletedProfiles = (ElinkDeletedWrapperResponse) deletedResponse.get("body");
        ElinkDataSchedularAudit deletedAuditEntry = elinksAudit.get(3);

        assertThat(deletedResponse).containsEntry("http_status", "200 OK");
        assertEquals("Deleted users Data Loaded Successfully", deletedProfiles.getMessage());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),deletedAuditEntry.getStatus());

        List<UserProfile> deletedUserProfile = profileRepository.findAll();
        assertEquals(2, deletedUserProfile.size());
        assertEquals("410540", deletedUserProfile.get(0).getPersonalCode());
        assertEquals(null, deletedUserProfile.get(0).getDeletedFlag());

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(3);
        assertThat(auditEntry.getId()).isPositive();
        assertEquals(DELETEDAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    private void validateLeaverApi(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        ElinkLeaversWrapperResponse leaversProfiles = (ElinkLeaversWrapperResponse) leaversResponse.get("body");
        ElinkDataSchedularAudit leaversAuditEntry = elinksAudit.get(2);

        assertThat(leaversResponse).containsEntry("http_status", "200 OK");
        assertEquals("Leavers Data Loaded Successfully", leaversProfiles.getMessage());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),leaversAuditEntry.getStatus());

        List<UserProfile> leaverUserProfile = profileRepository.findAll();
        assertEquals(2, leaverUserProfile.size());
        assertEquals("410551", leaverUserProfile.get(1).getPersonalCode());
        assertEquals(false, leaverUserProfile.get(1).getActiveFlag());
        assertEquals("c38f7bdc-e52b-4711-90e6-9d49a2bb38f2", leaverUserProfile.get(1).getObjectId());
        assertEquals("2023-03-01",leaverUserProfile.get(1).getLastWorkingDate().toString());
        assertNotNull(leaverUserProfile.get(1).getLastLoadedDate());


        ElinkDataSchedularAudit auditEntry = elinksAudit.get(2);
        assertThat(auditEntry.getId()).isPositive();
        assertEquals(LEAVERSAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    private void validateSidamPublish() {
        Map<String, Object> idamResponses = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals("200 OK",idamResponses.get("http_status"));
        List<IdamResponse> idamResponseVal = (ArrayList<IdamResponse>) idamResponses.get("body");
        assertEquals(2,idamResponseVal.size());

        List<UserProfile> userprofileAfterSidamresponse = profileRepository.findAll();
        UserProfile sidamID = userprofileAfterSidamresponse.get(0);

        assertEquals(2, userprofileAfterSidamresponse.size());
        assertEquals("94772643-2c5f-4f84-8731-3dd7c25c9e11",
            userprofileAfterSidamresponse.get(1).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e971",
            userprofileAfterSidamresponse.get(1).getSidamId());

        // asserting SIDAM publishing
        Map<String, Object> idamResponse = elinksReferenceDataClient.publishSidamIds();
        doNothing().when(elinkTopicPublisher).sendMessage(anyList(),anyString());;
        assertThat(idamResponse).containsEntry("http_status", "200 OK");
        HashMap publishSidamIdsResponse = (LinkedHashMap)idamResponse.get("body");

        assertThat(publishSidamIdsResponse.get("publishing_status")).isNotNull();

        List<ElinkDataExceptionRecords> elinksException = elinkDataExceptionRepository.findAll();
        assertEquals(22,elinksException.size());
    }

    private void validateBaseLocationApi(List<ElinkDataSchedularAudit> elinksAudit) {


        List<BaseLocation> baseLocationList = baseLocationRepository.findAll();
        assertEquals(1980, baseLocationList.size());
        assertEquals("Aberconwy",baseLocationList.get(0).getName());
        assertEquals("1",baseLocationList.get(0).getBaseLocationId());
        assertEquals("1722",baseLocationList.get(0).getParentId());

    }

    private void validateLocationApi(List<ElinkDataSchedularAudit> elinksAudit) {
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
    }

    private void validatePeopleApi(List<ElinkDataSchedularAudit> elinksAudit) {
        Map<String, Object> peopleResponse = elinksReferenceDataClient.getPeoples();
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse) peopleResponse.get("body");
        ElinkDataSchedularAudit peopleAuditEntry = elinksAudit.get(1);

        assertThat(peopleResponse).containsEntry("http_status", "200 OK");
        assertEquals("People data loaded successfully", profiles.getMessage());
        assertEquals(PEOPLEAPI,peopleAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS.getStatus(), peopleAuditEntry.getStatus());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(2, userprofile.size());
        assertEquals("410551", userprofile.get(0).getPersonalCode());
        assertEquals("Leslie", userprofile.get(0).getKnownAs());
        assertEquals("Jones", userprofile.get(0).getSurname());
        assertEquals("His Honour Judge Leslie Jones", userprofile.get(0).getFullName());
        assertEquals(null, userprofile.get(0).getPostNominals());
        assertEquals("HHJ.Leslie.Jones@judiciarystagingtest999.onmicrosoft.com",
            userprofile.get(0).getEmailId());
        assertEquals("c38f7bdc-e52b-4711-90e6-9d49a2bb38f2", userprofile.get(0).getObjectId());
        assertNull(userprofile.get(0).getSidamId());
        assertEquals("L.J",userprofile.get(0).getInitials());

        //asserting Judiciary additonal roles data
        List<JudicialRoleType> roleRequest = judicialRoleTypeRepository.findAll();
        assertEquals(0, roleRequest.size());


    }


    private void validateElasticSearchApi(List<DataloadSchedulerJob> audits) {
        Map<String, Object> idamResponses = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals("200 OK",idamResponses.get("http_status"));
        List<IdamResponse> idamResponseVal = (ArrayList<IdamResponse>) idamResponses.get("body");
        assertEquals(2,idamResponseVal.size());

        List<UserProfile> userprofileAfterSidamresponse = profileRepository.findAll();
        UserProfile sidamID = userprofileAfterSidamresponse.get(0);

        assertEquals(2, userprofileAfterSidamresponse.size());
        assertEquals("94772643-2c5f-4f84-8731-3dd7c25c9e11",
            userprofileAfterSidamresponse.get(1).getObjectId());
        assertEquals("6455c84c-e77d-4c4f-9759-bf4a93a8e971",
            userprofileAfterSidamresponse.get(1).getSidamId());

        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), audits.get(0).getPublishingStatus());
    }

    @BeforeAll
    private void setUpPeopleResponse() throws IOException {

        String peopleResponseValidationJson =
                loadJson("src/integrationTest/resources"
                        + "/wiremock_responses/extrabaselocation-id-for-failure-people.json");
        String locationResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/test_loc.json");
        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", V2.MediaType.SERVICE)
                .withHeader("Connection", "close")
                .withBody(locationResponseValidationJson)));
        elinks.stubFor(get(urlPathMatching("/people"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(peopleResponseValidationJson)));
    }

    private void idamSetUp() {
        initialize();
    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
        dataloadSchedulerJobRepository.deleteAll();
        authorisationsRepository.deleteAll();
        appointmentsRepository.deleteAll();
        baseLocationRepository.deleteAll();
        profileRepository.deleteAll();
        elinkDataExceptionRepository.deleteAll();
        judicialRoleTypeRepository.deleteAll();
    }


}
