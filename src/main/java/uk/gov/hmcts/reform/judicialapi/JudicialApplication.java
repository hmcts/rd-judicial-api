package uk.gov.hmcts.reform.judicialapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.idam.client.IdamApi;

@SpringBootApplication(scanBasePackages = "uk.gov.hmcts.reform.judicialapi")
@EnableFeignClients(basePackages = { "uk.gov.hmcts.reform.judicialapi" },
        basePackageClasses = { IdamApi.class, ServiceAuthorisationApi.class }
)
@EnableCaching
@EnableScheduling
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class JudicialApplication {

    public static void main(final String[] args) {
        SpringApplication.run(JudicialApplication.class, args);
    }
}
