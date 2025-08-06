package uk.gov.hmcts.reform.judicialapi;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
//@Disabled("Run when needed")
@WithTags({@WithTag("testType:Functional")})
@TestPropertySource(properties = "spring.flyway.enabled=false")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ElinksPublisherFunctionalTest {


    //@Autowired
    //private ElinkTopicPublisher publisher;

    // @Autowired
    //private ServiceBusSenderClient serviceBusSenderClient;

    //@Autowired
    //private ElinkDataIngestionSchedularAudit audit;

    //@Autowired
    //MessagingConfig messagingConfig;


    @Test
    void testSendMessageToTopic() throws InterruptedException {

        //ServiceBusReceiverClient receiverClient = messagingConfig.getServiceBusRecieverClient();

        // Given a list of ids from judicial that will be sent to the topic
      //  String jobId = UUID.randomUUID().toString();
        /*List<String> userIds = List.of("integration-user-1", "integration-user-2",
            "integration-user-3","integration-user-4","integration-user-5","integration-user-6","integration-user-7",
            "integration-user-8","integration-user-9","integration-user-10","integration-user-11",
            "integration-user-12","integration-user-13","integration-user-14","integration-user-15",
            "integration-user-16","integration-user-17","integration-user-18","integration-user-19",
            "integration-user-20");*/

      /*  List<String> userIds = new ArrayList<>(4000);

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
