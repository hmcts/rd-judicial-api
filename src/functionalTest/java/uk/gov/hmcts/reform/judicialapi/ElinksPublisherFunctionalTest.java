package uk.gov.hmcts.reform.judicialapi;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;


@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ElinksPublisherFunctionalTest  {


    @Autowired
    private ElinkTopicPublisher publisher;

    @Autowired
    private ServiceBusSenderClient serviceBusSenderClient;

    @Autowired
    private ElinkDataIngestionSchedularAudit audit;

    private ServiceBusReceiverClient receiverClient;


    private final String topicName = "rd-servicebus-aat";
    private final String subscriptionName = "rd-judicial-subscription-aat"; // This must exist
    private final String connectionString = "Endpoint=sb://rd-servicebus-aat.servicebus.windows.net/"
        + ";SharedAccessKeyName=SendAndListenSharedAccessKey;SharedAccessKey"
        + "=7I9wPIX+dLgsMHWW2ipEAAhBdujtCnko3F8dYQFkibQ=;EntityPath=rd-servicebus-aat";

    @BeforeAll
    void setUpReceiver() {
        receiverClient = new ServiceBusClientBuilder()
            .connectionString(connectionString)
            .receiver()
            .topicName(topicName)
            .subscriptionName(subscriptionName)
            .buildClient();
    }

    @AfterAll
    void cleanUp() {
        receiverClient.close();
    }

    @Test
    void testSendMessageToTopic() throws InterruptedException {
        // Given
        String jobId = UUID.randomUUID().toString();
        List<String> userIds = List.of("integration-user-1", "integration-user-2",
            "integration-user-3","integration-user-4","integration-user-5","integration-user-6","integration-user-7",
            "integration-user-8");

        // When
        publisher.sendMessage(userIds, jobId);

        // Then
        TimeUnit.SECONDS.sleep(5); // Wait for propagation

        ServiceBusReceivedMessage message = receiverClient.receiveMessages(1).stream().findFirst().orElse(null);

        assertNotNull(message, "Message should be received from topic subscription");
        String body = message.getBody().toString();
        assertTrue(body.contains("integration-user-1"), "Message body should contain sent userId");

        receiverClient.complete(message);
    }
}


