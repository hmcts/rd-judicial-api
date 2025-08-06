package uk.gov.hmcts.reform.judicialapi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.judicialapi.elinks.servicebus.ElinkTopicPublisher;


@SpringBootTest
//@Disabled("Run when needed")
@WithTags({@WithTag("testType:Functional")})
//@TestPropertySource(properties = "spring.flyway.enabled=false")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ElinksPublisherFunctionalTest {


    @Autowired
    private ElinkTopicPublisher publisher;

    // @Autowired
    //private ServiceBusSenderClient serviceBusSenderClient;


    //@Autowired
    //MessagingConfig messagingConfig;


    @Test
    void testSendMessageToTopic() throws InterruptedException {

        //ServiceBusReceiverClient receiverClient = messagingConfig.getServiceBusRecieverClient();

        // Given a list of ids from judicial that will be sent to the topic
        String jobId = UUID.randomUUID().toString();

        List<String> userIds = new ArrayList<>(4000);

        for (int i = 1; i <= 4000; i++) {
            userIds.add("integration-user-" + i);
        }

        // Example: print the first 10 user IDs
        userIds.stream().limit(10).forEach(System.out::println);

        // When
        publisher.sendMessage(userIds, jobId);

        // Then
        TimeUnit.SECONDS.sleep(5);

        // Wait for propagation
        /*ServiceBusReceivedMessage message = receiverClient.receiveMessages(1).stream().findFirst()
            .orElse(null);

        assertNotNull(message, "Message should be received from topic subscription");
        String body = message.getBody().toString();
        assertTrue(body.contains("integration-user-1"), "Message body should contain sent userId");

        receiverClient.complete(message);*/
    }
}
