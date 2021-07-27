package uk.gov.hmcts.reform.judicialapi;


import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import uk.gov.hmcts.reform.judicialapi.client.JudicialApiClient;
import uk.gov.hmcts.reform.judicialapi.client.S2sClient;
import uk.gov.hmcts.reform.judicialapi.config.Oauth2;
import uk.gov.hmcts.reform.judicialapi.config.TestConfigProperties;
import uk.gov.hmcts.reform.judicialapi.idam.IdamOpenIdClient;


import static java.util.Objects.isNull;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;

@ContextConfiguration(classes = {TestConfigProperties.class, Oauth2.class})
@ComponentScan("uk.gov.hmcts.reform.judicialapi")
@TestPropertySource("classpath:application-functional.yaml")
@Slf4j
@TestExecutionListeners(listeners = {
        AuthorizationFunctionalTest.class,
        DependencyInjectionTestExecutionListener.class})
public class AuthorizationFunctionalTest extends AbstractTestExecutionListener {

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


    protected static String s2sToken;

    public static final String EMAIL = "EMAIL";
    public static final String CREDS = "CREDS";
    public static final String EMAIL_TEMPLATE = "test-user-%s@jrdfunctestuser.com";
    public static final String ROLE_JRD_ADMIN = "jrd-admin";
    public static final String ROLE_JRD_SYSTEM_USER = "jrd-system-user";


    @Override
    public void beforeTestClass(TestContext testContext) {
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);

        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.defaultParser = Parser.JSON;

        log.info("Configured S2S secret: " + s2sSecret.substring(0, 2) + "************" + s2sSecret.substring(14));
        log.info("Configured S2S microservice: " + s2sName);
        log.info("Configured S2S URL: " + s2sUrl);

        idamOpenIdClient = new IdamOpenIdClient(configProperties);

        //Single S2S & Sidam call
        s2sToken = isNull(s2sToken) ? new S2sClient(s2sUrl, s2sName, s2sSecret).signIntoS2S() : s2sToken;

        judicialApiClient = new JudicialApiClient(jrdApiUrl, s2sToken, idamOpenIdClient);
    }

    public static String generateRandomEmail() {
        return String.format(EMAIL_TEMPLATE, randomAlphanumeric(10)).toLowerCase();
    }

    public static String getS2sToken() {
        return s2sToken;
    }


}
