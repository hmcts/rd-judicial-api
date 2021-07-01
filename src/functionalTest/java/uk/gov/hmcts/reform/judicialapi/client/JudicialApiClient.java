package uk.gov.hmcts.reform.judicialapi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.judicialapi.idam.IdamOpenIdClient;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class JudicialApiClient {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SERVICE_HEADER = "ServiceAuthorization";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String USER_EMAIL_HEADER = "UserEmail";
    private static final String RANDOM_EMAIL = "RANDOM_EMAIL";

    private final String judicialApiUrl;
    private final String s2sToken;
    private final IdamOpenIdClient idamOpenIdClient;

    public JudicialApiClient(String judicialApiUrl,
                             String s2sToken,
                             IdamOpenIdClient idamOpenIdClient) {
        this.judicialApiUrl = judicialApiUrl;
        this.s2sToken = s2sToken;
        this.idamOpenIdClient = idamOpenIdClient;
    }

    public String getWelcomePage() {
        return withUnauthenticatedRequest()
                .get("/")
                .then()
                .statusCode(OK.value())
                .and()
                .extract()
                .response()
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
                .response()
                .body()
                .asString();
    }

    private RequestSpecification withUnauthenticatedRequest() {
        return SerenityRest.given()
                .relaxedHTTPSValidation()
                .baseUri(judicialApiUrl)
                .header("Content-Type", APPLICATION_JSON_VALUE)
                .header("Accepts", APPLICATION_JSON_VALUE);
    }

    public RequestSpecification getMultipleAuthHeadersInternal(String role) {
        return getMultipleAuthHeaders(idamOpenIdClient.getOpenIdTokenByRole(role));
    }

    public RequestSpecification getMultiPartWithAuthHeaders(String role) {
        String userToken = idamOpenIdClient.getOpenIdTokenByRole(role);
        return SerenityRest.with()
                .relaxedHTTPSValidation()
                .baseUri(judicialApiUrl)
                .header(SERVICE_HEADER, "Bearer " + s2sToken)
                .header("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(AUTHORIZATION_HEADER, "Bearer " + userToken);
    }

    public RequestSpecification getMultipleAuthHeaders(String userToken) {
        return SerenityRest.with()
                .relaxedHTTPSValidation()
                .baseUri(judicialApiUrl)
                .header("Content-Type", APPLICATION_JSON_VALUE)
                .header("Accepts", APPLICATION_JSON_VALUE)
                .header(SERVICE_HEADER, "Bearer " + s2sToken)
                .header(AUTHORIZATION_HEADER, "Bearer " + userToken);
    }


}
