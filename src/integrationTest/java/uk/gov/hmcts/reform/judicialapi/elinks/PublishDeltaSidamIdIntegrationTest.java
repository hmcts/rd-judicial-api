package uk.gov.hmcts.reform.judicialapi.elinks;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.DataloadSchedulerJob;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinksResponses;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.UserProfile;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksDataLoadBaseTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JobStatus;

class PublishDeltaSidamIdIntegrationTest extends ElinksDataLoadBaseTest {
    @Autowired
    private ConfigurableApplicationContext context;
    protected static final String PEOPLE_DELTA_LOAD_JSON = "wiremock_responses/people_delta_load.json";
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
        "EXISTING-USER-AUTHORISATIONS-EXPIRED",
        "EXISTING-USER-APPOINTMENTS-EXPIRED",
        "EXISTING-USER-ROLES-EXPIRED",
        "EXISTING-USER-NOT-EXPIRED-REST-NOT-EXPIRED",
        "EXISTING-USER-NO-EXPIRY-DATES",
        "EXISTING-USER-EXPIRED-REST-NOT-EXPIRED",
        "ALL-EXPIRED",
        "ALL-EXPIRED-FLAG-DISABLED",
    }) void testScenariosDeltaFlagEnabled(String scenario) throws IOException {
        willDoNothing().given(elinkTopicPublisher).sendMessage(anyList(), anyString());

        ObjectMapper mapper = new ObjectMapper();
        InputStream jsonInput = getClass().getClassLoader()
            .getResourceAsStream(PEOPLE_DELTA_LOAD_JSON);
        JsonNode basePeopleData = mapper.readTree(jsonInput);
        JsonNode mutatedPeopleData = mutatePeopleResponse(scenario, mapper, basePeopleData);

        stubPeopleApiResponse(mapper.writeValueAsString(mutatedPeopleData), OK);


        stubResponsesToRunElinks();

        manipulatePeopleDataBeforePublish(scenario);

        publishSidamIds(OK);

        verifyPeopleResponse(scenario);

        verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
    }


    protected JsonNode mutatePeopleResponse(String scenario,
                                        ObjectMapper mapper,
                                        JsonNode base) throws IOException {

        ObjectNode root = (ObjectNode) base.deepCopy();
        JsonNode results = root.path("results");

        switch (scenario) {
            case "EXISTING-USER-AUTHORISATIONS-EXPIRED":
                expireFields(results, "judiciary_roles");
                break;
            case "EXISTING-USER-APPOINTMENTS-EXPIRED":
                expireFields(results, "appointments");
                break;
            case "EXISTING-USER-ROLES-EXPIRED":
                expireFields(results, "authorisations_with_dates");
                break;
            case "ALL-EXPIRED":
                expireFields(results, "judiciary_roles");
                expireFields(results, "appointments");
                expireFields(results, "authorisations_with_dates");
                break;
            case "ALL-EXPIRED-FLAG-DISABLED":
                expireFields(results, "judiciary_roles");
                expireFields(results, "appointments");
                expireFields(results, "authorisations_with_dates");
                break;
        }
     return root;
    }


    private void expireFields(JsonNode resultsNode, String fieldName) {
        if (resultsNode.isArray()) {
            for (JsonNode item : resultsNode) {
                    item.path(fieldName)
                        .forEach(role -> ((ObjectNode) role).put("end_date",
                            LocalDate.now().minusYears(9)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            }
        }
    }

    private void stubResponsesToRunElinks() throws IOException {

        stubLocationApiResponse(readJsonAsString(LOCATION_API_RESPONSE_JSON), OK);
        stubIdamResponse(readJsonAsString(IDAM_IDS_SEARCH_RESPONSE_JSON), OK);
        stubIdamTokenResponse(OK);

        loadLocationData(OK, RESPONSE_BODY_MSG_KEY, BASE_LOCATION_DATA_LOAD_SUCCESS);
        loadPeopleData(OK, RESPONSE_BODY_MSG_KEY, PEOPLE_DATA_LOAD_SUCCESS);

        findSidamIdsByObjectIds(OK);
    }


    protected void manipulatePeopleDataBeforePublish(String scenario) throws IOException {

        List<String> personalCodes = List.of("100000");

        List<UserProfile> users = profileRepository
            .fetchUserProfileByPersonalCodes(personalCodes, PageRequest.of(0, 100)).getContent();

        switch (scenario) {

            case "EXISTING-USER-NO-EXPIRY-DATES":
                users.get(0).setLastLoadedDate(null);
                break;
            case "EXISTING-USER-EXPIRED-REST-NOT-EXPIRED":
                users.get(0).setLastLoadedDate(now().minusYears(9));
                users.get(0).setLastUpdated(now().minusYears(9));
                break;
            case "EXISTING-USER-NOT-EXPIRED-REST-NOT-EXPIRED":
                break;
            case "ALL-EXPIRED":
                users.get(0).setLastLoadedDate(now().minusYears(9));
                users.get(0).setLastUpdated(now().minusYears(9));
                users.get(0).setKnownAs("Sabina");
                break;
            case "ALL-EXPIRED-FLAG-DISABLED":
                TestPropertyValues.of("jrd.publisher.publish-Idams-delta=false")
                    .applyTo(context);
                users.get(0).setLastLoadedDate(now().minusYears(9));
                users.get(0).setLastUpdated(now().minusYears(9));
                break;
        }
        profileRepository.saveAll(users);
    }




    public void verifyPeopleResponse(String scenario) {
        final List<ElinksResponses> eLinksResponses = elinksResponsesRepository.findAll()
                .stream().sorted(comparing(ElinksResponses::getApiName)).toList();
        assertThat(eLinksResponses).isNotNull().isNotEmpty().hasSize(2);

        ElinksResponses peopleElinksResponses = eLinksResponses.get(1);
        assertThat(peopleElinksResponses).isNotNull();
        assertThat(peopleElinksResponses.getApiName()).isNotNull().isNotNull().isEqualTo(PEOPLEAPI);
        assertThat(peopleElinksResponses.getCreatedDate()).isNotNull();
        assertThat(peopleElinksResponses.getElinksData()).isNotNull();

        com.fasterxml.jackson.databind.JsonNode resultsNode = peopleElinksResponses.getElinksData().path("results");
        if (resultsNode.isArray()) {
            switch (scenario) {
                case "USER-NOT-EXPIRED-REST-NOT-EXPIRED":
                    assertsToVerifyUserPublished(1, resultsNode);
                    break;
                case "USER-NOT-EXPIRED-ROLES-EXPIRED":
                    assertsToVerifyUserPublished(1, resultsNode);
                    break;
                case "USER-NOT-EXPIRED-APPOINTMENTS-EXPIRED":
                    assertsToVerifyUserPublished(1, resultsNode);
                    break;
                case "USER-NOT-EXPIRED-AUTHORISATIONS-EXPIRED":
                    assertsToVerifyUserPublished(1, resultsNode);
                    break;
                case "USER-NOT-EXPIRED-APPOINTMENTS-AUTHORISATIONS-EXPIRED":
                    assertsToVerifyUserPublished(1, resultsNode);
                    break;
                case "USER-NOT-EXPIRED-AUTHORISATIONS-ROLES-EXPIRED":
                    assertsToVerifyUserPublished(1, resultsNode);
                    break;
                case "USER-NOT-EXPIRED-AUTHORISATIONS-ROLES-APPOINTMENTS-EXPIRED":
                    assertsToVerifyUserPublished(0, resultsNode);
                    break;
                case "USER-EXPIRED-REST-NOT-EXPIRED":
                    assertsToVerifyUserPublished(1, resultsNode);
                    break;
                case "ALL-EXPIRED":
                    assertsToVerifyUserPublished(0, resultsNode);
                    break;
            }
        }
    }

    private void assertsToVerifyUserPublished(int size,com.fasterxml.jackson.databind.JsonNode resultsNode) {

        assertThat(resultsNode.size()).isEqualTo(size);
        for (com.fasterxml.jackson.databind.JsonNode item : resultsNode) {
            assertThat(item.path("id").asText()).contains("10000000-0c8b-4192-b5c7-311d737f0cae");
            assertThat(item.path("personal_code").asText()).contains("100000");
            assertThat(item.path("known_as").asText()).contains("User1");
            assertThat(item.path("email").asText()).contains("User1@ejudiciary.net");
        }
    }

}