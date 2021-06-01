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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.judicialapi.controller.request.UserRequest;

import java.util.Date;
import java.util.HashMap;
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
    private static String JWT_TOKEN = null;
    private final RestTemplate restTemplate = new RestTemplate();
    static String bearerToken;
    private String serviceName;
    private String baseUrl;
    private String issuer;
    private long expiration;

    public JudicialReferenceDataClient(int port, String issuer, Long tokenExpirationInterval, String serviceName) {
        this.baseUrl = "http://localhost:" + port + APP_BASE_PATH;
        this.issuer = issuer;
        this.expiration = tokenExpirationInterval;
        this.serviceName = serviceName;
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
                new HttpEntity<Object>(userRequest, invalidTokens ? getInvalidAuthHeaders(role, null) :
                        getMultipleAuthHeaders(role, null));

        try {

            responseEntity = restTemplate.exchange(
                    baseUrl + "/users" + stringBuilder.toString(),
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

        Map<String, Object> response = new HashMap();

        response.put("http_status", responseEntity.getStatusCode().toString());
        response.put("headers", responseEntity.getHeaders().toString());
        response.put("body", responseEntity.getBody());
        return response;
    }

    @NotNull
    private HttpHeaders getMultipleAuthHeaders(String role, String userId) {
        HttpHeaders headers = new HttpHeaders();
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
        return headers;
    }

    @NotNull
    private HttpHeaders getInvalidAuthHeaders(String role, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        headers.add("ServiceAuthorization", "Invalid token");

        if (StringUtils.isBlank(bearerToken)) {
            bearerToken = "Bearer ".concat("invalid token");
        }
        headers.add("Authorization", bearerToken);

        return headers;
    }

    public static String generateS2SToken(String serviceName) {
        return Jwts.builder()
                .setSubject(serviceName)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode("AA"))
                .compact();
    }

    private final String getBearerToken(String userId, String role) {
        return generateToken(issuer, expiration, userId, role);
    }

    public static void setBearerToken(String bearerToken) {
        JudicialReferenceDataClient.bearerToken = bearerToken;
    }
}
