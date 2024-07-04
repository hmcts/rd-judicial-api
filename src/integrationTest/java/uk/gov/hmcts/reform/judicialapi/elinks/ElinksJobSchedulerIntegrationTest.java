package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.elinks.util.TestDataArguments;

import java.io.IOException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

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

}
