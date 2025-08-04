package uk.gov.hmcts.reform.judicialapi.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.lib.config.TestConfig;

@Getter
@Setter
@Configuration
public class TestConfigProperties implements TestConfig {


    @Value("${oauth2.client.secret}")
    public String clientSecret;


    @Value("https://idam-api.aat.platform.hmcts.net")
    public String idamApiUrl;

    @Value("https://rd-judicial-api-aat.service.core-compute-aat.internal/oauth2redirect")
    public String oauthRedirectUrl;

    @Value("rd-professional-api")
    public String clientId;

    @Value("http://localhost:8093")
    protected String targetInstance;

    @Value("http://rpe-service-auth-provider-aat.service.core-compute-aat.internal")
    protected String s2sUrl;

    @Value("rd_judicial_api")
    protected String s2sName;

    @Value("${s2s-secret}")
    protected String s2sSecret;

    @Value("${scope-name}")
    protected String scope;

    @Bean
    public ObjectMapper defaultObjectMapper() {
        return new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new ParameterNamesModule(JsonCreator.Mode.DEFAULT))
                .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }



}
