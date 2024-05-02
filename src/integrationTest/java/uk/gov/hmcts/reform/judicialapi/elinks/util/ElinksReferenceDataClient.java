package uk.gov.hmcts.reform.judicialapi.elinks.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
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
import uk.gov.hmcts.reform.judicialapi.elinks.controller.request.RefreshRoleRequest;
import uk.gov.hmcts.reform.judicialapi.versions.V2;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.generateToken;

@Slf4j
@PropertySource(value = "/integrationTest/resources/application.yml")
public class ElinksReferenceDataClient {

    private static final String APP_BASE_PATH = "/refdata/internal/elink";
    private static final String SERVICE_AUTH_HEADER = "ServiceAuthorization";
    private static final String IDAM_AUTH_HEADER = "Authorization";
    private static final String JRD_SYSTEM_USER_ROLE = "jrd-system-user";
    static String idamAuthToken;
    static String bearerToken;
    private static String s2sToken = null;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String serviceName;
    private final String baseUrl;
    private final String issuer;
    private final long expiration;
    private final int port;

    public ElinksReferenceDataClient(int port, String issuer, Long tokenExpirationInterval, String serviceName) {
        this.baseUrl = "http://localhost:" + port + APP_BASE_PATH;
        this.port = port;
        this.issuer = issuer;
        this.expiration = tokenExpirationInterval;
        this.serviceName = serviceName;
    }

    public static void setIdamAuthToken(String idamAuthToken) {
        ElinksReferenceDataClient.idamAuthToken = idamAuthToken;
    }

    public static String generateS2SToken(String serviceName) {
        return Jwts.builder()
                .setSubject(serviceName)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode("AA"))
                .compact();
    }

    public ValidatableResponse loadPeopleData() {
        return withAuthenticatedUser()
                .get("/people")
                .then();
    }

    public ValidatableResponse elasticSearchLoadSidamIdsByObjectIds() {
        return withAuthenticatedUser()
                .get("/idam/elastic/search")
                .then();
    }

    public ValidatableResponse findSidamIdsByObjectIds() {
        return withAuthenticatedUser()
                .get("/idam/find")
                .then();
    }

    public ValidatableResponse publishSidamIds() {
        return withAuthenticatedUser()
                .get("/sidam/asb/publish")
                .then();
    }

    public ValidatableResponse loadLocationData() {
        return withAuthenticatedUser()
                .get("/reference_data/location")
                .then();
    }

    public ValidatableResponse loadLeaversData() {
        return withAuthenticatedUser()
                .get("/leavers")
                .then();
    }

    public ValidatableResponse loadDeletedData() {
        return withAuthenticatedUser()
                .get("/deleted")
                .then();
    }

    private Map<String, Object> getResponse(ResponseEntity<?> responseEntity) {

        var response = new HashMap();

        response.put("http_status", responseEntity.getStatusCode().toString());
        response.put("headers", responseEntity.getHeaders().toString());
        response.put("body", responseEntity.getBody());
        return response;
    }

    private RequestSpecification withAuthenticatedUser() {

        if (StringUtils.isBlank(s2sToken)) {
            s2sToken = generateS2SToken(serviceName);
        }
        if (StringUtils.isBlank(idamAuthToken)) {
            idamAuthToken = "Bearer ".concat(getBearerToken(UUID.randomUUID().toString(), JRD_SYSTEM_USER_ROLE));
        }

        return SerenityRest.with()
                .relaxedHTTPSValidation()
                .baseUri(baseUrl)
                .header("Content-Type", MediaType.valueOf(V2.MediaType.SERVICE))
                .header("Accepts", APPLICATION_JSON_VALUE)
                .header(SERVICE_AUTH_HEADER, "Bearer " + s2sToken)
                .header(IDAM_AUTH_HEADER, "Bearer " + idamAuthToken);
    }

    @NotNull
    private HttpHeaders getInvalidAuthHeaders(MediaType value) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        headers.add("ServiceAuthorization", "Invalid token");

        if (StringUtils.isBlank(bearerToken)) {
            bearerToken = "Bearer ".concat("invalid token");
        }
        headers.add("Authorization", bearerToken);

        return headers;
    }

    private String getBearerToken(String userId, String role) {
        return generateToken(issuer, expiration, userId, role);
    }

    private void additionalHeaders(Integer pageSize, Integer pageNumber, String sortDirection,
                                   String sortColumn, HttpHeaders headers) {
        headers.add("page_size", String.valueOf(pageSize));
        headers.add("page_number", String.valueOf(pageNumber));
        headers.add("sort_direction", sortDirection);
        headers.add("sort_column", sortColumn);
    }

    public void clearTokens() {
        s2sToken = null;
        bearerToken = null;
    }

    public String getAndReturnBearerToken(String userId, String role) {
        if (StringUtils.isBlank(s2sToken)) {
            s2sToken = generateS2SToken("rd_judicial_api");
        }

        if (StringUtils.isBlank(bearerToken)) {
            bearerToken = "Bearer ".concat(getBearerToken(Objects.isNull(userId) ? UUID.randomUUID().toString()
                    : userId, role));
        }
        return bearerToken;
    }

    public Map<String, Object> refreshUserProfile(RefreshRoleRequest refreshRoleRequest, Integer pageSize,
                                                  Integer pageNumber, String sortDirection, String sortColumn,
                                                  String role, boolean invalidTokens) {

        ResponseEntity<Object> responseEntity;
        HttpEntity<?> request =
                new HttpEntity<Object>(refreshRoleRequest,
                        invalidTokens ? getInvalidAuthHeaders(
                                MediaType.valueOf(V2.MediaType.SERVICE)) :
                                getMultipleAuthHeadersForRefreshUserProfile(role,
                                        pageSize, pageNumber,
                                        sortDirection, sortColumn, MediaType.valueOf(V2.MediaType.SERVICE)));

        try {

            String url = "http://localhost:" + port + "/refdata/judicial/users";
            responseEntity = restTemplate.exchange(
                    url,
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

    @NotNull
    private HttpHeaders getMultipleAuthHeadersForRefreshUserProfile(String role,
                                                                    Integer pageSize, Integer pageNumber,
                                                                    String sortDirection, String sortColumn,
                                                                    MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setAccept(List.of(mediaType));
        if (StringUtils.isBlank(s2sToken)) {
            s2sToken = generateS2SToken(serviceName);
        }

        headers.add("ServiceAuthorization", s2sToken);

        if (StringUtils.isBlank(bearerToken)) {
            bearerToken = "Bearer ".concat(getBearerToken(UUID.randomUUID().toString(), role));
        }
        headers.add("Authorization", bearerToken);
        additionalHeaders(pageSize, pageNumber, sortDirection, sortColumn, headers);
        return headers;
    }

}
