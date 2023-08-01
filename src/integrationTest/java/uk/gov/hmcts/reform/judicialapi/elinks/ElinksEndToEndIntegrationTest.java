package uk.gov.hmcts.reform.judicialapi.elinks;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.service.PublishSidamIdService;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;

public class ElinksEndToEndIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    JudicialRoleTypeRepository judicialRoleTypeRepository;
    @Autowired
    BaseLocationRepository baseLocationRepository;
    @Autowired
    AuthorisationsRepository authorisationsRepository;
    @Autowired
    AppointmentsRepository appointmentsRepository;
    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;


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



    @BeforeAll
    void loadElinksResponse() throws Exception {

        cleanupData();

        String locationResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/location.json");
        String baselocationResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/base_location.json");
        String peopleResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/people.json");
        String leaversResponseValidationJson =
            loadJson("src/integrationTest/resources/wiremock_responses/leavers.json");

        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", V2.MediaType.SERVICE)
                .withHeader("Connection", "close")
                .withBody(locationResponseValidationJson)));

        elinks.stubFor(get(urlPathMatching("/reference_data/base_location"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", V2.MediaType.SERVICE)
                .withHeader("Connection", "close")
                .withBody(baselocationResponseValidationJson)
                .withTransformers("user-token-response")));

        elinks.stubFor(get(urlPathMatching("/people"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", V2.MediaType.SERVICE)
                .withHeader("Connection", "close")
                .withBody(peopleResponseValidationJson)));

        elinks.stubFor(get(urlPathMatching("/leavers"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", V2.MediaType.SERVICE)
                .withHeader("Connection", "close")
                .withBody(leaversResponseValidationJson)));
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks end to end success scenario")
    @Test
    void test_elinks_end_to_end_success_scenario_with_return_status_200()
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

        /*assertThat(locationResponse).containsEntry("http_status", "200 OK");
        assertEquals(LOCATION_DATA_LOAD_SUCCESS, locations.getMessage());*/
        assertEquals(LOCATIONAPI,locationAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), locationAuditEntry.getStatus());


        List<Location> locationsList = locationRepository.findAll();
        assertEquals(11, locationsList.size());
        assertEquals("1", locationsList.get(1).getRegionId());
        assertEquals("London", locationsList.get(1).getRegionDescEn());


        //asserting baselocation data
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

        //asserting people data
        Map<String, Object> peopleResponse = elinksReferenceDataClient.getPeoples();
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse) peopleResponse.get("body");
        ElinkDataSchedularAudit peopleAuditEntry = elinksAudit.get(1);

        assertThat(peopleResponse).containsEntry("http_status", "200 OK");
        assertEquals("People data loaded successfully", profiles.getMessage());
        assertEquals(PEOPLEAPI,peopleAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(), peopleAuditEntry.getStatus());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(1, userprofile.size());
        assertEquals("4913085", userprofile.get(0).getPersonalCode());
        assertEquals("Rachel", userprofile.get(0).getKnownAs());
        assertEquals("Jones", userprofile.get(0).getSurname());
        assertEquals("District Judge Rachel Jones", userprofile.get(0).getFullName());
        assertEquals(null, userprofile.get(0).getPostNominals());
        assertEquals("DJ.Rachel.Jones@ejudiciary.net",
                userprofile.get(0).getEjudiciaryEmailId());
        assertTrue(userprofile.get(0).getActiveFlag());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", userprofile.get(0).getObjectId());
        assertNull(userprofile.get(0).getSidamId());
        assertEquals("RJ",userprofile.get(0).getInitials());


        //asserting userprofile data for leaver api
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        ElinkLeaversWrapperResponse leaversProfiles = (ElinkLeaversWrapperResponse) leaversResponse.get("body");
        ElinkDataSchedularAudit leaversAuditEntry = elinksAudit.get(2);

        assertThat(leaversResponse).containsEntry("http_status", "200 OK");
        assertEquals("Leavers Data Loaded Successfully", leaversProfiles.getMessage());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),leaversAuditEntry.getStatus());

        List<UserProfile> leaverUserProfile = profileRepository.findAll();
        assertEquals(1, leaverUserProfile.size());
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

        //assert elastic search api
        idamSetUp();

        List<ElinkDataExceptionRecords> elinksException = elinkDataExceptionRepository.findAll();
        assertThat(elinksException.size()).isEqualTo(0);

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

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
        authorisationsRepository.deleteAll();
        appointmentsRepository.deleteAll();
        judicialRoleTypeRepository.deleteAll();
        profileRepository.deleteAll();
        dataloadSchedulerJobRepository.deleteAll();
    }
}
