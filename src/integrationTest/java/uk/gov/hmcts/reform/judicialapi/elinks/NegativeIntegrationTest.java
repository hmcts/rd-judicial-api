package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.BaseLocation;
import uk.gov.hmcts.reform.judicialapi.elinks.domain.ElinkDataSchedularAudit;
import uk.gov.hmcts.reform.judicialapi.elinks.repository.BaseLocationRepository;
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
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.BASELOCATIONAPI;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.JUDICIAL_REF_DATA_ELINKS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.LEAVERSAPI;

class NegativeIntegrationTest extends ElinksEnabledIntegrationTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private BaseLocationRepository baseLocationRepository;

    @Autowired
    private ElinkSchedularAuditRepository elinkSchedularAuditRepository;


    @Test
    void test_get_people_return_response_status_400() throws JsonProcessingException  {

        int statusCode = 400;
        peopleApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();

        assertThat(response).containsEntry("http_status", "400");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_BAD_REQUEST, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_people_return_response_status_401() throws JsonProcessingException  {

        int statusCode = 401;
        peopleApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();

        assertThat(response).containsEntry("http_status", "401");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_UNAUTHORIZED, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_people_return_response_status_403() throws JsonProcessingException  {

        int statusCode = 403;
        peopleApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();

        assertThat(response).containsEntry("http_status", "403");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_FORBIDDEN, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_people_return_response_status_404() throws JsonProcessingException  {

        int statusCode = 404;
        peopleApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();

        assertThat(response).containsEntry("http_status", "404");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_NOT_FOUND, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_people_return_response_status_429() throws JsonProcessingException  {

        int statusCode = 429;
        peopleApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getPeoples();

        assertThat(response).containsEntry("http_status", "429");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS, errorDetails.getErrorMessage());
    }


    @Test
    void test_get_locations_return_response_status_400() throws JsonProcessingException {

        int statusCode = 400;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "400");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_BAD_REQUEST, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_locations_return_response_status_401() throws JsonProcessingException {

        int statusCode = 401;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "401");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_UNAUTHORIZED, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_locations_return_response_status_403() throws JsonProcessingException {

        int statusCode = 403;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "403");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_FORBIDDEN, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_locations_return_response_status_404() throws JsonProcessingException {

        int statusCode = 404;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "404");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_NOT_FOUND, errorDetails.getErrorMessage());
    }


    @Test
    void test_get_locations_return_response_status_429() throws JsonProcessingException {

        int statusCode = 429;
        locationApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLocations();
        assertThat(response).containsEntry("http_status", "429");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_baseLocations_return_response_status_400() throws JsonProcessingException {

        int statusCode = 400;
        baseLocationApi4xxResponse(statusCode,null);


        Map<String, Object> response = elinksReferenceDataClient.getBaseLocations();
        assertThat(response).containsEntry("http_status", "400");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_BAD_REQUEST, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_baseLocations_return_response_status_401() throws JsonProcessingException {

        int statusCode = 401;
        baseLocationApi4xxResponse(statusCode,null);


        Map<String, Object> response = elinksReferenceDataClient.getBaseLocations();
        assertThat(response).containsEntry("http_status", "401");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_UNAUTHORIZED, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_baseLocations_return_response_status_403() throws JsonProcessingException {

        int statusCode = 403;
        baseLocationApi4xxResponse(statusCode,null);


        Map<String, Object> response = elinksReferenceDataClient.getBaseLocations();
        assertThat(response).containsEntry("http_status", "403");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_FORBIDDEN, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_baseLocations_return_response_status_404() throws JsonProcessingException {

        int statusCode = 404;
        baseLocationApi4xxResponse(statusCode,null);


        Map<String, Object> response = elinksReferenceDataClient.getBaseLocations();
        assertThat(response).containsEntry("http_status", "404");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_NOT_FOUND, errorDetails.getErrorMessage());
    }

    @Test
    void test_get_baseLocations_return_response_status_429() throws JsonProcessingException {

        int statusCode = 429;
        baseLocationApi4xxResponse(statusCode,null);


        Map<String, Object> response = elinksReferenceDataClient.getBaseLocations();
        assertThat(response).containsEntry("http_status", "429");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS, errorDetails.getErrorMessage());
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

    @DisplayName("Elinks Base Locations to test JRD Audit Negative Scenario Functionality verification")
    @Test
    void verifyBaseLocationJrdAuditFunctionalityBadRequestScenario() {
        elinks.stubFor(get(urlPathMatching("/reference_data/base_location"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withHeader("Connection", "close")
                .withBody("{"
                    + " }")));
        elinkSchedularAuditRepository.deleteAll();
        Map<String, Object> baseLocationResponse = elinksReferenceDataClient.getBaseLocations();
        assertThat(baseLocationResponse).containsEntry("http_status", "400");
        String baseLocations = baseLocationResponse.get("response_body").toString();
        assertTrue(baseLocations.contains("Syntax error or Bad request"));

        List<BaseLocation> baseLocationList = baseLocationRepository.findAll();

        assertEquals(1, baseLocationList.size());
        assertEquals("0", baseLocationList.get(0).getBaseLocationId());
        assertEquals("default", baseLocationList.get(0).getCourtName());
        assertEquals("default", baseLocationList.get(0).getCourtType());
        assertEquals("default", baseLocationList.get(0).getCircuit());
        assertEquals("default", baseLocationList.get(0).getAreaOfExpertise());

        List<ElinkDataSchedularAudit> elinksAudit = elinkSchedularAuditRepository.findAll();

        ElinkDataSchedularAudit auditEntry = elinksAudit.get(0);

        assertEquals(BASELOCATIONAPI, auditEntry.getApiName());
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
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_BAD_REQUEST, errorDetails.getErrorMessage());
    }

    @DisplayName("test_get_leavers_with_wrong_token_return_response_status_401()")
    @Test
    void test_get_leavers_return_response_status_401() throws JsonProcessingException  {

        int statusCode = 401;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "401");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_UNAUTHORIZED, errorDetails.getErrorMessage());
    }

    @DisplayName("test_get_leavers_return_with_invalid_token_response_status_403()")
    @Test
    void test_get_leavers_return_response_status_403() throws JsonProcessingException  {

        int statusCode = 403;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "403");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_FORBIDDEN, errorDetails.getErrorMessage());
    }

    @DisplayName("test_get_leavers_url_not_found_return_response_status_404()")
    @Test
    void test_get_leavers_return_response_status_404() throws JsonProcessingException  {

        int statusCode = 404;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "404");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_NOT_FOUND, errorDetails.getErrorMessage());
    }

    @DisplayName("test_get_leavers_exceeding_limit_return_response_status_429()")
    @Test
    void test_get_leavers_return_response_status_429() throws JsonProcessingException  {

        int statusCode = 429;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "429");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS, errorDetails.getErrorMessage());
    }

    @DisplayName("test_get_leavers_missing_mandatory_param_return_response_status_400()")
    @Test
    void test_get_leavers_missing_mandatory_param_return_response_status_400() throws JsonProcessingException  {

        int statusCode = 400;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "400");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_BAD_REQUEST, errorDetails.getErrorMessage());
    }

    @DisplayName("test_get_leavers_future_since_then_return_response_status_400()")
    @Test
    void test_get_leavers_future_since_then_return_response_status_400() throws JsonProcessingException  {

        int statusCode = 400;
        leaversApi4xxResponse(statusCode,null);

        Map<String, Object> response = elinksReferenceDataClient.getLeavers();

        assertThat(response).containsEntry("http_status", "400");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_BAD_REQUEST, errorDetails.getErrorMessage());
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

}