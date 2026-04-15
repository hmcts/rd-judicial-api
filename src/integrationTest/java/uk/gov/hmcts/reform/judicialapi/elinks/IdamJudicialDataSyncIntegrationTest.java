package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELASTICSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;

@TestPropertySource(properties = {"idam.sync=true"})
class IdamJudicialDataSyncIntegrationTest extends ElinksDataLoadBaseTest {

    private static final String OBJECT_ID_1 = "5f8b26ba-0c8b-4192-b5c7-311d737f0cae";
    private static final String OBJECT_ID_2 = "8eft26ba-0c8b-4192-b5c7-311d737f0cae";
    private static final String SIDAM_ID_1 = "6455c84c-e77d-4c4f-9759-bf4a93a8e972";

    @BeforeEach
    void setUp() {
        deleteData();
    }

    @DisplayName("Should generate SIDAM id when IDAM user is missing via elastic search")
    @Test
    void shouldGenerateSidamIdWhenIdamUserIsMissing() throws IOException {

        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);

        final String peopleApiResponseJson = readJsonAsString(PEOPLE_API_RESPONSE_JSON);
        final String idamIdsSearchResponse = readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_JSON);
        final String locationApiResponseJson = readJsonAsString(LOCATION_API_RESPONSE_JSON);

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        stubIdamTokenResponse(OK);

        stubIdamElasticSearchResponse(idamIdsSearchResponse, OK);
        stubIdamResponseForQuery("ssoid:" + OBJECT_ID_1, idamIdsSearchResponse, OK);
        stubIdamResponseForQuery("ssoid:" + OBJECT_ID_2, "[]", OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        verifyUserSidamIdIsNull();

        elasticSearchLoadSidamIdsByObjectIds(OK);

        verifyUpdatedUserSidamIdsWithGenerated();

        verifySchedulerAudit(SUCCESS);
    }

    private void stubIdamResponseForQuery(String query, String responseBody, HttpStatus httpStatus) {
        sidamService.stubFor(get(urlPathMatching("/api/v1/users"))
                .withQueryParam("query", equalTo(query))
                .willReturn(aResponse()
                        .withStatus(httpStatus.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(responseBody)));
    }

    private void stubIdamElasticSearchResponse(String responseBody, HttpStatus httpStatus) {
        sidamService.stubFor(get(urlPathMatching("/api/v1/users"))
                .withQueryParam("query", equalTo("(roles:judiciary) AND lastModified:>now-12h"))
                .withQueryParam("size", equalTo("500"))
                .withQueryParam("page", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(httpStatus.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(responseBody)));
    }

    private void verifyUpdatedUserSidamIdsWithGenerated() {

        final List<UserProfile> userprofile =
                profileRepository.findAll().stream()
                        .sorted(Comparator.comparing(UserProfile::getPersonalCode))
                        .toList();

        assertThat(userprofile).isNotNull().hasSize(2);

        final UserProfile firstUser = userprofile.get(0);
        final UserProfile secondUser = userprofile.get(1);

        assertThat(firstUser.getObjectId()).isNotNull().isEqualTo(OBJECT_ID_1);
        assertThat(firstUser.getSidamId()).isNotNull().isEqualTo(SIDAM_ID_1);

        assertThat(secondUser.getObjectId()).isNotNull().isEqualTo(OBJECT_ID_2);
        assertThat(secondUser.getSidamId()).isNotNull()
                .matches("^[0-9a-fA-F\\-]{36}$")
                .isNotEqualTo(SIDAM_ID_1);
    }

    private void verifyUserSidamIdIsNull() {
        final List<UserProfile> userprofile =
                profileRepository.findAll().stream()
                        .sorted(Comparator.comparing(UserProfile::getPersonalCode))
                        .toList();
        assertThat(userprofile).isNotNull().hasSize(2);

        final UserProfile firstUser = userprofile.get(0);
        final UserProfile secondUser = userprofile.get(1);

        assertThat(firstUser.getObjectId()).isNotNull().isEqualTo(OBJECT_ID_1);
        assertThat(firstUser.getSidamId()).isNull();

        assertThat(secondUser.getObjectId()).isNotNull().isEqualTo(OBJECT_ID_2);
        assertThat(secondUser.getSidamId()).isNull();
    }

    private void verifySchedulerAudit(JobStatus idamElasticSerachJobStatus) {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits =
                elinkSchedularAuditRepository.findAll()
                        .stream()
                        .sorted(comparing(ElinkDataSchedularAudit::getApiName))
                        .toList();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty().hasSize(4);

        assertAuditEntry(eLinksDataSchedulerAudits, ELASTICSEARCH, SUCCESS.getStatus());
        assertAuditEntry(eLinksDataSchedulerAudits, IDAMSEARCH, idamElasticSerachJobStatus.getStatus());
        assertAuditEntry(eLinksDataSchedulerAudits, LOCATIONAPI, SUCCESS.getStatus());
        assertAuditEntry(eLinksDataSchedulerAudits, PEOPLEAPI, SUCCESS.getStatus());
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
