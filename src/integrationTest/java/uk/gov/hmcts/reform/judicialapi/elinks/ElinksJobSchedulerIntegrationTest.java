package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.elinks.util.TestDataArguments;

import java.io.IOException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELASTICSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PUBLISHSIDAM;

public class ElinksJobSchedulerIntegrationTest extends ElinksDataLoadBaseTest {

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled", true);
        deleteData();
    }

    @DisplayName("Should run scheduler job and load elinks api's data")
    @Test
    void shouldRunSchedulerJobAndLoadAllElinksApiData() throws IOException {
        ReflectionTestUtils.setField(publishSidamIdService, "elinkTopicPublisher", elinkTopicPublisher);
        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);
        willDoNothing().given(elinkTopicPublisher).sendMessage(anyList(), anyString());

        stubLocationApiResponse(readJsonAsString(LOCATION_API_RESPONSE_JSON), OK);
        stubPeopleApiResponse(readJsonAsString(PEOPLE_API_RESPONSE_JSON), OK);
        stubLeaversApiResponse(readJsonAsString(LEAVERS_API_RESPONSE_JSON), OK);
        stubDeletedApiResponse(readJsonAsString(DELETED_API_RESPONSE_JSON), OK);
        stubIdamResponse(readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_JSON), OK);
        stubIdamTokenResponse(OK);

        runElinksDataLoadJob();

        final TestDataArguments testDataArguments = getTestDataArguments();

        verifyLocationData();

        verifyUserProfileData(testDataArguments);

        verifyUserAppointmentsData(testDataArguments);

        verifyUserAuthorisationsData(testDataArguments);

        verifyUserJudiciaryRolesData(testDataArguments.expectedRoleSize());

        verifyAudit();

        verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
    }

    @DisplayName("should Fail To Run Elinks Job When Job Already Run")
    @Test
    void shouldFailToRunElinksJobWhenJobAlreadyRun() {

        DataloadSchedulerJob dataloadSchedulerJob = new DataloadSchedulerJob();
        dataloadSchedulerJob.setJobStartTime(now());
        dataloadSchedulerJob.setJobEndTime(now());
        dataloadSchedulerJob.setPublishingStatus(RefDataElinksConstants.JobStatus.SUCCESS.getStatus());
        dataloadSchedulerJobAudit.auditSchedulerJobStatus(dataloadSchedulerJob);

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

    private void verifyAudit() {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits =
                elinkSchedularAuditRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinkDataSchedularAudit::getApiName))
                        .toList();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty().hasSize(7);

        final ElinkDataSchedularAudit auditEntry1 = eLinksDataSchedulerAudits.get(0);
        final ElinkDataSchedularAudit auditEntry2 = eLinksDataSchedulerAudits.get(1);
        final ElinkDataSchedularAudit auditEntry3 = eLinksDataSchedulerAudits.get(2);
        final ElinkDataSchedularAudit auditEntry4 = eLinksDataSchedulerAudits.get(3);
        final ElinkDataSchedularAudit auditEntry5 = eLinksDataSchedulerAudits.get(4);
        final ElinkDataSchedularAudit auditEntry6 = eLinksDataSchedulerAudits.get(5);
        final ElinkDataSchedularAudit auditEntry7 = eLinksDataSchedulerAudits.get(6);

        assertThat(auditEntry1).isNotNull();
        assertThat(auditEntry2).isNotNull();
        assertThat(auditEntry3).isNotNull();
        assertThat(auditEntry4).isNotNull();

        assertThat(auditEntry1.getApiName()).isNotNull().isEqualTo(DELETEDAPI);
        assertThat(auditEntry1.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry1.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry1.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry1.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry2.getApiName()).isNotNull().isEqualTo(ELASTICSEARCH);
        assertThat(auditEntry2.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry2.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry2.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry2.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry3.getApiName()).isNotNull().isEqualTo(IDAMSEARCH);
        assertThat(auditEntry3.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry3.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry3.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry3.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry4.getApiName()).isNotNull().isEqualTo(LEAVERSAPI);
        assertThat(auditEntry4.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry4.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry4.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry4.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry5.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(auditEntry5.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry5.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry5.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry5.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry6.getApiName()).isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(auditEntry6.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry6.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry6.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry6.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry7.getApiName()).isNotNull().isEqualTo(PUBLISHSIDAM);
        assertThat(auditEntry7.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry7.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry7.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry7.getSchedulerEndTime()).isNotNull();
    }

    private void runElinksDataLoadJob() {
        elinksApiJobScheduler.loadElinksJob();
        List<DataloadSchedulerJob> audits = dataloadSchedulerJobRepository.findAll();
        DataloadSchedulerJob jobDetails = audits.get(0);

        assertThat(jobDetails).isNotNull();
        assertThat(jobDetails.getPublishingStatus()).isNotNull();
        assertEquals(SUCCESS.getStatus(), jobDetails.getPublishingStatus());
    }

    private TestDataArguments getTestDataArguments() {
        return
                TestDataArguments.builder()
                        .expectedAppointmentsSize(4)
                        .expectedAuthorisationSize(4)
                        .expectedRoleSize(2)
                        .expectedUserProfiles(2)
                        .expectedActiveFlag(false)
                        .expectedDeletedFlag(true)
                        .expectedDeletedOnDate("2022-07-10")
                        .expectedLastWorkingDate("2023-03-01")
                        .expectedJobStatus(SUCCESS)
                        .isAfterIdamElasticSearch(true)
                        .elasticSearchSidamId("6455c84c-e77d-4c4f-9759-bf4a93a8e972")
                        .build();
    }

}
