package uk.gov.hmcts.reform.judicialapi.util;

import com.launchdarkly.sdk.server.LDClient;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.JudicialReferenceDataClient;
import uk.gov.hmcts.reform.judicialapi.configuration.RestTemplateConfiguration;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Configuration
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Integration")})
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990", "IDAM_URL:http://127.0.0.1:5000"})
@ContextConfiguration(classes = {RestTemplateConfiguration.class})
public abstract class AuthorizationEnabledIntegrationTest extends SpringBootIntegrationTest {

    @MockBean
    protected FeatureToggleServiceImpl featureToggleServiceImpl;

    @MockBean
    LDClient ldClient;

    protected JudicialReferenceDataClient judicialReferenceDataClient;

    @Value("${oidc.expiration}")
    private long expiration;
    @Value("${oidc.issuer}")
    private String issuer;


    @Autowired
    Flyway flyway;

    @Before
    public void setUpClient() {
        judicialReferenceDataClient = new JudicialReferenceDataClient(port, issuer, expiration);
        when(featureToggleServiceImpl.isFlagEnabled(anyString(), anyString())).thenReturn(true);
        flyway.clean();
        flyway.migrate();
    }

    @After
    public void cleanupTestData() {
    }

}

