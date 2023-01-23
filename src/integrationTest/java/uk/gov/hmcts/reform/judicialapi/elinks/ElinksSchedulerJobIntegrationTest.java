package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

public class ElinksSchedulerJobIntegrationTest extends ElinksEnabledIntegrationTest {


    @Autowired
    LocationRepository locationRepository;


    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @Autowired
    private ElinksApiJobScheduler elinksApiJobScheduler;

    @Autowired
    private DataloadSchedulerJobRepository dataloadSchedulerJobRepository;




    @DisplayName("Elinks load eLinks scheduler status verification success case")
    @Test
    void test_load_elinks_job_status_sucess() {

        elinksApiJobScheduler.loadElinksJob();

        DataloadSchedulerJob jobDetails = dataloadSchedulerJobRepository.findAll().get(0);

        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isEqualTo(RefDataElinksConstants.JobStatus.SUCCESS.getStatus());

    }

    @DisplayName("Elinks load eLinks scheduler status verification failure case")
    @Test
    void test_load_elinks_job_status_failure() {


        int statusCode = 400;
        locationApi4xxResponse(statusCode,null);

        elinksApiJobScheduler.loadElinksJob();

        DataloadSchedulerJob jobDetails = dataloadSchedulerJobRepository.findAll().get(0);

        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isEqualTo(RefDataElinksConstants.JobStatus.FAILED.getStatus());

    }

    private void locationApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)
                        .withTransformers("user-token-response")));
    }
}
