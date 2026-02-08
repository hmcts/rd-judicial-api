package uk.gov.hmcts.reform.judicialapi.elinks;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAMSEARCH;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus.SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PUBLISHSIDAM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus;

class PublishDeltaSidamIdIntegrationTest extends ElinksDataLoadBaseTest {
    private static final String LAST_LOADED_DATE_REGEX = "\"lastLoadedDate\":\"[^\"]+\"";
    private static final String LAST_UPDATED_DATE_REGEX = "\"lastUpdated\":\"[^\"]+\"";

    @BeforeEach
    void setUp() {
        deleteData();

        DataloadSchedulerJob job = new DataloadSchedulerJob();
        job.setJobStartTime(now());
        job.setPublishingStatus(JobStatus.IN_PROGRESS.getStatus());
        dataloadSchedulerJobRepository.save(job);

        ReflectionTestUtils.setField(publishSidamIdService, "elinkTopicPublisher", elinkTopicPublisher);
        given(idamTokenConfigProperties.getAuthorization()).willReturn(USER_PASSWORD);
    }

    @DisplayName("Should publish delta SidamId to topic")
    @ParameterizedTest
    @ValueSource(strings = {
        "USER-NOT-EXPIRED-REST-NOT-EXPIRED",
        "USER-NOT-EXPIRED-ROLES-EXPIRED",
        "USER-NOT-EXPIRED-APPOINTMENTS-EXPIRED",
        "USER-NOT-EXPIRED-AUTHORISATIONS-EXPIRED",
        "USER-NOT-EXPIRED-APPOINTMENTS-AUTHORISATIONS-EXPIRED",
        "USER-NOT-EXPIRED-AUTHORISATIONS-ROLES-EXPIRED",
        "USER-NOT-EXPIRED-AUTHORISATIONS-ROLES-APPOINTMENTS-EXPIRED",
        "USER-NOT-EXPIRED-APPOINTMENTS-ROLES-EXPIRED",
        "USER-EXPIRED-REST-NOT-EXPIRED",
        "ALL-EXPIRED",
    })
    void testScenarios(String scenario) throws IOException {
        willDoNothing().given(elinkTopicPublisher).sendMessage(anyList(), anyString());

        String newEndDate = "2014-04-30";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode basePeopleData = loadBasePeopleJson(mapper);

        JsonNode mutatedPeopleData = mutatePeopleResponse(scenario, mapper, basePeopleData, newEndDate);

        if (mutatedPeopleData != null) {
            stubPeopleApiResponse(mapper.writeValueAsString(mutatedPeopleData), OK);
        }

        stubCommonResponses();

        verifyElinksResponse(scenario);
        verifyAudit(scenario);

        verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
    }

    private JsonNode loadBasePeopleJson(ObjectMapper mapper) throws IOException {
        InputStream jsonInput = getClass().getClassLoader()
            .getResourceAsStream("wiremock_responses/people_not_epired.json");

        return mapper.readTree(jsonInput);
    }

    private JsonNode mutatePeopleResponse(String scenario,
                                          ObjectMapper mapper,
                                          JsonNode base,
                                          String newEndDate) throws IOException {

        ObjectNode root = (ObjectNode) base.deepCopy();
        JsonNode results = root.path("results");

        switch (scenario) {
            case "USER-NOT-EXPIRED-REST-NOT-EXPIRED":
                return base;

            case "USER-NOT-EXPIRED-ROLES-EXPIRED":
                expireFields(results, "judiciary_roles", newEndDate);
                break;

            case "USER-NOT-EXPIRED-APPOINTMENTS-EXPIRED":
                expireFields(results, "appointments", newEndDate);
                break;

            case "USER-NOT-EXPIRED-AUTHORISATIONS-EXPIRED":
                expireFields(results, "authorisations", newEndDate);
                break;

            case "USER-NOT-EXPIRED-APPOINTMENTS-AUTHORISATIONS-EXPIRED":
                expireFields(results, "appointments", newEndDate);
                expireFields(results, "authorisations", newEndDate);
                break;

            case "USER-NOT-EXPIRED-AUTHORISATIONS-ROLES-EXPIRED":
                expireFields(results, "authorisations", newEndDate);
                expireFields(results, "judiciary_roles", newEndDate);
                break;

            case "USER-NOT-EXPIRED-AUTHORISATIONS-ROLES-APPOINTMENTS-EXPIRED":
                expireFields(results, "authorisations", newEndDate);
                expireFields(results, "appointments", newEndDate);
                expireFields(results, "judiciary_roles", newEndDate);
                break;

            case "USER-NOT-EXPIRED-APPOINTMENTS-ROLES-EXPIRED":
                expireFields(results, "appointments", newEndDate);
                expireFields(results, "judiciary_roles", newEndDate);
                break;

            case "ALL-EXPIRED":
                expireFields(results, "appointments", newEndDate);
                expireFields(results, "judiciary_roles", newEndDate);
                break;

            case "USER-EXPIRED-REST-NOT-EXPIRED":
                // only audit/date replacement, no stub change
                return null;
        }

        return root;
    }

    private void expireFields(JsonNode resultsNode, String fieldName, String newEndDate) {
        if (resultsNode.isArray()) {
            for (JsonNode item : resultsNode) {
                String knownAs = item.path("known_as").asText();
                if ("Ronin".equalsIgnoreCase(knownAs)) {
                    item.path(fieldName)
                        .forEach(role -> ((ObjectNode) role).put("end_date", newEndDate));
                }
            }
        }
    }

    private void stubCommonResponses() throws IOException {
        stubLocationApiResponse(readJsonAsString(LOCATION_API_RESPONSE_JSON), OK);

        stubIdamResponse(readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_JSON), OK);
        stubIdamTokenResponse(OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        findSidamIdsByObjectIds(OK);
        publishSidamIds(OK);
    }
    public void verifyElinksResponse(String scenario) {
        final List<ElinksResponses> eLinksResponses =
            elinksResponsesRepository.findAll()
                .stream()
                .sorted(comparing(ElinksResponses::getApiName))
                .toList();
        switch (scenario) {
            case "USER-NOT-EXPIRED-REST-NOT-EXPIRED":
                assertThat(eLinksResponses).isNotNull().isNotEmpty().hasSize(2);
                eLinksResponses.forEach(response ->
                    {
                        if (response.getApiName().equalsIgnoreCase(PEOPLEAPI)) {
                            assertThat(response.getElinksData()).isNotNull();
                            com.fasterxml.jackson.databind.JsonNode rootNode = response.getElinksData();
                            com.fasterxml.jackson.databind.JsonNode resultsNode = rootNode.path("results");
                            if (resultsNode.isArray()) {
                                for (com.fasterxml.jackson.databind.JsonNode item : resultsNode) {
                                    //String id = item.path("id").asText();
                                    //String email = item.path("email").asText();
                                    //assertThat(response.getElinksData().get("id")).contains("");
                                    assertThat(item.path("email").asText()).contains("");
                                }
                            }

                        }
                    }
                );
            case "USER-NOT-EXPIRED-ROLES-EXPIRED":

            case "USER-NOT-EXPIRED-APPOINTMENTS-EXPIRED":

            case "USER-NOT-EXPIRED-AUTHORISATIONS-EXPIRED":

            case "USER-NOT-EXPIRED-APPOINTMENTS-AUTHORISATIONS-EXPIRED":

            case "USER-NOT-EXPIRED-AUTHORISATIONS-ROLES-EXPIRED":

            case "USER-NOT-EXPIRED-AUTHORISATIONS-ROLES-APPOINTMENTS-EXPIRED":

            case "USER-NOT-EXPIRED-APPOINTMENTS-ROLES-EXPIRED":

            case "USER-EXPIRED-REST-NOT-EXPIRED":

            case "ALL-EXPIRED":

        }

        ElinksResponses peopleElinksResponses = eLinksResponses.get(1);
        assertThat(peopleElinksResponses).isNotNull();
        assertThat(peopleElinksResponses.getApiName()).isNotNull().isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(peopleElinksResponses.getCreatedDate()).isNotNull();
        assertThat(peopleElinksResponses.getElinksData()).isNotNull();

    }

    private void verifyAudit(String scenario) {

        final List<ElinkDataSchedularAudit> eLinksDataSchedulerAudits =
            elinkSchedularAuditRepository.findAll()
                .stream()
                .sorted(comparing(ElinkDataSchedularAudit::getApiName))
                .toList();
        assertThat(eLinksDataSchedulerAudits).isNotNull().isNotEmpty().hasSize(4);

        final ElinkDataSchedularAudit auditEntry1 = eLinksDataSchedulerAudits.get(0);
        final ElinkDataSchedularAudit auditEntry2 = eLinksDataSchedulerAudits.get(1);
        final ElinkDataSchedularAudit auditEntry3 = eLinksDataSchedulerAudits.get(2);
        final ElinkDataSchedularAudit auditEntry4 = eLinksDataSchedulerAudits.get(3);

        assertThat(auditEntry1).isNotNull();
        assertThat(auditEntry2).isNotNull();
        assertThat(auditEntry3).isNotNull();
        assertThat(auditEntry4).isNotNull();

        assertThat(auditEntry1.getApiName()).isNotNull().isEqualTo(IDAMSEARCH);
        assertThat(auditEntry1.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry1.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry1.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry1.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry2.getApiName()).isNotNull().isEqualTo(LOCATIONAPI);
        assertThat(auditEntry2.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry2.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry2.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry2.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry3.getApiName()).isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(auditEntry3.getStatus()).isNotNull().isEqualTo(PARTIAL_SUCCESS.getStatus());
        assertThat(auditEntry3.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry3.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry3.getSchedulerEndTime()).isNotNull();

        assertThat(auditEntry4.getApiName()).isNotNull().isEqualTo(PUBLISHSIDAM);
        assertThat(auditEntry4.getStatus()).isNotNull().isEqualTo(SUCCESS.getStatus());
        assertThat(auditEntry4.getSchedulerName()).isNotNull().isEqualTo(JUDICIAL_REF_DATA_ELINKS);
        assertThat(auditEntry4.getSchedulerStartTime()).isNotNull();
        assertThat(auditEntry4.getSchedulerEndTime()).isNotNull();
    }


}