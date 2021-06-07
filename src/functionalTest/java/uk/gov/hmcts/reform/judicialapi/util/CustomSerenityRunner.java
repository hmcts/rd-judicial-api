package uk.gov.hmcts.reform.judicialapi.util;

import com.launchdarkly.sdk.server.LDClient;

import uk.gov.hmcts.reform.judicialapi.config.Oauth2;
import uk.gov.hmcts.reform.judicialapi.config.TestConfigProperties;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.judicialapi.service.impl.FeatureToggleServiceImpl;

import static java.lang.System.getenv;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.BooleanUtils.isNotTrue;

@ContextConfiguration(classes = {TestConfigProperties.class, Oauth2.class})
@ComponentScan("uk.gov.hmcts.reform.judicialapi")
@TestPropertySource("classpath:application-functional.yaml")
public class CustomSerenityRunner extends SpringIntegrationSerenityRunner {

    private static LDClient ldClient;

    private static FeatureToggleServiceImpl featureToggleService;

    private static boolean isInitialized = false;

    private static String flagName;

    public CustomSerenityRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }


    @Override
    protected boolean isIgnored(FrameworkMethod child) {

        if (isNotTrue(isInitialized)) {
            initialize();
        }

        ToggleEnable toggleEnable = child.getAnnotation(ToggleEnable.class);
        if (nonNull(toggleEnable)) {
            featureToggleService.mapServiceToFlag();
            flagName = featureToggleService.getLaunchDarklyMap()
                .get(toggleEnable.mapKey());

            boolean isEnabledLD = featureToggleService.isFlagEnabled("rd_judicial_api", flagName);

            if (isEnabledLD) {
                if (isNotTrue(toggleEnable.withFeature())) {
                    return true;
                }
            } else {
                if (toggleEnable.withFeature()) {
                    return true;
                }
            }
        }
        return super.isIgnored(child);
    }

    private void initialize() {
        ldClient = new LDClient(getenv("LD_SDK_KEY"));
        featureToggleService = new FeatureToggleServiceImpl(ldClient, "rd");
        String executionEnvironment = getenv("execution_environment");
        ReflectionTestUtils.setField(featureToggleService, "environment", executionEnvironment);

        isInitialized = true;
    }

    public static String getFeatureFlagName() {
        return  flagName;
    }
}
