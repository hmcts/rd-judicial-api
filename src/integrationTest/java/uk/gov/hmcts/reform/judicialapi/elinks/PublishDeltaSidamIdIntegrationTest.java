package uk.gov.hmcts.reform.judicialapi.elinks;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_DATA_LOAD_SUCCESS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLE_DATA_LOAD_SUCCESS;

import io.restassured.response.ValidatableResponse;
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

        ValidatableResponse publishDeltaSidamIdsResponse = publishDeltaSidamIds(OK);

        verifyPeopleResponse(scenario, publishDeltaSidamIdsResponse);

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




    public void verifyPeopleResponse(String scenario, ValidatableResponse response) {
        int actualSidamIdsCountPublished = response.extract().path("sidamIdsCount");

            switch (scenario) {
                case "EXISTING-USER-APPOINTMENTS-EXPIRED":
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case "EXISTING-USER-ROLES-EXPIRED":
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case "EXISTING-USER-AUTHORISATIONS-EXPIRED":
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case "EXISTING-USER-NOT-EXPIRED-REST-NOT-EXPIRED":
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case "EXISTING-USER-NO-EXPIRY-DATES":
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case "EXISTING-USER-EXPIRED-REST-NOT-EXPIRED":
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case "ALL-EXPIRED":
                    assertThat(actualSidamIdsCountPublished).isEqualTo(0);
                    break;
                case "ALL-EXPIRED-FLAG-DISABLED":
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
            }
        }



}