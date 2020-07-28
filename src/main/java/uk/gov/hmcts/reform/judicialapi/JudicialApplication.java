package uk.gov.hmcts.reform.judicialapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "uk.gov.hmcts.reform.judicialapi")
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class JudicialApplication {

    public static void main(final String[] args) {
        SpringApplication.run(JudicialApplication.class, args);
    }
}
