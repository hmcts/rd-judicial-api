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
import org.junit.jupiter.api.TestInstance;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublishDeltaSidamIdIntegrationTest extends ElinksDataLoadBaseTest {
    @Autowired
    private ConfigurableApplicationContext context;
    protected static final String PEOPLE_DELTA_LOAD_JSON = "wiremock_responses/people_delta_load.json";
    protected static final String EXISTING_USER_AUTHORISATIONS_EXPIRED = "EXISTING-USER-AUTHORISATIONS-EXPIRED";
    protected static final String EXISTING_USER_APPOINTMENTS_EXPIRED = "EXISTING-USER-APPOINTMENTS-EXPIRED";
    protected static final String EXISTING_USER_ROLES_EXPIRED = "EXISTING-USER-ROLES-EXPIRED";
    protected static final String EXISTING_USER_EXPIRED_LONG_TIME =  "EXISTING-USER-EXPIRED-REST-NOT-EXPIRED";
    protected static final String ALL_EXPIRED = "ALL-EXPIRED";
    protected static final String EXISTING_USER_NO_EXPIRY_DATES = "EXISTING-USER-NO-EXPIRY-DATES";
    protected static final String EXISTING_USER_NOT_EXPIRED = "EXISTING-USER-NOT-EXPIRED-REST-NOT-EXPIRED";
    protected static final String RECENTLY_UPLOADED_USER = "RECENTLY_UPLOADED_USER";
    protected static final String RECENTLY_UPDATED_USER = "RECENTLY_UPDATED_USER";

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
        RECENTLY_UPDATED_USER,
        RECENTLY_UPLOADED_USER,
        EXISTING_USER_AUTHORISATIONS_EXPIRED,
        EXISTING_USER_APPOINTMENTS_EXPIRED,
        EXISTING_USER_ROLES_EXPIRED,
        EXISTING_USER_EXPIRED_LONG_TIME,
        EXISTING_USER_NO_EXPIRY_DATES,
        EXISTING_USER_NOT_EXPIRED,
        ALL_EXPIRED
    }) void testScenariosDeltaFlagDisabled(String scenario) throws IOException {
        // Inject property to disable delta flag BEFORE calling the service
        TestPropertyValues.of("jrd.publisher.publish-Idams-delta=false")
            .applyTo(context);

        // Also directly set the field on the service to ensure it reads the new value
        ReflectionTestUtils.setField(publishSidamIdService, "publishIdamsDelta", false);

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


    @DisplayName("Should publish delta SidamId to topic")
    @ParameterizedTest
    @ValueSource(strings = {
        RECENTLY_UPDATED_USER,
        RECENTLY_UPLOADED_USER,
        EXISTING_USER_AUTHORISATIONS_EXPIRED,
        EXISTING_USER_APPOINTMENTS_EXPIRED,
        EXISTING_USER_ROLES_EXPIRED,
        EXISTING_USER_EXPIRED_LONG_TIME,
        EXISTING_USER_NO_EXPIRY_DATES,
        EXISTING_USER_NOT_EXPIRED,
        ALL_EXPIRED
    }) void testScenariosDeltaFlagEnabled(String scenario) throws IOException {
        // Inject property to enable delta flag BEFORE calling the service
        TestPropertyValues.of("jrd.publisher.publish-Idams-delta=true")
            .applyTo(context);

        // Also directly set the field on the service to ensure it reads the new value
        ReflectionTestUtils.setField(publishSidamIdService, "publishIdamsDelta", true);

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
        String newDate =  LocalDate.now().minusYears(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        switch (scenario) {
            case EXISTING_USER_EXPIRED_LONG_TIME:
                expireFields(results, "authorisations_with_dates",newDate);
                break;
            case EXISTING_USER_NO_EXPIRY_DATES:
                expireFields(results, "judiciary_roles","");
                expireFields(results, "appointments","");
                expireFields(results, "authorisations_with_dates","");
                break;
            case EXISTING_USER_AUTHORISATIONS_EXPIRED:
                expireFields(results, "authorisations_with_dates",newDate);
                break;
            case EXISTING_USER_APPOINTMENTS_EXPIRED:
                expireFields(results, "appointments",newDate);
                break;
            case EXISTING_USER_ROLES_EXPIRED:
                expireFields(results, "judiciary_roles",newDate);
                break;
            case ALL_EXPIRED:
                expireFields(results, "judiciary_roles",newDate);
                expireFields(results, "appointments",newDate);
                expireFields(results, "authorisations_with_dates",newDate);
                break;
        }
     return root;
    }


    private void expireFields(JsonNode resultsNode, String fieldName,String newDate ) {
        if (resultsNode.isArray()) {
            for (JsonNode item : resultsNode) {
                    item.path(fieldName)
                        .forEach(role -> ((ObjectNode) role).put("end_date",newDate));
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
            case RECENTLY_UPDATED_USER:
                users.get(0).setLastUpdated(now());
                break;
            case RECENTLY_UPLOADED_USER:
                users.get(0).setLastLoadedDate(now());
                break;
            case EXISTING_USER_NO_EXPIRY_DATES:
                users.get(0).setLastLoadedDate(now().minusYears(2));
                users.get(0).setLastUpdated(now().minusYears(2));
                break;
            case EXISTING_USER_EXPIRED_LONG_TIME:
                users.get(0).setLastLoadedDate(now().minusYears(2));
                users.get(0).setLastUpdated(now().minusYears(2));
                break;
            case EXISTING_USER_NOT_EXPIRED:
                users.get(0).setLastLoadedDate(now().minusYears(2));
                users.get(0).setLastUpdated(now().minusYears(2));
                break;
            case EXISTING_USER_AUTHORISATIONS_EXPIRED:
                users.get(0).setLastLoadedDate(now().minusYears(2));
                users.get(0).setLastUpdated(now().minusYears(2));
                break;
            case EXISTING_USER_APPOINTMENTS_EXPIRED:
                users.get(0).setLastLoadedDate(now().minusYears(2));
                users.get(0).setLastUpdated(now().minusYears(2));
                break;
            case EXISTING_USER_ROLES_EXPIRED:
                users.get(0).setLastLoadedDate(now().minusYears(2));
                users.get(0).setLastUpdated(now().minusYears(2));
                break;
            case ALL_EXPIRED:
                users.get(0).setLastLoadedDate(now().minusYears(10));
                users.get(0).setLastUpdated(now().minusYears(10));
                break;
        }
        profileRepository.saveAll(users);
    }


    public void verifyPeopleResponse(String scenario, ValidatableResponse response) {
        int actualSidamIdsCountPublished = response.extract().path("sidamIdsCount");

            switch (scenario) {
                case RECENTLY_UPDATED_USER:
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case RECENTLY_UPLOADED_USER:
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case EXISTING_USER_AUTHORISATIONS_EXPIRED:
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case EXISTING_USER_ROLES_EXPIRED:
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case EXISTING_USER_APPOINTMENTS_EXPIRED:
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case EXISTING_USER_NOT_EXPIRED:
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case EXISTING_USER_NO_EXPIRY_DATES:
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case EXISTING_USER_EXPIRED_LONG_TIME:
                    assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    verify(elinkTopicPublisher).sendMessage(anyList(), anyString());
                    break;
                case ALL_EXPIRED:
                    if((ReflectionTestUtils.getField(publishSidamIdService, "publishIdamsDelta")).toString().equalsIgnoreCase("true")) {
                        assertThat(actualSidamIdsCountPublished).isEqualTo(0);
                    }else{
                        assertThat(actualSidamIdsCountPublished).isEqualTo(1);
                    }
                    break;
            }
        }

}