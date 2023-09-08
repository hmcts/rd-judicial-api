package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.Assert;
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
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLocationWrapperResponse;
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
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;

class ElinksEndToEndIntegrationForSidamIdisPresent extends ElinksEnabledIntegrationTest {

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
            loadJson("src/integrationTest/resources/wiremock_responses/testpeople_withoutsidam.json");

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
    }

    @BeforeEach
    void before() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks end to test sidam is not updated")
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
        Assert.assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),jobDetails.getPublishingStatus());

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
        List<BaseLocation> baseLocationList = baseLocationRepository.findAll();
        assertEquals(12, baseLocationList.size());
        assertEquals("Aberconwy",baseLocationList.get(4).getName());
        assertEquals("1",baseLocationList.get(4).getBaseLocationId());
        assertEquals("1722",baseLocationList.get(4).getParentId());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(11, userprofile.size());
        assertEquals("123454", userprofile.get(10).getPersonalCode());
        assertEquals("Rachel", userprofile.get(10).getKnownAs());
        assertEquals("Jones", userprofile.get(10).getSurname());
        assertEquals("District Judge Rachel Jones", userprofile.get(10).getFullName());
        assertEquals(null, userprofile.get(10).getPostNominals());
        assertEquals("DJ.Rachel.Jones@ejudiciary.net",
            userprofile.get(10).getEjudiciaryEmailId());
        assertTrue(userprofile.get(10).getActiveFlag());
        assertEquals("5f8b26ba-0c8b-4192-b5c7-311d737f0cae", userprofile.get(10).getObjectId());
        assertEquals("3333333",userprofile.get(10).getSidamId());
        assertEquals("RJ",userprofile.get(10).getInitials());



        //asserting userprofile data for leaver api

        //asserting userprofile data for deleted api

    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
        dataloadSchedulerJobRepository.deleteAll();
    }
}
