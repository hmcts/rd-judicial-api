package uk.gov.hmcts.reform.judicialapi;

import com.google.common.collect.ImmutableMap;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.client.JudicialApiClient;
import uk.gov.hmcts.reform.judicialapi.config.Oauth2;
import uk.gov.hmcts.reform.judicialapi.config.TestConfigProperties;
import uk.gov.hmcts.reform.judicialapi.idam.IdamOpenIdClient;

import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ContextConfiguration(classes = {TestConfigProperties.class, Oauth2.class})
@ComponentScan("uk.gov.hmcts.reform.judicialapi")
@TestPropertySource("classpath:application-functional.yaml")
@Slf4j
public class AuthorizationFunctionalTest {

    @Value("${s2s-url}")
    protected String s2sUrl;

    @Value("${s2s-name}")
    protected String s2sName;

    @Value("${s2s-secret}")
    protected String s2sSecret;

    @Value("${targetInstance}")
    protected String jrdApiUrl;

    protected static JudicialApiClient judicialApiClient;

    protected static IdamOpenIdClient idamOpenIdClient;

    @Autowired
    protected TestConfigProperties configProperties;

    private final GoogleAuthenticator authenticator = new GoogleAuthenticator();

    protected static String s2sToken;

    public static final String EMAIL = "EMAIL";
    public static final String CREDS = "CREDS";
    public static final String EMAIL_TEMPLATE = "test-user-%s@jrdfunctestuser.com";
    public static final String ROLE_JRD_ADMIN = "jrd-admin";
    public static final String ROLE_JRD_SYSTEM_USER = "jrd-system-user";

    @Autowired
    protected TestConfigProperties testConfigProperties;

    @PostConstruct
    public void beforeTestClass() {
        SerenityRest.useRelaxedHTTPSValidation();

        if (null == s2sToken) {
            log.info(":::: Generating S2S Token");
            s2sToken = signIntoS2S();
        }

        if (null == idamOpenIdClient) {
            idamOpenIdClient = new IdamOpenIdClient(testConfigProperties);
        }

        judicialApiClient = new JudicialApiClient(jrdApiUrl, s2sToken, idamOpenIdClient);
    }

    public static String generateRandomEmail() {
        return String.format(EMAIL_TEMPLATE, secure().nextAlphanumeric(10)).toLowerCase();
    }

    public String signIntoS2S() {
        Map<String, Object> params = ImmutableMap.of("microservice",
                testConfigProperties.getS2sName(),
                "oneTimePassword",
                authenticator.getTotpPassword(testConfigProperties.getS2sSecret()));

        Response response = RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(testConfigProperties.getS2sUrl())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(params)
                .post("/lease")
                .andReturn();

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().asString();
    }
}
