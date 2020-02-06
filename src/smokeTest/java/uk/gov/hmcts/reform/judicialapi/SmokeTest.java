package uk.gov.hmcts.reform.judicialapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringIntegrationSerenityRunner.class)
public class SmokeTest {

    private final String targetInstance =
        StringUtils.defaultIfBlank(
            System.getenv("TEST_URL"),
            "http://localhost:8093"
        );

    @Test
    public void should_prove_app_is_running_and_healthy() {

        RestAssured.baseURI = targetInstance;

        Response response = RestAssured
                .given()
                .relaxedHTTPSValidation()
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .get("/")
                .andReturn();
        if (null != response && response.statusCode() == 200) {
            assertThat(response.body().asString())
                    .contains("Welcome to the Judicial API");

        } else {

            Assert.fail();
        }
    }
}
