package uk.gov.hmcts.reform.judicialapi.configuration;

import com.launchdarkly.sdk.server.LDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.hmcts.reform.judicialapi.util.FeatureConditionEvaluation;

@Configuration
public class LaunchDarklyConfiguration implements WebMvcConfigurer {

    @Bean
    public LDClient ldClient(@Value("${launchdarkly.sdk.key}") String sdkKey) {
        return new LDClient(sdkKey);
    }

    @Autowired
    private FeatureConditionEvaluation featureConditionEvaluation;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(featureConditionEvaluation)
            .addPathPatterns("/refdata/judicial/users/fetch")
            .addPathPatterns("/refdata/judicial/users/search")
            .addPathPatterns("/refdata/judicial/users/testing-support/sidam/actions/create-users")
            .addPathPatterns("/refdata/judicial/users")
            .addPathPatterns("/refdata/internal/elink/reference_data/location")
            .addPathPatterns("/refdata/internal/elink/people")
            .addPathPatterns("/refdata/internal/elink/idam/elastic/search")
            .addPathPatterns("/refdata/internal/elink/idam/find")
            .addPathPatterns("/refdata/internal/elink/leavers")
            .addPathPatterns("/refdata/internal/elink/deleted")
            .addPathPatterns("/refdata/internal/elink/sidam/asb/publish")
            .addPathPatterns("/refdata/internal/elink");
    }
}
