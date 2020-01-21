package uk.gov.hmcts.reform.judicialapi.client;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.judicialapi.idam.IdamOpenIdClient;

@Slf4j
public class JudicialApiClient {

    private static final String SERVICE_HEADER = "ServiceAuthorization";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final String judicialApiUrl;
    private final String s2sToken;

    protected IdamOpenIdClient idamOpenIdClient;

    public JudicialApiClient(String judicialApiUrl,
                             String s2sToken,
                             IdamOpenIdClient idamOpenIdClient) {
        this.judicialApiUrl = judicialApiUrl;
        this.s2sToken = s2sToken;
        this.idamOpenIdClient = idamOpenIdClient;
    }
    /*public JudicialApiClient(String judicialApiUrl) {
        this.judicialApiUrl = judicialApiUrl;
    }*/

    public String getWelcomePage() {
        return withUnauthenticatedRequest()
                .get("/")
                .then()
                .statusCode(OK.value())
                .and()
                .extract()
                .body()
                .asString();
    }

    public String getHealthPage() {
        return withUnauthenticatedRequest()
                .get("/health")
                .then()
                .statusCode(OK.value())
                .and()
                .extract()
                .body()
                .asString();
    }

    private RequestSpecification withUnauthenticatedRequest() {
        return SerenityRest.given()
                .relaxedHTTPSValidation()
                .baseUri(judicialApiUrl)
                .header("Content-Type", APPLICATION_JSON_UTF8_VALUE)
                .header("Accepts", APPLICATION_JSON_UTF8_VALUE);
    }

    private RequestSpecification getS2sTokenHeaders() {
        return withUnauthenticatedRequest()
                .header(SERVICE_HEADER, "Bearer " + s2sToken);
    }

    private RequestSpecification getMultipleAuthHeadersInternal() {
        return getMultipleAuthHeaders(idamOpenIdClient.getInternalOpenIdToken());
    }

    public RequestSpecification getMultipleAuthHeaders(String userToken) {

        return SerenityRest.with()
                .relaxedHTTPSValidation()
                .baseUri(judicialApiUrl)
                .header("Content-Type", APPLICATION_JSON_UTF8_VALUE)
                .header("Accepts", APPLICATION_JSON_UTF8_VALUE)
                .header(SERVICE_HEADER, "Bearer " + s2sToken)
                .header(AUTHORIZATION_HEADER, "Bearer " + userToken);
    }

    public Map<String, Object> retrieveAllJudicialRoles(String roleOfAccessor, HttpStatus expectedStatus) {
        Response response = getS2sTokenHeaders()
                .get("/refdata/v1/judicial/roles")
                .andReturn();

        response.then()
                .assertThat()
                .statusCode(expectedStatus.value());

        return response.body().as(Map.class);
    }
}
