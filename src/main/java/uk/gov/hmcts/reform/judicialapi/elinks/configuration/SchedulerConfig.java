package uk.gov.hmcts.reform.judicialapi.elinks.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "elinks.scheduler.enabled", matchIfMissing = true)
public class SchedulerConfig {
}
