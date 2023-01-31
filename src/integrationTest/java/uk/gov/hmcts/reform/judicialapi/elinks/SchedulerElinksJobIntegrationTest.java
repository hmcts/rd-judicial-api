package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

class SchedulerElinksJobIntegrationTest extends ElinksEnabledIntegrationTest {


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

    @BeforeEach
    void setUp() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks load eLinks scheduler status verification success case")
    @Test
    @Order(1)
    void test_load_elinks_job_status_sucess() throws JOSEException, JsonProcessingException {

        dataloadSchedulerJobRepository.deleteAll();

        elinksApiJobScheduler.loadElinksJob();
        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();

        assertThat(audits).isEmpty();

    }


    @DisplayName("Elinks load eLinks scheduler status verification failure case")
    @Test
    @Order(2)
    void test_load_elinks_job_status_failure() {

        dataloadSchedulerJobRepository.deleteAll();

        int statusCode = 400;
        String body = null;
        locationApi4xxResponse(statusCode,body);

        elinksApiJobScheduler.loadElinksJob();

        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();

        assertThat(audits).isEmpty();
    }



    private void locationApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)
                ));
    }

    private void cleanupData() {
        elinkSchedularAuditRepository.deleteAll();
    }
}
