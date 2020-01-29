package uk.gov.hmcts.reform.judicialapi.configuration;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import uk.gov.hmcts.reform.auth.checker.core.RequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.spring.serviceonly.AuthCheckerServiceOnlyFilter;

@EnableWebSecurity
@Slf4j
public class SecurityConfiguration  {

    //Replace this static class with the one immediately below once IDAM set up
    @ConfigurationProperties(prefix = "security")
    @Configuration
    public static class RestAllApiSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        List<String> anonymousPaths;

        private AuthCheckerServiceOnlyFilter authCheckerServiceOnlyFilter;

        public List<String> getAnonymousPaths() {
            return anonymousPaths;
        }

        public void setAnonymousPaths(List<String> anonymousPaths) {
            this.anonymousPaths = anonymousPaths;
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring()
                    .antMatchers(anonymousPaths.toArray(new String[0]));
        }


        public RestAllApiSecurityConfigurationAdapter(RequestAuthorizer<Service> serviceRequestAuthorizer,

                                                      AuthenticationManager authenticationManager) {

            authCheckerServiceOnlyFilter = new AuthCheckerServiceOnlyFilter(serviceRequestAuthorizer);

            authCheckerServiceOnlyFilter.setAuthenticationManager(authenticationManager);
        }

        protected void configure(HttpSecurity http) throws Exception {

            http.addFilter(authCheckerServiceOnlyFilter)
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().authenticated();
        }
    }

    /*@ConfigurationProperties(prefix = "security")
    @Configuration
    public static class RestAllApiSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        List<String> anonymousPaths;

        private AuthCheckerServiceAndUserFilter authCheckerServiceAndUserFilter;

        public List<String> getAnonymousPaths() {
            return anonymousPaths;
        }

        public void setAnonymousPaths(List<String> anonymousPaths) {
            this.anonymousPaths = anonymousPaths;
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring()
                    .antMatchers(anonymousPaths.toArray(new String[0]));
        }


        public RestAllApiSecurityConfigurationAdapter(RequestAuthorizer<User> userRequestAuthorizer,

                                                      RequestAuthorizer<Service> serviceRequestAuthorizer,

                                                      AuthenticationManager authenticationManager) {

            authCheckerServiceAndUserFilter = new AuthCheckerServiceAndUserFilter(serviceRequestAuthorizer, userRequestAuthorizer);

            authCheckerServiceAndUserFilter.setAuthenticationManager(authenticationManager);

        }

        @Override
        protected void configure(HttpSecurity http) throws AccessDeniedException,Exception {

            http.authorizeRequests()
                    .antMatchers("/actuator/**","/search/**")
                    .permitAll()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(STATELESS)
                    .and()
                    .csrf()
                    .disable()
                    .formLogin()
                    .disable()
                    .logout()
                    .disable()
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .addFilter(authCheckerServiceAndUserFilter);
        }


    }*/

}

