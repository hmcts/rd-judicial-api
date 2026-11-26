package uk.gov.hmcts.reform.judicialapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;


@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = false)
public class MethodSecurityConfiguration {

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
