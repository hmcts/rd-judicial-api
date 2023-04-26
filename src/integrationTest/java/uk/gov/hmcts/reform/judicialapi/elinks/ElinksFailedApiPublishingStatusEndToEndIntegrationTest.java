package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.Location;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkBaseLocationWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkLeaversWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASELOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;

public class ElinksFailedApiPublishingStatusEndToEndIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    BaseLocationRepository baseLocationRepository;


    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @Autowired
    private ElinksApiJobScheduler elinksApiJobScheduler;

    @Autowired
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;

    @BeforeEach
    void setUp() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }


    @DisplayName("Elinks end to end success scenario for failed publishing status")
    @Test
    void test_end_to_end_load_elinks_job_status_failure()
            throws JOSEException, JsonProcessingException {
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled", true);

        dataloadSchedulerJobRepository.deleteAll();
        elinksApiJobScheduler.loadElinksJob();
        setupIdamStubs();

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();
        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();
        DataloadSchedulerJob jobDetails = audits.get(0);

        //assserting scheduler data
        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isNotNull();

        // asserting location data
        Map<String, Object> locationResponse = elinksReferenceDataClient.getLocations();
        ElinkDataSchedularAudit locationAuditEntry = elinksAudit.get(0);

        assertThat(locationResponse).containsEntry("http_status", "400");
        assertEquals(LOCATIONAPI,locationAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.FAILED.getStatus(), locationAuditEntry.getStatus());


        List<Location> locationsList = locationRepository.findAll();

        assertEquals(1, locationsList.size());
        assertEquals("0",locationsList.get(0).getRegionId());
        assertEquals("default",locationsList.get(0).getRegionDescCy());
        assertEquals("default",locationsList.get(0).getRegionDescEn());

        //asserting baselocation data
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        ElinkBaseLocationWrapperResponse baseLocations =
                (ElinkBaseLocationWrapperResponse) baseLocationResponse.get("body");
        ElinkDataSchedularAudit baseLocationAuditEntry = elinksAudit.get(1);

        assertThat(baseLocationResponse).containsEntry("http_status", "400");
        assertEquals(BASELOCATIONAPI, baseLocationAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.FAILED.getStatus(), baseLocationAuditEntry.getStatus());


        List<BaseLocation> baseLocationList = baseLocationRepository.findAll();
        assertEquals(0, baseLocationList.size());

        //asserting people data
        Map<String, Object> peopleResponse = elinksReferenceDataClient.getPeoples();
        ElinkPeopleWrapperResponse profiles = (ElinkPeopleWrapperResponse) peopleResponse.get("body");
        ElinkDataSchedularAudit peopleAuditEntry = elinksAudit.get(2);

        assertThat(peopleResponse).containsEntry("http_status", "400");
        assertEquals(PEOPLEAPI,peopleAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.FAILED.getStatus(), peopleAuditEntry.getStatus());

        List<UserProfile> userprofile = profileRepository.findAll();
        assertEquals(0, userprofile.size());

        //asserting userprofile data for leaver api
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        ElinkLeaversWrapperResponse leaversProfiles = (ElinkLeaversWrapperResponse) leaversResponse.get("body");
        ElinkDataSchedularAudit leaversAuditEntry = elinksAudit.get(3);

        assertThat(leaversResponse).containsEntry("http_status", "400");
        assertEquals(LEAVERSAPI,leaversAuditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.FAILED.getStatus(), leaversAuditEntry.getStatus());

        List<UserProfile> leaverUserProfile = profileRepository.findAll();
        assertEquals(0, leaverUserProfile.size());

    }


    @BeforeAll
    public void setupIdamStubs() {

        String body = null;
        int statusCode = 400;

        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close").withBody(body)));


        elinks.stubFor(get(urlPathMatching("/reference_data/base_location"))
                .atPriority(2)
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)
                ));


        elinks.stubFor(get(urlPathMatching("/people"))
                .atPriority(3)
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)
                ));

        elinks.stubFor(get(urlPathMatching("/leavers"))
                .atPriority(4)
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)
                ));
    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
        dataloadSchedulerJobRepository.deleteAll();
    }
}
