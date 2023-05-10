package uk.gov.hmcts.reform.judicialapi.elinks.configuration;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import uk.gov.hmcts.reform.judicialapi.configuration.FeignHeaderConfig;

import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.AUTHORIZATION;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.CONTENT_TYPE;

@Slf4j
public class ElinksFeignInterceptorConfiguration {

    @Value("${elinksApiKey}")
    private String elinksApiKey;


    @Bean
    public RequestInterceptor requestInterceptor(FeignHeaderConfig config) {

        return requestTemplate -> {
            requestTemplate.header(AUTHORIZATION, "Token " + elinksApiKey);
            requestTemplate.header(CONTENT_TYPE, "application/json");
        };
    }
}