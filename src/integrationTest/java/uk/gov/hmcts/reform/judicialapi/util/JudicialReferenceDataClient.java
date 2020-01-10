package uk.gov.hmcts.reform.judicialapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class JudicialReferenceDataClient {

    private static final String APP_BASE_PATH = "/refdata/v1/judicial";
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    private final int jrdApiPort;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private String baseUrl;

    public JudicialReferenceDataClient(int jrdApiPort) {
        this.jrdApiPort = jrdApiPort;
        this.baseUrl = "http://localhost:" + jrdApiPort + APP_BASE_PATH;
    }

    public Map<String,Object> retrieveAllJudicialRoleTypes(String role) {
        return getRequest(APP_BASE_PATH + "/roles", role);
    }


    public Map<String, Object> findJudicialUserProfileByUserEmail(String email,String role) {
        return getRequest("/user?email={email}", email,role);
    }

    private HttpHeaders getS2sTokenHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("ServiceAuthorization", JWT_TOKEN);
        return headers;
    }

    private Map getResponse(ResponseEntity<Map> responseEntity) {

        Map response = objectMapper
                .convertValue(
                        responseEntity.getBody(),
                        Map.class);

        response.put("http_status", responseEntity.getStatusCode().toString());
        response.put("headers", responseEntity.getHeaders().toString());


        return response;
    }

    private Map<String, Object> getRequest(String uriPath, String role, Object... params) {

        ResponseEntity<Map> responseEntity;

        try {
            HttpEntity<?> request = new HttpEntity<>(getMultipleAuthHeaders(role));
            responseEntity = restTemplate
                    .exchange("http://localhost:" + jrdApiPort + uriPath,
                            HttpMethod.GET,
                            request,
                            Map.class,
                            params);
        } catch (HttpStatusCodeException ex) {
            HashMap<String, Object> statusAndBody = new HashMap<>(2);
            statusAndBody.put("http_status", String.valueOf(ex.getRawStatusCode()));
            statusAndBody.put("response_body", ex.getResponseBodyAsString());
            return statusAndBody;
        }

        return getResponse(responseEntity);
    }

    private HttpHeaders getMultipleAuthHeaders(String role) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        headers.add("ServiceAuthorization", JWT_TOKEN);

        headers.add("Authorization", role);

        return headers;
    }
}
