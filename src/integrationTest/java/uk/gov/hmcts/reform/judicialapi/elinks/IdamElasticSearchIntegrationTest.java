package uk.gov.hmcts.reform.judicialapi.elinks;

import lombok.extern.slf4j.Slf4j;
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
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELASTICSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.FAILED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;

@Slf4j
class IdamElasticSearchIntegrationTest extends ElinksDataLoadBaseTest {

    @BeforeEach
    void setUp() {
        deleteData();
    }

    @DisplayName("Should update sidam id for matched object ids")
    @Test
    void shouldUpdateSidamIdForMatchedObjectIds() throws IOException {
        //Given
        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);

        final String peopleApiResponseJson = readJsonAsString(PEOPLE_API_RESPONSE_JSON);
        final String locationApiResponseJson = readJsonAsString(LOCATION_API_RESPONSE_JSON);

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubIdamElasticSearchResponse(readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_PG1_JSON), 0, OK);
        stubIdamElasticSearchResponse(readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_PG2_JSON), 1, OK);
        stubIdamElasticSearchResponse(EMPTY_RESPONSE, 2, OK);
        stubIdamResponse(EMPTY_RESPONSE, OK);
        stubIdamTokenResponse(OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        //When
        verifyUserSidamIdIsNull();
        elasticSearchLoadSidamIdsByObjectIds(OK);

        //Then
        verifyUpdatedUserSidamId();
        verifySchedulerAudit(SUCCESS);
    }

    @DisplayName("Should generate sidam id on empty Idam ids list")
    @Test
    void shouldGenerateSidamIdOnEmptyList() throws IOException {

        //Given
        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);

        final String peopleApiResponseJson = readJsonAsString(PEOPLE_API_RESPONSE_JSON);
        final String locationApiResponseJson = readJsonAsString(LOCATION_API_RESPONSE_JSON);

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubIdamElasticSearchResponse(EMPTY_RESPONSE, 0, OK);
        stubIdamResponse(EMPTY_RESPONSE, OK);
        stubIdamTokenResponse(OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        //When
        verifyUserSidamIdIsNull();
        elasticSearchLoadSidamIdsByObjectIds(OK);

        //Then
        verifyGeneratedUserSidamIds();
        verifySchedulerAudit(SUCCESS);
    }

    @DisplayName("Should audit failed idam elastic search")
    @Test
    void shouldAuditFailedIdamElasticSearch() throws IOException {
        //Given
        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);

        final String peopleApiResponseJson = readJsonAsString(PEOPLE_API_RESPONSE_JSON);
        final String locationApiResponseJson = readJsonAsString(LOCATION_API_RESPONSE_JSON);

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubIdamElasticSearchResponse(readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_PG1_JSON), 0, INTERNAL_SERVER_ERROR);
        stubIdamTokenResponse(OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        //When
        verifyUserSidamIdIsNull();
        elasticSearchLoadSidamIdsByObjectIds(INTERNAL_SERVER_ERROR);

        //Then
        verifyUserSidamIdIsNull();
        verifySchedulerAudit(FAILED);
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
        assertThat(secondUser.getSidamId()).isNotNull().isNotBlank().isNotEqualTo(firstUser.getSidamId());
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

    private void verifyGeneratedUserSidamIds() {
        final List<UserProfile> userprofile =
                profileRepository.findAll().stream()
                        .sorted(Comparator.comparing(UserProfile::getPersonalCode))
                        .toList();
        assertThat(userprofile).isNotNull().hasSize(2);

        final UserProfile firstUser = userprofile.get(0);
        final UserProfile secondUser = userprofile.get(1);

        assertThat(firstUser.getObjectId()).isNotNull().isEqualTo("5f8b26ba-0c8b-4192-b5c7-311d737f0cae");
        assertThat(firstUser.getSidamId()).isNotNull().isNotBlank();

        assertThat(secondUser.getObjectId()).isNotNull().isEqualTo("8eft26ba-0c8b-4192-b5c7-311d737f0cae");
        assertThat(secondUser.getSidamId()).isNotNull().isNotBlank().isNotEqualTo(firstUser.getSidamId());
    }

    private void verifySchedulerAudit(JobStatus idamElasticSerachJobStatus) {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits =
                elinkSchedularAuditRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinkDataSchedularAudit::getApiName))
                        .toList();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty();

        assertAuditEntry(eLinksDataSchedulerAudits, ELASTICSEARCH, idamElasticSerachJobStatus.getStatus());
        assertAuditEntry(eLinksDataSchedulerAudits, LOCATIONAPI, SUCCESS.getStatus());
        assertAuditEntry(eLinksDataSchedulerAudits, PEOPLEAPI, SUCCESS.getStatus());

        boolean hasIdamSearchAudit = eLinksDataSchedulerAudits.stream()
                .anyMatch(entry -> IDAMSEARCH.equals(entry.getApiName()));
        if (hasIdamSearchAudit) {
            assertAuditEntry(eLinksDataSchedulerAudits, IDAMSEARCH, SUCCESS.getStatus());
            assertThat(eLinksDataSchedulerAudits).hasSize(4);
        } else {
            assertThat(eLinksDataSchedulerAudits).hasSize(3);
        }
    }

    private void assertAuditEntry(List<ElinkDataSchedularAudit> audits,
                                  String apiName,
                                  String expectedStatus) {
        ElinkDataSchedularAudit audit = audits.stream()
                .filter(entry -> apiName.equals(entry.getApiName()))
                .findFirst()
                .orElseThrow();

        assertThat(audit.getStatus()).isNotNull().isEqualTo(expectedStatus);
        assertThat(audit.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(audit.getSchedulerStartTime()).isNotNull();
        assertThat(audit.getSchedulerEndTime()).isNotNull();
    }

}
