package uk.gov.hmcts.reform.judicialapi.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Configuration
public class MessagingConfig {

    @Value("${jrd.publisher.azure.service.bus.host}")
    String host;

    @Value("${jrd.publisher.azure.service.bus.topic}")
    String topic;

    @Value("${jrd.publisher.azure.service.bus.username}")
    String sharedAccessKeyName;

    @Value("${jrd.publisher.azure.service.bus.password}")
    String sharedAccessKeyValue;




}
