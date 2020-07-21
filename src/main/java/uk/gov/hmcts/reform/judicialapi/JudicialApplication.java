package uk.gov.hmcts.reform.judicialapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.hmcts.reform.health.HealthAutoConfiguration;


@SpringBootApplication(scanBasePackages = "uk.gov.hmcts.reform.judicialapi", exclude = HealthAutoConfiguration.class)
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class JudicialApplication {

    public static void main(final String[] args) {
        SpringApplication.run(JudicialApplication.class, args);
    }
}
