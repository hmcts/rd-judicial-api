package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataExceptionRecords;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AppointmentsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.AuthorisationsRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkDataExceptionRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ElinkSchedularAuditRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.ProfileRepository;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;
import uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASE_LOCATION_ID;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.DELETEDAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAM_ERROR_MESSAGE;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.PEOPLEAPI;

class ElinkClientsCommonIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private BaseLocationRepository baseLocationRepository;

    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;

    @Autowired
    private ElinkDataExceptionRepository elinkDataExceptionRepository;

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    @Autowired
    private AuthorisationsRepository authorisationsRepository;

    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;

    @BeforeEach
    void setUp() {
        cleanupData();
    }

    @AfterEach
    void cleanUp() {
        cleanupData();
    }

    @DisplayName("Elinks People endpoint status verification for future update_since")
    @Test
    void test_get_people_return_response_status_400() throws JsonProcessingException  {

        int statusCode = 400;
        peopleApi4xxResponse(statusCode,null);

        String peopleUrl = "/people?updated_since=2025-01-01";
        Map<String, Object> response = elinksReferenceDataClient.getPeoples(peopleUrl);

        assertThat(response).containsEntry("http_status", "400");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);
    }

    @DisplayName("Elinks People endpoint status verification for unauthorized status")
    @Test
    void test_get_people_return_response_status_401() throws JsonProcessingException  {

        int statusCode = 401;
        peopleApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();

        assertThat(response).containsEntry("http_status", "401");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_UNAUTHORIZED);
    }

    @DisplayName("Elinks People endpoint status verification for forbidden status")
    @Test
    void test_get_people_return_response_status_403() throws JsonProcessingException  {

        int statusCode = 403;
        peopleApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();

        assertThat(response).containsEntry("http_status", "403");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_FORBIDDEN);
    }

    @DisplayName("Elinks People endpoint status verification for resource not found status")
    @Test
    void test_get_people_return_response_status_404() throws JsonProcessingException  {

        int statusCode = 404;
        peopleApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();

        assertThat(response).containsEntry("http_status", "404");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_NOT_FOUND);
    }

    @DisplayName("Elinks Location endpoint status verification for bad request status")
    @Test
    void test_get_locations_return_response_status_400() throws JsonProcessingException {

        int statusCode = 400;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "400");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);

    }

    @DisplayName("Elinks Location endpoint status verification for unauthorized status")
    @Test
    void test_get_locations_return_response_status_401() throws JsonProcessingException {

        int statusCode = 401;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "401");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_UNAUTHORIZED);
    }

    @DisplayName("Elinks Location endpoint status verification for forbidden status")
    @Test
    void test_get_locations_return_response_status_403() throws JsonProcessingException {

        int statusCode = 403;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "403");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_FORBIDDEN);
    }

    @DisplayName("Elinks Location endpoint status verification for resource not found status")
    @Test
    void test_get_locations_return_response_status_404() throws JsonProcessingException {

        int statusCode = 404;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "404");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_NOT_FOUND);
    }


    @DisplayName("Elinks Location endpoint status verification for Too many requests status")
    @Test
    void test_get_locations_return_response_status_429() throws JsonProcessingException {

        int statusCode = 429;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "429");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);
    }

    @DisplayName("Elinks BaseLocation endpoint status verification for Too many requests status")
    @Test
    void test_get_baseLocations_return_response_status_429() throws JsonProcessingException {

        int statusCode = 429;
        baseLocationApi4xxResponse(statusCode,null);


        Map<String, Object> response = elinksReferenceDataClient.getBaseLocations();
        assertThat(response).containsEntry("http_status", "429");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);
    }

    @DisplayName("Elinks people to test JRD Audit Negative Scenario Functionality verification")
    @Test
    void verifyPeopleJrdAuditFunctionalityBadRequestScenario() {
        elinks.stubFor(get(urlPathMatching("/people"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("{"

                                + " }")));

        elinkSchedularAuditRepository.deleteAll();
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getPeoples();
        assertThat(leaversResponse).containsEntry("http_status", "400");
        String profiles = leaversResponse.get("response_body").toString();
        assertTrue(profiles.contains("Syntax error or Bad request"));

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);

        assertEquals(PEOPLEAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.FAILED.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    @DisplayName("peoples data exception and audit testing when base location id not present exclude appointments")
    @Test
    void verifyPeoplesJrdExceptionRecordsBaseLocationNotFoundScenario() {
        elinks.stubFor(get(urlPathMatching("/people"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("{"
                                + "    \"pagination\": {"
                                + "      \"results\": 1,"
                                + "      \"pages\": 1,"
                                + "      \"current_page\": 1,"
                                + "      \"results_per_page\": 1,"
                                + "      \"more_pages\": false"
                                + "  },"
                                + "  \"results\": ["
                                + "      {"
                                + "          \"id\": \"552da697-4b3d-4aed-9c22-1e903b70aead\","
                                + "          \"per_id\": 57818,"
                                + "          \"personal_code\": \"0049931063\","
                                + "          \"title\": \"Tribunal Judge\","
                                + "          \"known_as\": \"Tester\","
                                + "          \"surname\": \"TestAccount 2\","
                                + "          \"fullname\": \"Tribunal Judge Tester TestAccount 2\","
                                + "          \"post_nominals\": \"ABC\","
                                + "      \"email\": \"Tester2@judiciarystaging.onmicrosoft.com\","
                                + "          \"sex\": \"sex_unknown\","
                                + "          \"work_phone\": null,"
                                + "          \"disability\": false,"
                                + "          \"retirement_date\": null,"
                                + "          \"leaving_on\": null,"
                                + "          \"appointments\": ["
                                + "              {"
                                + "                  \"appointment_id\": 114325,"
                                + "                  \"role\": \"Tribunal Judge\","
                                + "                  \"role_name\": \"Tribunal Judge\","
                                + "                  \"role_name_id\": null,"
                                + "                  \"type\": \"Courts\","
                                + "                  \"court_name\": \"Worcester Combined Court - Crown\","
                                + "                  \"court_type\": \"Crown Court\","
                                + "                  \"circuit\": \"default\","
                                + "                  \"bench\": null,"
                                + "                  \"advisory_committee_area\": null,"
                                + "                  \"location\": \"National\","
                                + "                  \"base_location\": \"Worcester Combined Court - Crown\","
                                + "                  \"base_location_id\": 1000,"
                                + "                  \"is_principal\": false,"
                                + "                  \"start_date\": \"2022-06-29\","
                                + "                  \"end_date\": null,"
                                + "                  \"superseded\": true,"
                                + "                  \"contract_type\": \"fee_paid\","
                                + "                  \"contract_type_id\": 1,"
                                + "                  \"work_pattern\": \"Fee Paid Judiciary 5 Days Mon - Fri\","
                                + "                  \"work_pattern_id\": 10,"
                                + "                  \"fte_percent\": 100"
                                + "              },"
                                + "              {"
                                + "                  \"appointment_id\": 114329,"
                                + "                  \"role\": \"Magistrate\","
                                + "                  \"role_name\": \"Magistrate\","
                                + "                  \"role_name_id\": null,"
                                + "                   \"type\": \"Courts\","
                                + "                    \"court_name\": \"Central London County Court\","
                                + "                   \"court_type\": \"County Court\","
                                + "                  \"circuit\": \"default\","
                                + "                  \"bench\": null,"
                                + "                  \"advisory_committee_area\": null,"
                                + "                  \"location\": \"National\","
                                + "                  \"base_location\": \"Central London County Court\","
                                + "                  \"base_location_id\": 1200,"
                                + "                  \"is_principal\": true,"
                                + "                  \"start_date\": \"2022-07-27\","
                                + "                  \"end_date\": null,"
                                + "                  \"superseded\": false,"
                                + "                  \"contract_type\": \"salaried\","
                                + "                  \"contract_type_id\": 0,"
                                + "                  \"work_pattern\": \"Fee Paid Judiciary 5 Days Mon - Fri\","
                                + "                  \"work_pattern_id\": 10,"
                                + "                  \"fte_percent\": 100"
                                + "              }"
                                + "          ],"
                                + "          \"training_records\": [],"
                                + "          \"authorisations\": ["
                                + "              {"
                                + "                  \"jurisdiction\": \"Family\","
                                + "                  \"tickets\": ["
                                + "                      \"Private Law\""
                                + "                  ]"
                                + "              },"
                                + "              {"
                                + "                  \"jurisdiction\": \"Tribunals\","
                                + "                  \"tickets\": ["
                                + "                      \"05 - Industrial Injuries\""
                                + "                  ]"
                                + "              }"
                                + "          ],"
                                + "          \"authorisations_with_dates\": ["
                                + "              {"
                                + "                  \"authorisation_id\": 29701,"
                                + "                  \"jurisdiction\": \"Family\","
                                + "                  \"jurisdiction_id\": 26,"
                                + "                  \"ticket\": \"Private Law\","
                                + "                  \"ticket_id\": 315,"
                                + "                  \"start_date\": \"2022-07-03\","
                                + "                 \"end_date\": null"
                                + "                },"
                                + "                {"
                                + "                \"authorisation_id\": 29700,"
                                + "                 \"jurisdiction\": \"Tribunals\","
                                + "                 \"jurisdiction_id\": 27,"
                                + "                 \"ticket\": \"05 - Industrial Injuries\","
                                + "                 \"ticket_id\": 367,"
                                + "                 \"start_date\": \"2022-07-04\","
                                + "                 \"end_date\": null"
                                + "                }"
                                + "            ]"
                                + "        }"
                                + "    ]"
                                + " }")
                ));

        elinkSchedularAuditRepository.deleteAll();
        Map<String, Object> peoplesResponse = elinksReferenceDataClient.getPeoples();
        assertThat(peoplesResponse).containsEntry("http_status", "200 OK");

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();
        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);
        assertEquals(PEOPLEAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.PARTIAL_SUCCESS.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());

        List<ElinkDataExceptionRecords> elinksException = elinkDataExceptionRepository.findAll();
        ElinkDataExceptionRecords exceptionEntry = elinksException.get(0);
        assertEquals(BASE_LOCATION_ID, exceptionEntry.getFieldInError());
        assertNotNull(exceptionEntry.getSchedulerStartTime());

    }

    @DisplayName("Elinks Leavers to test JRD Audit Negative Scenario Functionality verification")
    @Test
    void verifyLeaversJrdAuditFunctionalityBadRequestScenario() {
        elinks.stubFor(get(urlPathMatching("/leavers"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody("{"

                                + " }")));

        elinkSchedularAuditRepository.deleteAll();
        Map<String, Object> leaversResponse = elinksReferenceDataClient.getLeavers();
        assertThat(leaversResponse).containsEntry("http_status", "400");
        String profiles = leaversResponse.get("response_body").toString();
        assertTrue(profiles.contains("Syntax error or Bad request"));

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);

        assertEquals(LEAVERSAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.FAILED.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    @DisplayName("Elinks Deleted to test JRD Audit Negative Scenario Functionality verification")
    @Test
    void verifyDeletedJrdAuditFunctionalityBadRequestScenario() {
        elinks.stubFor(get(urlPathMatching("/deleted"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withHeader("Connection", "close")
                .withBody("{"

                    + " }")));

        elinkSchedularAuditRepository.deleteAll();
        Map<String, Object> deletedResponse = elinksReferenceDataClient.getDeleted();
        assertThat(deletedResponse).containsEntry("http_status", "400");
        String profiles = deletedResponse.get("response_body").toString();
        assertTrue(profiles.contains("Syntax error or Bad request"));

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);

        assertEquals(DELETEDAPI, auditEntry.getApiName());
        assertEquals(RefDataElinksConstants.JobStatus.FAILED.getStatus(), auditEntry.getStatus());
        assertEquals(JUDICIAL_REF_DATA_ELINKS, auditEntry.getSchedulerName());
        assertNotNull(auditEntry.getSchedulerStartTime());
        assertNotNull(auditEntry.getSchedulerEndTime());
    }

    @DisplayName("test_get_leavers_with_wrong_endpoint_return_response_status_400()")
    @Test
    void test_get_leavers_return_response_status_400() throws JsonProcessingException  {

        int statusCode = 400;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "400");


        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);
    }

    @DisplayName("test_get_leavers_with_wrong_token_return_response_status_401()")
    @Test
    void test_get_leavers_return_response_status_401() throws JsonProcessingException  {

        int statusCode = 401;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "401");


        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_UNAUTHORIZED);
    }

    @DisplayName("test_get_leavers_return_with_invalid_token_response_status_403()")
    @Test
    void test_get_leavers_return_response_status_403() throws JsonProcessingException  {

        int statusCode = 403;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "403");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_FORBIDDEN);
    }

    @DisplayName("test_get_leavers_url_not_found_return_response_status_404()")
    @Test
    void test_get_leavers_return_response_status_404() throws JsonProcessingException  {

        int statusCode = 404;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "404");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_NOT_FOUND);
    }

    @DisplayName("test_get_leavers_exceeding_limit_return_response_status_429()")
    @Test
    void test_get_leavers_return_response_status_429() throws JsonProcessingException  {

        int statusCode = 429;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "429");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);
    }

    @DisplayName("test_get_leavers_missing_mandatory_param_return_response_status_400()")
    @Test
    void test_get_leavers_missing_mandatory_param_return_response_status_400() throws JsonProcessingException  {

        int statusCode = 400;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "400");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);

    }

    @DisplayName("test_get_leavers_future_since_then_return_response_status_400()")
    @Test
    void test_get_leavers_future_since_then_return_response_status_400() throws JsonProcessingException  {

        int statusCode = 400;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "400");


        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);
    }

    @DisplayName("test_get_deleted_with_wrong_endpoint_return_response_status_400()")
    @Test
    void test_get_deleted_return_response_status_400() throws JsonProcessingException  {
        int statusCode = 400;
        deletedApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getDeleted();

        assertThat(response).containsEntry("http_status", "400");
        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);
    }

    @DisplayName("test_get_deleted_with_wrong_token_return_response_status_401()")
    @Test
    void test_get_deleted_return_response_status_401() throws JsonProcessingException  {

        int statusCode = 401;
        deletedApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getDeleted();

        assertThat(response).containsEntry("http_status", "401");


        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_UNAUTHORIZED);
    }

    @DisplayName("test_get_deleted_return_with_invalid_token_response_status_403()")
    @Test
    void test_get_deleted_return_response_status_403() throws JsonProcessingException  {

        int statusCode = 403;
        deletedApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getDeleted();

        assertThat(response).containsEntry("http_status", "403");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_FORBIDDEN);
    }

    @DisplayName("test_get_deleted_url_not_found_return_response_status_404()")
    @Test
    void test_get_deleted_return_response_status_404() throws JsonProcessingException  {

        int statusCode = 404;
        deletedApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getDeleted();

        assertThat(response).containsEntry("http_status", "404");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_NOT_FOUND);
    }

    @DisplayName("test_get_deleted_exceeding_limit_return_response_status_429()")
    @Test
    void test_get_deleted_return_response_status_429() throws JsonProcessingException  {

        int statusCode = 429;
        deletedApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getDeleted();

        assertThat(response).containsEntry("http_status", "429");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS);
    }

    @DisplayName("test_get_deleted_missing_mandatory_param_return_response_status_400()")
    @Test
    void test_get_deleted_missing_mandatory_param_return_response_status_400() throws JsonProcessingException  {

        int statusCode = 400;
        deletedApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getDeleted();

        assertThat(response).containsEntry("http_status", "400");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);

    }

    @DisplayName("test_get_deleted_future_since_then_return_response_status_400()")
    @Test
    void test_get_deleted_future_since_then_return_response_status_400() throws JsonProcessingException  {

        int statusCode = 400;
        deletedApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getDeleted();

        assertThat(response).containsEntry("http_status", "400");

        assertThat(response.get("response_body").toString()).contains(ELINKS_ERROR_RESPONSE_BAD_REQUEST);
    }

    @DisplayName("Idam_return_with_invalid_token_response_status_403")
    @Test
    void test_get_idam_return_response_status_403() throws JsonProcessingException {

        int statusCode = 403;
        idamSearchApi4xxResponse(statusCode, "[]");
        initialize();
        Map<String, Object> response  = elinksReferenceDataClient.getIdamElasticSearch();

        assertEquals(response.get("http_status"),String.valueOf(statusCode));

        assertThat(response.get("response_body").toString()).contains(IDAM_ERROR_MESSAGE);
    }

    @DisplayName("Idam_url_not_found_return_response_status_404")
    @Test
    void test_get_idam_url_not_found_return_response_status_404() throws JsonProcessingException {

        int statusCode = 404;
        idamSearchApi4xxResponse(statusCode, "[]");

        initialize();
        Map<String, Object> response  = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals(response.get("http_status"),String.valueOf(statusCode));
    }

    @DisplayName("Idam_unauthorised_return_response_status_401")
    @Test
    void test_get_idam_unauthorised_return_response_status_401() throws JsonProcessingException {

        int statusCode = 401;
        idamSearchApi4xxResponse(statusCode,"[]");

        initialize();

        Map<String, Object> response  = elinksReferenceDataClient.getIdamElasticSearch();
        assertEquals(response.get("http_status"),String.valueOf(statusCode));
    }

    private void peopleApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/people"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")

                        .withBody(body)
                        .withTransformers("user-token-response")));
    }

    private void locationApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)
                        .withTransformers("user-token-response")));
    }

    private void baseLocationApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/reference_data/base_location"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)
                        .withTransformers("user-token-response")));
    }

    private void leaversApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/leavers"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")

                        .withBody(body)
                        .withTransformers("user-token-response")));
    }

    private void deletedApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/deleted"))
            .willReturn(aResponse()
                .withStatus(statusCode)
                .withHeader("Content-Type", "application/json")
                .withHeader("Connection", "close")

                .withBody(body)
                .withTransformers("user-token-response")));
    }

    private void idamSearchApi4xxResponse(int statusCode, String body) {
        sidamService.stubFor(get(urlPathMatching("/api/v1/users"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)));
    }

    private void initialize() {
        final String clientId = "234342332";
        final String redirectUri = "http://idam-api.aat.platform.hmcts.net";
        //The authorization and clientAuth is the dummy value which we can evaluate using BASE64 encoder.
        final String authorization = "ZHVtbXl2YWx1ZUBobWN0cy5uZXQ6SE1DVFMxMjM0";
        final String clientAuth = "cmQteHl6LWFwaTp4eXo=";
        final String url = "http://127.0.0.1:5000";
        tokenConfigProperties.setClientId(clientId);
        tokenConfigProperties.setClientAuthorization(clientAuth);
        tokenConfigProperties.setAuthorization(authorization);
        tokenConfigProperties.setRedirectUri(redirectUri);
        tokenConfigProperties.setUrl(url);

    }
}