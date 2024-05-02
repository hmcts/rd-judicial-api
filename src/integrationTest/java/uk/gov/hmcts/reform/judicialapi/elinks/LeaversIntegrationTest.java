package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus;
import uk.gov.hmcts.reform.judicialapi.elinks.util.TestDataArguments;

import java.io.IOException;
import java.util.List;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSSUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;


class LeaversIntegrationTest extends ElinksDataLoadBaseTest {

    @BeforeEach
    void setUp() {
        deleteData();
    }

    @DisplayName("Success - ELinks Leavers Api Data Load Success Scenario")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDataForLeaversApi")
    void shouldLoadLeaverApiData(TestDataArguments testDataArguments) throws Exception {

        final String locationApiResponseJson = readJsonAsString(testDataArguments.eLinksLocationApiResponseJson());
        final String peopleApiResponseJson = readJsonAsString(testDataArguments.eLinksPeopleApiResponseJson());
        final String leaversApiResponseJson = readJsonAsString(testDataArguments.eLinksLeaversApiResponseJson());

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubLeaversApiResponse(leaversApiResponseJson, OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);

        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        loadLeaversData(OK, RESPONSE_BODY_MSG_KEY, LEAVERSSUCCESS);

        verifySavedOriginalELinksResponse();

        verifyUserProfileData(testDataArguments);

        verifyUserAppointmentsData(testDataArguments);

        verifyUserAuthorisationsData(testDataArguments);

        verifyUserJudiciaryRolesData(testDataArguments.expectedRoleSize());

        verifyLeaversDataLoadAudit(testDataArguments.expectedJobStatus());
    }

    @DisplayName("Negative - ELinks Leavers Api Data Load Failure Scenarios")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDataLoadFailStatusCodes")
    void shouldFailToLoadLeaversApiDataWhenELinksApiResponseNot200(TestDataArguments testDataArguments)
            throws IOException {
        final String locationApiResponseJson = readJsonAsString(testDataArguments.eLinksLocationApiResponseJson());
        final String peopleApiResponseJson = readJsonAsString(testDataArguments.eLinksPeopleApiResponseJson());

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubLeaversApiResponse(null, testDataArguments.httpStatus());

        final HttpStatus expectedHttpStatus = testDataArguments.httpStatus() == SERVICE_UNAVAILABLE ? FORBIDDEN :
                testDataArguments.httpStatus();

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);

        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        loadLeaversData(expectedHttpStatus, RESPONSE_BODY_ERROR_MSG, testDataArguments.expectedErrorMessage());

        verifyLeaversDataLoadAudit(testDataArguments.expectedJobStatus());
    }

    private void verifySavedOriginalELinksResponse() {

        final List<ElinksResponses> eLinksResponses =
                elinksResponsesRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinksResponses::getApiName))
                        .toList();

        assertThat(eLinksResponses).isNotNull().isNotEmpty().hasSize(3);


        ElinksResponses leaversElinksResponses = eLinksResponses.get(0);
        ElinksResponses locationElinksResponses = eLinksResponses.get(1);
        ElinksResponses peopleElinksResponses = eLinksResponses.get(2);

        assertThat(leaversElinksResponses).isNotNull();
        assertThat(locationElinksResponses).isNotNull();
        assertThat(peopleElinksResponses).isNotNull();

        assertThat(leaversElinksResponses.getApiName()).isNotNull().isEqualTo(LEAVERSAPI);
        assertThat(leaversElinksResponses.getCreatedDate()).isNotNull();
        assertThat(leaversElinksResponses.getElinksData()).isNotNull();

        assertThat(locationElinksResponses.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(locationElinksResponses.getCreatedDate()).isNotNull();
        assertThat(locationElinksResponses.getElinksData()).isNotNull();

        assertThat(peopleElinksResponses.getApiName()).isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(peopleElinksResponses.getCreatedDate()).isNotNull();
        assertThat(peopleElinksResponses.getElinksData()).isNotNull();
    }

    private void verifyLeaversDataLoadAudit(JobStatus expectedLeaversLoadJobStatus) {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits =
                elinkSchedularAuditRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinkDataSchedularAudit::getApiName))
                        .toList();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty().hasSize(3);

        final ElinkDataSchedularAudit auditEntry1 = eLinksDataSchedulerAudits.get(0);
        final ElinkDataSchedularAudit auditEntry2 = eLinksDataSchedulerAudits.get(1);
        final ElinkDataSchedularAudit auditEntry3 = eLinksDataSchedulerAudits.get(2);

        assertThat(auditEntry1).isNotNull();
        assertThat(auditEntry2).isNotNull();

        assertThat(auditEntry1.getApiName()).isNotNull().isEqualTo(LEAVERSAPI);
        assertThat(auditEntry1.getStatus()).isNotNull().isEqualTo(expectedLeaversLoadJobStatus.getStatus());
        assertThat(auditEntry1.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry1.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry1.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry2.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(auditEntry2.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry2.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry2.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry2.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry3.getApiName()).isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(auditEntry3.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry3.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry3.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry3.getSchedulerEndTime()).isNotNull();
    }

}
