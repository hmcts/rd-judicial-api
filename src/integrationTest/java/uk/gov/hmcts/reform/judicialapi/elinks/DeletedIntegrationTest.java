package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.AppointmentAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.AuthorisationAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.JudicialRoleTypeAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.audit.UserProfileAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;
import uk.gov.hmcts.reform.judicialapi.elinks.util.TestDataArguments;

import java.io.IOException;
import java.util.List;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDSUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;


class DeletedIntegrationTest extends ElinksDataLoadBaseTest {

    @BeforeEach
    void setUp() {
        deleteData();
    }

    @DisplayName("Success - ELinks Deleted Api Data Load Success Scenario")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDataForDeletedApi")
    void shouldLoadDeletedApiData(TestDataArguments testDataArguments) throws Exception {

        final String locationApiResponseJson = readJsonAsString(testDataArguments.eLinksLocationApiResponseJson());
        final String peopleApiResponseJson = readJsonAsString(testDataArguments.eLinksPeopleApiResponseJson());
        final String deletedApiResponseJson = readJsonAsString(testDataArguments.eLinksDeletedApiResponseJson());

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubDeletedApiResponse(deletedApiResponseJson, OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);
        loadDeletedData(OK, RESPONSE_BODY_MSG_KEY, DELETEDSUCCESS);

        verifySavedOriginalELinksResponse();

        verifyUserProfileData(testDataArguments);

        verifyUserAppointmentsData(testDataArguments);

        verifyUserAuthorisationsData(testDataArguments);

        verifyUserJudiciaryRolesData(testDataArguments.expectedRoleSize());

        verifyDeletedDataLoadAudit(testDataArguments.expectedJobStatus(), 4);

        verifyDeletedPeopleAudit(testDataArguments);
    }

    @DisplayName("Negative - ELinks Deleted Api Data Load Failure Scenarios")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDataLoadFailStatusCodes")
    void shouldFailToLoadDeletedApiDataWhenELinksApiResponseNot200(TestDataArguments testDataArguments)
            throws IOException {
        final String locationApiResponseJson = readJsonAsString(testDataArguments.eLinksLocationApiResponseJson());
        final String peopleApiResponseJson = readJsonAsString(testDataArguments.eLinksPeopleApiResponseJson());

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubDeletedApiResponse(null, testDataArguments.httpStatus());

        final HttpStatus expectedHttpStatus = testDataArguments.httpStatus() == SERVICE_UNAVAILABLE ? FORBIDDEN :
                testDataArguments.httpStatus();

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);
        loadDeletedData(expectedHttpStatus, RESPONSE_BODY_ERROR_MSG, testDataArguments.expectedErrorMessage());

        verifyDeletedDataLoadAudit(testDataArguments.expectedJobStatus());
    }

    private void verifySavedOriginalELinksResponse() {

        final List<ElinksResponses> eLinksResponses =
                elinksResponsesRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinksResponses::getApiName))
                        .toList();

        assertThat(eLinksResponses).isNotNull().isNotEmpty().hasSize(3);


        ElinksResponses deletedElinksResponses = eLinksResponses.get(0);
        ElinksResponses locationElinksResponses = eLinksResponses.get(1);
        ElinksResponses peopleElinksResponses = eLinksResponses.get(2);

        assertThat(deletedElinksResponses).isNotNull();
        assertThat(locationElinksResponses).isNotNull();
        assertThat(peopleElinksResponses).isNotNull();

        assertThat(deletedElinksResponses.getApiName()).isNotNull().isEqualTo(DELETEDAPI);
        assertThat(deletedElinksResponses.getCreatedDate()).isNotNull();
        assertThat(deletedElinksResponses.getElinksData()).isNotNull();

        assertThat(locationElinksResponses.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(locationElinksResponses.getCreatedDate()).isNotNull();
        assertThat(locationElinksResponses.getElinksData()).isNotNull();

        assertThat(peopleElinksResponses.getApiName()).isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(peopleElinksResponses.getCreatedDate()).isNotNull();
        assertThat(peopleElinksResponses.getElinksData()).isNotNull();
    }

    private void verifyDeletedDataLoadAudit(RefDataElinksConstants.JobStatus expectedDeletedLoadJobStatus) {
        verifyDeletedDataLoadAudit(expectedDeletedLoadJobStatus, 2);
    }

    private void verifyDeletedDataLoadAudit(RefDataElinksConstants.JobStatus expectedDeletedLoadJobStatus,
                                            int expectedCount) {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits =
                elinkSchedularAuditRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinkDataSchedularAudit::getApiName))
                        .toList();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty().hasSize(expectedCount);

        final ElinkDataSchedularAudit auditEntry1 = eLinksDataSchedulerAudits.get(0);
        final ElinkDataSchedularAudit auditEntry2 = eLinksDataSchedulerAudits.get(1);
        final ElinkDataSchedularAudit auditEntry3 = eLinksDataSchedulerAudits.get(2);

        assertThat(auditEntry1).isNotNull();
        assertThat(auditEntry2).isNotNull();

        assertThat(auditEntry1.getApiName()).isNotNull().isEqualTo(DELETEDAPI);
        assertThat(auditEntry1.getStatus()).isNotNull().isEqualTo(expectedDeletedLoadJobStatus.getStatus());
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

    private void verifyDeletedPeopleAudit(TestDataArguments testDataArguments) {

        List<AuthorisationAudit> authorisationAudits = authorisationsRepositoryAudit.findAll();
        assertNotNull(authorisationAudits);
        assertEquals(testDataArguments.expectedAuthorisationSize(), authorisationAudits.size());
        List<AppointmentAudit> appointmentAudits = appointmentsRepositoryAudit.findAll();
        assertNotNull(appointmentAudits);
        assertEquals(testDataArguments.expectedAppointmentsSize(), appointmentAudits.size());
        List<JudicialRoleTypeAudit> judicialRoleTypeAudits = judicialRoleTypeRepositoryAudit.findAll();
        assertNotNull(judicialRoleTypeAudits);
        List<UserProfileAudit> userProfileAudits = profileRepositoryAudit.findAll();
        assertNotNull(userProfileAudits);
        assertEquals(testDataArguments.expectedUserProfiles(), userProfileAudits.size());
    }

}
