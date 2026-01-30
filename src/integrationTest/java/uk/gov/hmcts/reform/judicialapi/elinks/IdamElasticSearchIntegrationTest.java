package uk.gov.hmcts.reform.judicialapi.elinks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonParser;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.MapperFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.json.JsonMapper;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.response.IdamResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.elinks.IdamElasticSearchIntegrationTest.PAGE_SIZE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELASTICSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.FAILED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;

@TestPropertySource(properties = {"elastic.search.recordsPerPage=" + PAGE_SIZE})
class IdamElasticSearchIntegrationTest extends ElinksDataLoadBaseTest {

    private static final String EMPTY_LIST_JSON = "[]";
    public static final int PAGE_SIZE = 1;

    private static final JsonMapper MAPPER = JsonMapper.builder()
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();

    @BeforeEach
    void setUp() {
        deleteData();
    }

    @DisplayName("Should update sidam id for matched object id")
    @Test
    void shouldUpdateSidamIdForMatchedObjectId() throws IOException {
        runTest(OK, readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_JSON));
    }

    @DisplayName("Should not update on empty list")
    @Test
    void shouldNotUpdateonEmptyList() throws IOException {
        runTest(OK, EMPTY_LIST_JSON);
    }

    @DisplayName("Should audit failed idam elastic search")
    @Test
    void shouldAuditFailedIdamElasticSearch() throws IOException {
        runTest(INTERNAL_SERVER_ERROR, readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_JSON));
    }

    private void runTest(final HttpStatus httpStatus,
                         String idamElasticSearchResponse) throws IOException {

        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);

        final String peopleApiResponseJson = readJsonAsString(PEOPLE_API_RESPONSE_JSON);
        final String locationApiResponseJson = readJsonAsString(LOCATION_API_RESPONSE_JSON);

        stubLocationApiResponse(locationApiResponseJson, OK);
        stubPeopleApiResponse(peopleApiResponseJson, OK);
        paginateIdamResponseJson(idamElasticSearchResponse).entrySet().stream()
                .forEach(entry -> stubIdamResponse(entry.getValue(), httpStatus));
        stubIdamResponse(EMPTY_LIST_JSON, httpStatus); // A final stub with an empty list to end loop
        stubIdamTokenResponse(OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        verifyUserSidamIdIsNull();

        elasticSearchLoadSidamIdsByObjectIds(httpStatus);

        if (OK.equals(httpStatus) && !EMPTY_LIST_JSON.equals(idamElasticSearchResponse)) {
            verifyUpdatedUserSidamId();
        } else {
            verifyUserSidamIdIsNull();
        }

        verifySchedulerAudit(OK.equals(httpStatus) ? SUCCESS : FAILED);
    }

    private Map<Integer, String> paginateIdamResponseJson(String json) throws JsonProcessingException {
        List<IdamResponse> idamResponses = MAPPER.readValue(json, MAPPER.getTypeFactory()
                .constructCollectionType(List.class, IdamResponse.class));
        return getPaginatedMap(idamResponses);
    }

    private Map<Integer, String> getPaginatedMap(List<IdamResponse> idamResponses)
            throws JsonProcessingException {
        Map<Integer, String> result = new HashMap<>();
        Integer pageNo = 0;
        List<IdamResponse> list = new ArrayList<>();
        for (IdamResponse idamResponse : idamResponses) {
            list.add(idamResponse);
            if (list.size() >= PAGE_SIZE) {
                MAPPER.writeValueAsString(list);
                result.put(pageNo, MAPPER.writeValueAsString(list));
                list.clear();
                pageNo++;
            }
        }
        return result;
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

        assertThat(auditEntry1.getApiName()).isNotNull().isEqualTo(ELASTICSEARCH);
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
