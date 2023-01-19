package uk.gov.hmcts.reform.judicialapi.configuration;

import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public ServiceBusSenderClient getServiceBusSenderClient() {

        log.error("Maddineni Prudhvi logs");
        log.error(topic);
        log.error(sharedAccessKeyName);
        log.error(sharedAccessKeyValue);
        log.error(host);
        String host1 = host;
        String connectionString = "Endpoint=sb://"
                + host1 + ";SharedAccessKeyName=" + sharedAccessKeyName + ";SharedAccessKey=" + sharedAccessKeyValue;
        log.error(connectionString,host1);
        log.error("Maddineni Prudhvi End");
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .retryOptions(new AmqpRetryOptions())
                .sender()
                .topicName(topic)
                .buildClient();
    }


}
