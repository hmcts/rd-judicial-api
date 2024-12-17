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

@ContextConfiguration(classes = {TestConfigProperties.class, Oauth2.class})
@ComponentScan("uk.gov.hmcts.reform.judicialapi")
@TestPropertySource("classpath:application-functional.yaml")
@Slf4j
public class AuthorizationFunctionalTest {

    @Value("${targetInstance}")
    protected String jrdApiUrl;

    protected static JudicialApiClient judicialApiClient;

    protected static IdamOpenIdClient idamOpenIdClient;

    protected static String s2sToken;

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

}
