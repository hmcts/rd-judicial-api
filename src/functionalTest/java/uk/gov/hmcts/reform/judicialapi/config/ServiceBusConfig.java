package uk.gov.hmcts.reform.judicialapi.config;


import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServiceBusConfig {

    @Value("${jrd.publisher.azure.service.bus.topic}")
    String topic;

    @Value("${jrd.publisher.azure.service.bus.host}")
    String host;

    @Value("${jrd.publisher.azure.service.bus.username}")
    String sharedAccessKeyName;

    @Value("${jrd.publisher.azure.service.bus.password}")
    String sharedAccessKeyValue;


    private ServiceBusReceiverClient receiverClient;
    private final String subscriptionName = "rd-judicial-subscription-aat";

    @Bean
    public ServiceBusReceiverClient getServiceBusRecieverClient() {
        String connectionString = "Endpoint=sb://"
            + host + ";SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=" + sharedAccessKeyValue;
        return receiverClient = new ServiceBusClientBuilder()
            .connectionString(connectionString)
           .receiver()
           .topicName(host)
           .subscriptionName(subscriptionName)
           .buildClient();
    }
}
