package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.TestDataArguments;

import java.io.IOException;
import java.util.List;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PUBLISHSIDAM;

class PeopleIntegrationTest extends ElinksDataLoadBaseTest {

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(elinksApiJobScheduler, "isSchedulerEnabled", true);
        deleteData();
    }

    @DisplayName("Success - ELinks People Api Data Load Success Scenarios")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDataForPeopleApi")
    void shouldLoadPeopleApiData(TestDataArguments testDataArguments) throws Exception {

        final String peopleApiResponseJson = readJsonAsString(testDataArguments.eLinksPeopleApiResponseJson());
        final String locationApiResponseJson = readJsonAsString(testDataArguments.eLinksLocationApiResponseJson());

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        verifySavedOriginalELinksResponses();

        verifyUserProfileData(testDataArguments);

        verifyUserAppointmentsData(testDataArguments);

        verifyUserAuthorisationsData(testDataArguments);

        verifyUserJudiciaryRolesData(testDataArguments.expectedRoleSize());

        verifyPeopleDataLoadAudit(testDataArguments.expectedJobStatus(), testDataArguments.expectedAuditRecords());

        verifyExceptions(testDataArguments);
    }

    @DisplayName("Success - ELinks People Api Data Load and Delete Success Scenarios")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDataForPeopleLoadAndDeleteApi")
    void shouldLoadPeopleApiDataAndDelete(TestDataArguments testDataArguments) throws Exception {

        final String peopleApiResponseJson = readJsonAsString(testDataArguments.eLinksPeopleApiResponseJson());
        final String locationApiResponseJson = readJsonAsString(testDataArguments.eLinksLocationApiResponseJson());

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        verifySavedOriginalELinksResponses();

        verifyUserProfileData(testDataArguments);

        verifyUserAppointmentsData(testDataArguments);

        verifyUserAuthorisationsData(testDataArguments);

        verifyUserJudiciaryRolesData(testDataArguments.expectedRoleSize());

        verifyPeopleDataLoadAudit(testDataArguments.expectedJobStatus());

        verifyExceptions(testDataArguments);
    }

    @DisplayName("Negative - ELinks People Api Data Load Failure Scenarios")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDataLoadFailStatusCodes")
    void shouldFailToLoadPeopleApiDataWhenELinksApiResponseNot200(TestDataArguments testDataArguments)
            throws IOException {
        final String peopleApiResponseJson = readJsonAsString(testDataArguments.eLinksPeopleApiResponseJson());
        final String locationApiResponseJson = readJsonAsString(testDataArguments.eLinksLocationApiResponseJson());

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, testDataArguments.httpStatus());

        final HttpStatus expectedHttpStatus = testDataArguments.httpStatus() == SERVICE_UNAVAILABLE ? FORBIDDEN :
                testDataArguments.httpStatus();

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(expectedHttpStatus, RESPONSE_BODY_ERROR_MSG, testDataArguments.expectedErrorMessage());

        verifyPeopleDataLoadAudit(testDataArguments.expectedJobStatus(), 2);
    }

    private void verifyExceptions(TestDataArguments testDataArguments) {
        final var exceptionRecords = elinkDataExceptionRepository.findAll();

        final int expectedExceptionSize = testDataArguments.exceptionSize();

        if (expectedExceptionSize != 0) {
            assertThat(exceptionRecords).isNotNull().hasSize(expectedExceptionSize);
            assertThat(exceptionRecords.get(0).getErrorDescription())
                    .isNotNull()
                    .isEqualTo(testDataArguments.errorMsg1());
            if (expectedExceptionSize == 2) {
                assertThat(exceptionRecords.get(1).getErrorDescription())
                        .isNotNull().isEqualTo(testDataArguments.errorMsg2());
            }
        } else {
            assertThat(exceptionRecords).isEmpty();
        }
    }

    private void verifySavedOriginalELinksResponses() {

        final List<ElinksResponses> eLinksResponses =
                elinksResponsesRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinksResponses::getApiName))
                        .toList();
        assertThat(eLinksResponses).isNotNull().isNotEmpty().hasSize(2);

        ElinksResponses locationElinksResponses = eLinksResponses.get(0);
        ElinksResponses peopleElinksResponses = eLinksResponses.get(1);

        assertThat(locationElinksResponses).isNotNull();
        assertThat(peopleElinksResponses).isNotNull();

        assertThat(locationElinksResponses.getApiName()).isNotNull().isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(locationElinksResponses.getCreatedDate()).isNotNull();
        assertThat(locationElinksResponses.getElinksData()).isNotNull();

        assertThat(peopleElinksResponses.getApiName()).isNotNull().isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(peopleElinksResponses.getCreatedDate()).isNotNull();
        assertThat(peopleElinksResponses.getElinksData()).isNotNull();
    }

    private void verifyPeopleDataLoadAudit(JobStatus peopleLoadJobStatus) {
        verifyPeopleDataLoadAudit(peopleLoadJobStatus, 2);
    }

    private void verifyPeopleDataLoadAudit(JobStatus peopleLoadJobStatus,
                                           int expectedSize) {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits =
                elinkSchedularAuditRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinkDataSchedularAudit::getApiName))
                        .toList();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty().hasSize(expectedSize);

        final ElinkDataSchedularAudit auditEntry1 = eLinksDataSchedulerAudits.get(0);
        final ElinkDataSchedularAudit auditEntry2 = eLinksDataSchedulerAudits.get(1);
        assertThat(auditEntry1).isNotNull();
        assertThat(auditEntry2).isNotNull();

        assertThat(auditEntry1.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(auditEntry1.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry1.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry1.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry1.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry2.getApiName()).isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(auditEntry2.getStatus()).isNotNull().isEqualTo(peopleLoadJobStatus.getStatus());
        assertThat(auditEntry2.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry2.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry2.getSchedulerEndTime()).isNotNull();

        if (expectedSize > 2) {
            final ElinkDataSchedularAudit auditEntry3 = eLinksDataSchedulerAudits.get(2);
            assertThat(auditEntry3).isNotNull();
            assertThat(auditEntry3.getApiName()).isNotNull().isEqualTo(PUBLISHSIDAM);
            assertThat(auditEntry3.getStatus()).isNotNull().isEqualTo(peopleLoadJobStatus.getStatus());
            assertThat(auditEntry3.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
            assertThat(auditEntry3.getSchedulerStartTime()).isNotNull();
            assertThat(auditEntry3.getSchedulerEndTime()).isNotNull();
        }
    }


}
