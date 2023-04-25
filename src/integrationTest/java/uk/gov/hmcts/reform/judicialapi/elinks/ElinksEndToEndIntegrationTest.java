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
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.DataloadSchedulerJobAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASELOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;

public class ElinksEndToEndIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    AppointmentsRepository appointmentsRepository;

    @Autowired
    AuthorisationsRepository authorisationsRepository;
    @Autowired
    BaseLocationRepository baseLocationRepository;


    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @Autowired
    private ElinksApiJobScheduler elinksApiJobScheduler;

    @Autowired
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;

    @Autowired
    private DataloadSchedulerJobAudit dataloadSchedulerJobAudit;

    @Autowired
    private ElinkDataExceptionRepository elinkDataExceptionRepository;

    @Autowired
    PublishSidamIdService publishSidamIdService;

    @MockBean
    ElinkTopicPublisher elinkTopicPublisher;

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
    void test_elinks_end_to_end_success_scenario_with_return_status_200()
            throws JOSEException, JsonProcessingException, InterruptedException {

        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled", true);
        ReflectionTestUtils.setField(publishSidamIdService, "elinkTopicPublisher", elinkTopicPublisher);

        dataloadSchedulerJobRepository.deleteAll();
        elinksApiJobScheduler.loadElinksJob();

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();
        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();
        DataloadSchedulerJob jobDetails = audits.get(0);

        //assserting scheduler data
        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isNotNull();

        // asserting location data
        Map<String, Object> locationResponse = elinksReferenceDataClient.getLocations();
        ElinkLocationWrapperResponse locations = (ElinkLocationWrapperResponse) locationResponse.get("body");
        ElinkDataSchedularAudit locationAuditEntry = elinksAudit.get(0);

        assertThat(locationResponse).containsEntry("http_status", "200 OK");
        assertEquals(LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());
        assertEquals(LOCATIONAPI,locationAuditEntry.getApiName());

        List<Location> locationsList = locationRepository.findAll();
        assertEquals(35, locationsList.size());
        assertEquals("1", locationsList.get(1).getRegionId());
        assertEquals("National", locationsList.get(1).getRegionDescEn());


        //asserting baselocation data
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        ElinkBaseLocationWrapperResponse baseLocations =
                (ElinkBaseLocationWrapperResponse) baseLocationResponse.get("body");
        ElinkDataSchedularAudit baseLocationAuditEntry = elinksAudit.get(1);

        assertThat(baseLocationResponse).containsEntry("http_status", "200 OK");
        assertEquals(BASE_LOCATION_DATA_LOAD_SUCCESS, baseLocations.getMessage());
        assertEquals(BASELOCATIONAPI, baseLocationAuditEntry.getApiName());

        List<BaseLocation> baseLocationList = baseLocationRepository.findAll();
        assertEquals(6, baseLocationList.size());
        assertEquals("Aberconwy",baseLocationList.get(0).getCourtName());
        assertEquals("1",baseLocationList.get(0).getBaseLocationId());
        assertEquals("Old Gwynedd",baseLocationList.get(0).getCourtType());

        //asserting people data
        Map<String, Object> peopleResponse = elinksReferenceDataClient.getPeoples();
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse) peopleResponse.get("body");
        ElinkDataSchedularAudit peopleAuditEntry = elinksAudit.get(2);

        assertThat(peopleResponse).containsEntry("http_status", "200 OK");
        assertEquals("People data loaded successfully", profiles.getMessage());
        assertEquals(PEOPLEAPI,peopleAuditEntry.getApiName());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(2, userprofile.size());
        assertEquals("410540", userprofile.get(0).getPersonalCode());
        assertEquals("Yuriko", userprofile.get(0).getKnownAs());
        assertEquals("Koiko", userprofile.get(0).getSurname());
        assertEquals("Her Honour Judge Yuriko Koiko", userprofile.get(0).getFullName());
        assertEquals(null, userprofile.get(0).getPostNominals());
        assertEquals("HHJ.Yuriko.Koiko@judiciarystaging13232.onmicrosoft.com",
                userprofile.get(0).getEjudiciaryEmailId());
        assertNull(userprofile.get(0).getLastWorkingDate());
        assertEquals("94772643-2c5f-4f84-8731-3dd7c25c9e11", userprofile.get(0).getObjectId());
        assertEquals("B.K",userprofile.get(0).getInitials());
        assertEquals("c38f7bdc-e52b-4711-90e6-9d49a2bb38f2", userprofile.get(1).getObjectId());

        //asserting userprofile data for leaver api
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        ElinkLeaversWrapperResponse leaversProfiles = (ElinkLeaversWrapperResponse) leaversResponse.get("body");

        assertThat(leaversResponse).containsEntry("http_status", "200 OK");
        assertEquals("Leavers Data Loaded Successfully", leaversProfiles.getMessage());

        List<UserProfile> leaverUserProfile = profileRepository.findAll();
        assertEquals(2, leaverUserProfile.size());
        assertEquals("410540", leaverUserProfile.get(0).getPersonalCode());
        assertEquals(true, leaverUserProfile.get(0).getActiveFlag());
        assertEquals("94772643-2c5f-4f84-8731-3dd7c25c9e11", leaverUserProfile.get(0).getObjectId());


        ElinkDataSchedularAudit auditEntry = elinksAudit.get(3);
        assertThat(auditEntry.getId()).isPositive();
        assertEquals(LEAVERSAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());

        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),audits.get(0).getPublishingStatus());

        // asserting SIDAM publishing
        Map<String, Object> idamResponse = elinksReferenceDataClient.publishSidamIds();
        doNothing().when(elinkTopicPublisher).sendMessage(anyList(),anyString());;
        assertThat(idamResponse).containsEntry("http_status", "200 OK");
        HashMap publishSidamIdsResponse = (LinkedHashMap)idamResponse.get("body");

        assertThat(publishSidamIdsResponse.get("publishing_status")).isNotNull();
    }





    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
        elinkDataExceptionRepository.deleteAll();
    }
}
