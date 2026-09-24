package uk.gov.hmcts.reform.judicialapi.util;

import com.launchdarkly.sdk.server.LDClient;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Configuration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SerenityJUnit5Extension.class)
@WithTags({@WithTag("testType:Integration")})
@DirtiesContext
public abstract class AuthorizationEnabledIntegrationTest extends SpringBootIntegrationTest {

    @MockitoBean
    protected FeatureToggleServiceImpl featureToggleServiceImpl;
    protected JudicialReferenceDataClient judicialReferenceDataClient;
    @MockitoBean
    LDClient ldClient;
    @Autowired
    Flyway flyway;
    @Value("${oidc.expiration}")
    private long expiration;
    @Value("${oidc.issuer}")
    private String issuer;
    @Value("${idam.s2s-authorised.services}")
    private String serviceName;

    @Value("${jrd.security.roles.system-user}")
    protected String jrdSystemUser;

    @BeforeEach
    public void setUpClient() {
        judicialReferenceDataClient = new JudicialReferenceDataClient(port, issuer, expiration, serviceName);
        when(featureToggleServiceImpl.isFlagEnabled(anyString())).thenReturn(true);
        flyway.clean();
        flyway.migrate();
    }

}



