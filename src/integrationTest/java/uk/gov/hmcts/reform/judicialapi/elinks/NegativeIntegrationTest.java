package uk.gov.hmcts.reform.judicialapi.elinks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.judicialapi.controller.advice.ErrorResponse;
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

class NegativeIntegrationTest extends ElinksEnabledIntegrationTest {



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

}