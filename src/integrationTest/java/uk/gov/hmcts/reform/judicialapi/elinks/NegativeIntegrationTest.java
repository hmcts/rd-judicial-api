package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.IdamTokenConfigProperties;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinksEnabledIntegrationTest;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_BAD_REQUEST;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_FORBIDDEN;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_NOT_FOUND;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.ELINKS_ERROR_RESPONSE_UNAUTHORIZED;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.IDAM_ERROR_MESSAGE;

class NegativeIntegrationTest extends ElinksEnabledIntegrationTest {


    @Autowired
    IdamTokenConfigProperties tokenConfigProperties;

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
        baseLocationApi4xxResponse(statusCode,"{ }");

        Map<String, Object> response = elinksReferenceDataClient.getBaseLocations();
        assertThat(response).containsEntry("http_status", "429");
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(ELINKS_ERROR_RESPONSE_TOO_MANY_REQUESTS, errorDetails.getErrorMessage());
    }

    @DisplayName("Idam_return_with_invalid_token_response_status_403")
    @Test
    void test_get_idam_return_response_status_403() throws JsonProcessingException {

        int statusCode = 403;
        idamSearchApi4xxResponse(statusCode, "[]");
        initialize();
        Map<String, Object> response  = elinksReferenceDataClient.getIdamElasticSearch();

        assertEquals(response.get("http_status"),String.valueOf(statusCode));
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorDetails = objectMapper
                .readValue(response.get("response_body").toString(),ErrorResponse.class);

        assertEquals(IDAM_ERROR_MESSAGE, errorDetails.getErrorMessage());
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

    @DisplayName("Idam_url_not_found_return_response_status_401")
    @Test
    void test_get_idam_url_not_found_return_response_status_401() throws JsonProcessingException {

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
                        .withBody(body)));
    }

    private void locationApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/reference_data/location"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)));
    }

    private void baseLocationApi4xxResponse(int statusCode, String body) {
        elinks.stubFor(get(urlPathMatching("/reference_data/base_location"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Connection", "close")
                        .withBody(body)));
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
        final String authorization = "c2hyZWVkaGFyLmxvbXRlQGhtY3RzLm5ldDpITUNUUzEyMzQ=";
        final String clientAuth = "cmQteHl6LWFwaTp4eXo=";
        final String url = "http://127.0.0.1:5000";
        tokenConfigProperties.setClientId(clientId);
        tokenConfigProperties.setClientAuthorization(clientAuth);
        tokenConfigProperties.setAuthorization(authorization);
        tokenConfigProperties.setRedirectUri(redirectUri);
        tokenConfigProperties.setUrl(url);

    }
}