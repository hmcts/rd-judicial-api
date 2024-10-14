package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;

class FindIdamIdsIntegrationTest extends ElinksDataLoadBaseTest {

    @BeforeEach
    void setUp() {
        deleteData();
    }

    @DisplayName("Should update sidam id for matched object id")
    @Test
    void shouldUpdateSidamIdForMatchedObjectId() throws IOException {

        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);

        final String peopleApiResponseJson = readJsonAsString(PEOPLE_API_RESPONSE_JSON);
        final String idamIdsSearchResponse = readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_JSON);
        final String locationApiResponseJson = readJsonAsString(LOCATION_API_RESPONSE_JSON);

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubIdamResponse(idamIdsSearchResponse, OK);
        stubIdamTokenResponse(OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        verifyUserSidamIdIsNull();

        findSidamIdsByObjectIds(OK);

        verifyUpdatedUserSidamId();

        verifySchedulerAudit(SUCCESS);
    }

    @DisplayName("Should audit failed when idam returns error response")
    @Test
    void shouldAuditFailedWhenIdamReturnsErrorResponse() throws IOException {

        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);

        final String peopleApiResponseJson = readJsonAsString(PEOPLE_API_RESPONSE_JSON);
        final String idamElasticSearchResponse = readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_JSON);
        final String locationApiResponseJson = readJsonAsString(LOCATION_API_RESPONSE_JSON);

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubIdamResponse(idamElasticSearchResponse, INTERNAL_SERVER_ERROR);
        stubIdamTokenResponse(OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        verifyUserSidamIdIsNull();

        findSidamIdsByObjectIds(OK);

        verifyUserSidamIdIsNull();

        verifySchedulerAudit(PARTIAL_SUCCESS);
    }

    private void verifyUpdatedUserSidamId() {

        final List<UserProfile> userprofile =
                profileRepository.findAll().stream()
                        .sorted(Comparator.comparing(UserProfile::getPersonalCode))
                        .toList();

        assertThat(userprofile).isNotNull().hasSize(2);

        final UserProfile firstUser = userprofile.get(0);
        final UserProfile secondUser = userprofile.get(1);

        assertThat(firstUser.getObjectId()).isNotNull().isEqualTo("5f8b26ba-0c8b-4192-b5c7-311d737f0cae");
        assertThat(firstUser.getSidamId()).isNotNull().isEqualTo("6455c84c-e77d-4c4f-9759-bf4a93a8e972");

        assertThat(secondUser.getObjectId()).isNotNull().isEqualTo("8eft26ba-0c8b-4192-b5c7-311d737f0cae");
        assertThat(secondUser.getSidamId()).isNull();
    }

    private void verifyUserSidamIdIsNull() {
        final List<UserProfile> userprofile =
                profileRepository.findAll().stream()
                        .sorted(Comparator.comparing(UserProfile::getPersonalCode))
                        .toList();
        assertThat(userprofile).isNotNull().hasSize(2);

        final UserProfile firstUser = userprofile.get(0);
        final UserProfile secondUser = userprofile.get(1);

        assertThat(firstUser.getObjectId()).isNotNull().isEqualTo("5f8b26ba-0c8b-4192-b5c7-311d737f0cae");
        assertThat(firstUser.getSidamId()).isNull();

        assertThat(secondUser.getObjectId()).isNotNull().isEqualTo("8eft26ba-0c8b-4192-b5c7-311d737f0cae");
        assertThat(secondUser.getSidamId()).isNull();
    }

    private void verifySchedulerAudit(JobStatus idamElasticSerachJobStatus) {

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
        assertThat(auditEntry3).isNotNull();

        assertThat(auditEntry1.getApiName()).isNotNull().isEqualTo(IDAMSEARCH);
        assertThat(auditEntry1.getStatus()).isNotNull().isEqualTo(idamElasticSerachJobStatus.getStatus());
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
