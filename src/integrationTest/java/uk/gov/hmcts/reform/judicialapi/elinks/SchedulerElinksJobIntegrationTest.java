package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.DataloadSchedulerJobRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.LocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.scheduler.ElinksApiJobScheduler;
import uk.gov.hmcts.reform.judicialapi.elinks.util.DataloadSchedulerJobAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

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

    @Autowired
    private DataloadSchedulerJobAudit dataloadSchedulerJobAudit;

    @Autowired
    private ElinkDataExceptionRepository elinkDataExceptionRepository;


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

        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled",
                true);

        dataloadSchedulerJobRepository.deleteAll();

        elinksApiJobScheduler.loadElinksJob();
        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();

        DataloadSchedulerJob jobDetails = audits.get(0);

        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isNotNull();

    }


    @DisplayName("Elinks load eLinks scheduler status verification failure case")
    @Test
    @Order(2)
    void test_load_elinks_job_status_failure() {

        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled",
                true);

        dataloadSchedulerJobRepository.deleteAll();

        int statusCode = 400;
        String body = null;
        locationApi4xxResponse(statusCode,body);

        elinksApiJobScheduler.loadElinksJob();

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();
        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();
        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);
        DataloadSchedulerJob jobDetails = audits.get(0);

        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isNotNull();
        assertEquals(RefDataElinksConstants.JobStatus.FAILED.getStatus(), auditEntry.getStatus());
        assertEquals(RefDataElinksConstants.JobStatus.SUCCESS.getStatus(),audits.get(0).getPublishingStatus());

    }


    @DisplayName("Elinks load eLinks scheduler status verification failure case  for job ran already")
    @Test
    @Order(3)
    void test_load_elinks_job_status_failure_job_ran_already() {

        DataloadSchedulerJob audit = new DataloadSchedulerJob();
        audit.setJobStartTime(now());
        audit.setJobEndTime(now());
        audit.setPublishingStatus(RefDataElinksConstants.JobStatus.SUCCESS.getStatus());
        dataloadSchedulerJobAudit.auditSchedulerJobStatus(audit);

        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled",
                true);
        elinkDataExceptionRepository.deleteAll();

        elinksApiJobScheduler.loadElinksJob();

        List<ElinkDataExceptionRecords> exceptions = elinkDataExceptionRepository.findAll();
        ElinkDataExceptionRecords exception = exceptions.get(0);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrorDescription())
                .isEqualToIgnoringCase("JRD load failed since job has already ran for the day");

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
        elinkDataExceptionRepository.deleteAll();
        dataloadSchedulerJobRepository.deleteAll();
        authorisationsRepository.deleteAll();
    }
}
