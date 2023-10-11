package uk.gov.hmcts.reform.judicialapi;

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
import uk.gov.hmcts.reform.lib.client.response.S2sClient;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;

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
            s2sToken = new S2sClient(
                    testConfigProperties.getS2sUrl(),
                    testConfigProperties.getS2sName(),
                    testConfigProperties.getS2sSecret())
                    .signIntoS2S();
        }

        if (null == idamOpenIdClient) {
            idamOpenIdClient = new IdamOpenIdClient(testConfigProperties);
        }

        judicialApiClient = new JudicialApiClient(jrdApiUrl, s2sToken, idamOpenIdClient);
    }

    public static String generateRandomEmail() {
        return String.format(EMAIL_TEMPLATE, randomAlphanumeric(10)).toLowerCase();
    }
}
