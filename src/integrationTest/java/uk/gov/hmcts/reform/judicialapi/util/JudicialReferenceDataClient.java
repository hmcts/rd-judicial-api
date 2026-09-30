package uk.gov.hmcts.reform.judicialapi.util;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.UserSearchRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.generateAuthToken;
import static uk.gov.hmcts.reform.judicialapi.util.RefDataConstants.SERVICE_AUTHORIZATION;
import static uk.gov.hmcts.reform.judicialapi.wiremock.S2sWireMockStubs.RD_JUDICIAL_API;

@Slf4j
@PropertySource(value = "/integrationTest/resources/application.yml")
public class JudicialReferenceDataClient {

    public static final String APP_BASE_PATH = "/refdata/judicial";
    static String bearerToken;
    private static String JWT_TOKEN = null;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String serviceName;
    private final String baseUrl;
    private final String issuer;

    public JudicialReferenceDataClient(int port, String issuer, Long tokenExpirationInterval, String serviceName) {
        this.baseUrl = "http://localhost:" + port + APP_BASE_PATH;
        this.issuer = issuer;
        this.serviceName = serviceName;
    }

    public static String generateS2SToken(String serviceName) {
        return Jwts.builder()
                .subject(serviceName)
                .issuedAt(new Date())
                .signWith(Jwts.SIG.HS256.key().build())
                .compact();
    }

    public static void setBearerToken(String bearerToken) {
        JudicialReferenceDataClient.bearerToken = bearerToken;
    }

    private Map<String, Object> getResponse(ResponseEntity<Object> responseEntity) {

        var response = new HashMap();

        response.put("http_status", responseEntity.getStatusCode().toString());
        response.put("headers", responseEntity.getHeaders().toString());
        response.put("body", responseEntity.getBody());
        return response;
    }

    public String setAndReturnJwtToken() {
        if (StringUtils.isBlank(JWT_TOKEN)) {
            JWT_TOKEN = generateS2SToken("rd_judicial_api");
        }
        return JWT_TOKEN;
    }

    public String getAndReturnBearerToken(String userId, String role) {
        setAndReturnJwtToken();
        if (StringUtils.isBlank(bearerToken)) {
            bearerToken = "Bearer ".concat(getBearerToken(Objects.isNull(userId) ? UUID.randomUUID().toString()
                    : userId, role));
        }
        return bearerToken;
    }

    @NotNull
    private HttpHeaders getMultipleAuthHeaders(MediaType value, String role, String userId, MediaType accept) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(value);
        headers.setAccept(List.of(accept));
        if (StringUtils.isBlank(JWT_TOKEN)) {

            JWT_TOKEN = generateS2SToken(serviceName);
        }
        getAndReturnBearerToken(userId, role);

        headers.add("ServiceAuthorization", JWT_TOKEN);

        headers.add("Authorization", bearerToken);
        return headers;
    }

    @NotNull
    private HttpHeaders getInvalidAuthHeaders(MediaType value, String role, String userId, MediaType accept) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(value);
        headers.setAccept(List.of(accept));
        if (StringUtils.isBlank(JWT_TOKEN)) {

            JWT_TOKEN = generateS2SToken("Invalid token");
        }

        headers.add("ServiceAuthorization", JWT_TOKEN);

        if (StringUtils.isBlank(bearerToken)) {
            bearerToken = "Bearer ".concat("invalid token");
        }
        getAndReturnBearerToken(userId, role);
        headers.add("Authorization", bearerToken);

        return headers;
    }

    private String getBearerToken(String userId, String role) {
        return generateAuthToken(issuer, false, userId, role);
    }

    public static HttpHeaders getHttpHeaders(String issuer, boolean isExpired, String userId, String role) {
        HttpHeaders headers = new HttpHeaders();
        var userAuthToken = generateAuthToken(issuer, isExpired, userId, role);
        headers.setBearerAuth(userAuthToken);
        headers.add(SERVICE_AUTHORIZATION, "Bearer " + generateS2SToken(RD_JUDICIAL_API));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public Map<String, Object> searchUsers(UserSearchRequest userSearchRequest, String role,
                                           boolean invalidTokens, MediaType mediaType, MediaType accept) {
        ResponseEntity<Object> responseEntity;
        var request =
                new HttpEntity<Object>(userSearchRequest, invalidTokens ? getInvalidAuthHeaders(
                    mediaType,role, null,accept) :
                        getMultipleAuthHeaders(mediaType,role, null,accept));

        try {
            responseEntity = restTemplate.exchange(baseUrl + "/users/search", HttpMethod.POST, request, Object.class
            );

        } catch (RestClientResponseException ex) {
            var statusAndBody = new HashMap<String, Object>(2);
            statusAndBody.put("http_status", String.valueOf(ex.getRawStatusCode()));
            statusAndBody.put("response_body", ex.getResponseBodyAsString());
            return statusAndBody;
        }

        return getResponse(responseEntity);
    }

    public void clearTokens() {
        JWT_TOKEN = null;
        bearerToken = null;
    }
}
