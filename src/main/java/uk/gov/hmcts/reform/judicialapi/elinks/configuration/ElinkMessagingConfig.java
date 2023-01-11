package uk.gov.hmcts.reform.judicialapi.elinks.configuration;


import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ElinkMessagingConfig {

    @Value("${elink.publisher.azure.service.bus.topic}")
    String topic;

    @Value("${elink.publisher.azure.service.bus.connection-string}")
    String accessConnectionString;

    @Bean
    public ServiceBusSenderClient getServiceBusSenderClient() {

        return new ServiceBusClientBuilder()
                .connectionString(accessConnectionString)
                .retryOptions(new AmqpRetryOptions())
                .sender()
                .topicName(topic)
                .buildClient();
    }
}