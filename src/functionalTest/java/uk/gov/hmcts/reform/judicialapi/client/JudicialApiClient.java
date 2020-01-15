package uk.gov.hmcts.reform.judicialapi.client;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.http.HttpStatus;

@Slf4j
public class JudicialApiClient {

    private final String judicialApiUrl;

    public JudicialApiClient(String judicialApiUrl) {
        this.judicialApiUrl = judicialApiUrl;
    }

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

    public Map<String, Object> searchForUserByEmailAddress(String email, String role) {
        Response response = withUnauthenticatedRequest()
                .param("email", email)
                .get("/refdata//v1/judicial/user")
                .andReturn();
        log.info("Search For User By Email Response: " + response.asString());
        response.then()
                .assertThat()
                .statusCode(OK.value());

        return response.body().as(Map.class);
    }

    public Map<String, Object> retrieveAllJudicialRoles(String roleOfAccessor, HttpStatus expectedStatus) {
        Response response = withUnauthenticatedRequest()
                .get("/refdata/v1/judicial/roles")
                .andReturn();

        response.then()
                .assertThat()
                .statusCode(expectedStatus.value());

        return response.body().as(Map.class);
    }
}
