package uk.gov.hmcts.reform.judicialapi.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
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
import uk.gov.hmcts.reform.judicialapi.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserSearchRequest;
import uk.gov.hmcts.reform.judicialapi.versions.V1;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.generateToken;

@Slf4j
@PropertySource(value = "/integrationTest/resources/application.yml")
public class JudicialReferenceDataClient {

    private static final String APP_BASE_PATH = "/refdata/judicial";
    static String bearerToken;
    private static String JWT_TOKEN = null;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String serviceName;
    private final String baseUrl;
    private final String issuer;
    private final long expiration;

    public JudicialReferenceDataClient(int port, String issuer, Long tokenExpirationInterval, String serviceName) {
        this.baseUrl = "http://localhost:" + port + APP_BASE_PATH;
        this.issuer = issuer;
        this.expiration = tokenExpirationInterval;
        this.serviceName = serviceName;
    }

    public static String generateS2SToken(String serviceName) {
        return Jwts.builder()
                .setSubject(serviceName)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode("AA"))
                .compact();
    }

    public static void setBearerToken(String bearerToken) {
        JudicialReferenceDataClient.bearerToken = bearerToken;
    }

    public Map<String, Object> fetchJudicialProfilesById(Integer pageSize, Integer pageNumber,
                                                         UserRequest userRequest, String role, boolean invalidTokens) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/fetch");

        if (nonNull(pageSize)) {
            stringBuilder.append("?page_size=");
            stringBuilder.append(pageSize);
        }
        if (nonNull(pageNumber)) {
            stringBuilder.append("&page_number=");
            stringBuilder.append(pageNumber);
        }

        ResponseEntity<Object> responseEntity;
        HttpEntity<?> request =
                new HttpEntity<Object>(userRequest, invalidTokens
                        ? getInvalidAuthHeaders(MediaType.valueOf(V1.MediaType.SERVICE), role,
                        null, MediaType.valueOf(V1.MediaType.SERVICE)) :
                        getMultipleAuthHeaders(MediaType.valueOf(V1.MediaType.SERVICE), role,
                                null, MediaType.valueOf(V1.MediaType.SERVICE)));

        try {

            responseEntity = restTemplate.exchange(
                    baseUrl + "/users" + stringBuilder,
                    HttpMethod.POST, request,
                    Object.class
            );

        } catch (RestClientResponseException ex) {
            HashMap<String, Object> statusAndBody = new HashMap<>(2);
            statusAndBody.put("http_status", String.valueOf(ex.getRawStatusCode()));
            statusAndBody.put("response_body", ex.getResponseBodyAsString());
            return statusAndBody;
        }

        return getResponse(responseEntity);
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
        return generateToken(issuer, expiration, userId, role);
    }

    public Map<String, Object> searchUsers(UserSearchRequest userSearchRequest, String role,
                                           boolean invalidTokens,MediaType mediaType,MediaType accept) {
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

    public Map<String, Object> refreshUserProfile(RefreshRoleRequest refreshRoleRequest, Integer pageSize,
                                                  Integer pageNumber, String sortDirection, String sortColumn,
                                                  String role, boolean invalidTokens) {

        var stringBuilder = new StringBuilder();

        ResponseEntity<Object> responseEntity;
        HttpEntity<?> request =
                new HttpEntity<Object>(refreshRoleRequest,
                        invalidTokens ? getInvalidAuthHeaders(
                            MediaType.valueOf(V1.MediaType.SERVICE),role,
                            null,MediaType.valueOf(V1.MediaType.SERVICE)) :
                                getMultipleAuthHeadersForRefreshUserProfile(role, null,
                                        pageSize, pageNumber,
                                        sortDirection, sortColumn));

        try {

            responseEntity = restTemplate.exchange(
                    baseUrl + "/users" + stringBuilder,
                    HttpMethod.POST, request,
                    Object.class
            );

        } catch (RestClientResponseException ex) {
            var statusAndBody = new HashMap<String, Object>(2);
            statusAndBody.put("http_status", String.valueOf(ex.getRawStatusCode()));
            statusAndBody.put("response_body", ex.getResponseBodyAsString());
            return statusAndBody;
        }

        return getResponse(responseEntity);
    }

    private void additionalHeaders(Integer pageSize, Integer pageNumber, String sortDirection,
                                   String sortColumn, HttpHeaders headers) {
        headers.add("page_size", String.valueOf(pageSize));
        headers.add("page_number", String.valueOf(pageNumber));
        headers.add("sort_direction", sortDirection);
        headers.add("sort_column", sortColumn);
    }

    @NotNull
    private HttpHeaders getMultipleAuthHeadersForRefreshUserProfile(String role, String userId,
                                                                    Integer pageSize, Integer pageNumber,
                                                                    String sortDirection, String sortColumn) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        if (StringUtils.isBlank(JWT_TOKEN)) {

            JWT_TOKEN = generateS2SToken(serviceName);
        }

        headers.add("ServiceAuthorization", JWT_TOKEN);

        if (StringUtils.isBlank(bearerToken)) {
            bearerToken = "Bearer ".concat(getBearerToken(Objects.isNull(userId) ? UUID.randomUUID().toString()
                    : userId, role));
        }
        headers.add("Authorization", bearerToken);
        additionalHeaders(pageSize, pageNumber, sortDirection, sortColumn, headers);
        return headers;
    }


    public void clearTokens() {
        JWT_TOKEN = null;
        bearerToken = null;
    }
}
