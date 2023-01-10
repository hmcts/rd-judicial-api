package uk.gov.hmcts.reform.judicialapi.elinks.util;

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
import uk.gov.hmcts.reform.judicialapi.elinks.response.ElinkPeopleWrapperResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.gov.hmcts.reform.judicialapi.util.JwtTokenUtil.generateToken;

@Slf4j
@PropertySource(value = "/integrationTest/resources/application.yml")
public class ElinksReferenceDataClient {

    private static final String APP_BASE_PATH = "/refdata/internal/elink";
    private static String JWT_TOKEN = null;
    private final RestTemplate restTemplate = new RestTemplate();
    static String bearerToken;
    private final String  serviceName;
    private final String baseUrl;
    private final String issuer;
    private final long expiration;

    public ElinksReferenceDataClient(int port, String issuer, Long tokenExpirationInterval, String serviceName) {
        this.baseUrl = "http://localhost:" + port + APP_BASE_PATH;
        this.issuer = issuer;
        this.expiration = tokenExpirationInterval;
        this.serviceName = serviceName;
    }

    public Map<String, Object>  getPeoples() {

        var stringBuilder = new StringBuilder();

        ResponseEntity<ElinkPeopleWrapperResponse> responseEntity;
        HttpEntity<?> request =
                new HttpEntity<Object>(getMultipleAuthHeaders("jrd-system-user", null));

        try {

            responseEntity = restTemplate.exchange(
                    baseUrl + "/people",HttpMethod.GET,request, ElinkPeopleWrapperResponse.class);

        } catch (RestClientResponseException ex) {
            var statusAndBody = new HashMap<String, Object>(2);
            statusAndBody.put("http_status", String.valueOf(ex.getRawStatusCode()));
            statusAndBody.put("response_body", ex.getResponseBodyAsString());
            return statusAndBody;
        }

        return getResponse(responseEntity);
    }

    private Map<String, Object> getResponse(ResponseEntity<ElinkPeopleWrapperResponse> responseEntity) {

        var response = new HashMap();

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

    private String getBearerToken(String userId, String role) {
        return generateToken(issuer, expiration, userId, role);
    }

    public static void setBearerToken(String bearerToken) {
        ElinksReferenceDataClient.bearerToken = bearerToken;
    }

    private void additionalHeaders(Integer pageSize, Integer pageNumber, String sortDirection,
                                   String sortColumn, HttpHeaders headers) {
        headers.add("page_size", String.valueOf(pageSize));
        headers.add("page_number", String.valueOf(pageNumber));
        headers.add("sort_direction", sortDirection);
        headers.add("sort_column", sortColumn);
    }

}
