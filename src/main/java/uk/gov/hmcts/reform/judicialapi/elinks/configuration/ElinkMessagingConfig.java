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

    @Value("${jrd.publisher.azure.service.bus.topic}")
    String topic;

    @Value("${jrd.publisher.azure.service.bus.host}")
    String host;

    @Value("${jrd.publisher.azure.service.bus.username}")
    String sharedAccessKeyName;

    @Value("${jrd.publisher.azure.service.bus.password}")
    String sharedAccessKeyValue;



    @Bean
    public ServiceBusSenderClient getServiceBusSenderClient() {


        String connectionString = "Endpoint=sb://"
            + host + ";SharedAccessKeyName=" + sharedAccessKeyName + ";SharedAccessKey=" + sharedAccessKeyValue;

        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .retryOptions(new AmqpRetryOptions())
                .sender()
                .topicName(topic)
                .buildClient();
    }
}
