package uk.gov.hmcts.reform.judicialapi.idam;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.judicialapi.config.TestConfigProperties;
import uk.gov.hmcts.reform.lib.idam.IdamOpenId;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Slf4j
public class IdamOpenIdClient extends IdamOpenId {

    private final TestConfigProperties testConfig;

    public static String jrdAdminToken;

    public static String jrdSystemUserToken;

    public IdamOpenIdClient(TestConfigProperties testConfig) {
        super(testConfig);
        this.testConfig = testConfig;
    }

    public void deleteSidamUser(String email) {
        try {
            RestAssured
                    .given()
                    .relaxedHTTPSValidation()
                    .baseUri(testConfig.getIdamApiUrl())
                    .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                    .delete("/testing-support/accounts/" + email);
        } catch (Exception ex) {
            log.error("unable to delete sidam user with email");
        }
    }
}
