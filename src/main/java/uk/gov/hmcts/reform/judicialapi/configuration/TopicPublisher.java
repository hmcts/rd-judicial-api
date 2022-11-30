package uk.gov.hmcts.reform.judicialapi.configuration;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusMessageBatch;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.ServiceBusTransactionContext;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;

@Slf4j
@Service
public class TopicPublisher {


    @Autowired
    private ServiceBusSenderClient serviceBusSenderClient;

    @Value("${loggingComponentName}")
    String loggingComponentName;

    @Value("${jrd.publisher.dataPerMessage}")
    int dataPerMessage;

    @Value("${jrd.publisher.azure.service.bus.topic}")
    String topic;

    public void sendMessage(@NotNull List<String> jrddIds) {
        ServiceBusTransactionContext transactionContext = null;

        try {
            log.info("{}:: Publishing message to service bus topic", loggingComponentName);

            transactionContext = serviceBusSenderClient.createTransaction();
            publishMessageToTopic(jrddIds, serviceBusSenderClient, transactionContext);
        } catch (Exception exception) {
            log.error("{}:: Publishing message to service bus topic failed with exception: {}",
                    loggingComponentName, exception);
            if (Objects.nonNull(serviceBusSenderClient) && Objects.nonNull(transactionContext)) {
                serviceBusSenderClient.rollbackTransaction(transactionContext);
            }
        }
        serviceBusSenderClient.commitTransaction(transactionContext);
        log.info("{}:: Message published to service bus topic", loggingComponentName);
    }

    private void publishMessageToTopic(List<String> jrdIds,
                                       ServiceBusSenderClient serviceBusSenderClient,
                                       ServiceBusTransactionContext transactionContext) {
        log.info("{}:: Started publishing to topic", loggingComponentName);
        ServiceBusMessageBatch messageBatch = serviceBusSenderClient.createMessageBatch();
        List<ServiceBusMessage> serviceBusMessages = new ArrayList<>();

        ListUtils.partition(jrdIds, dataPerMessage)
                .forEach(data -> {
                    PublishingData publishCaseWorkerDataChunk = new PublishingData();
                    publishCaseWorkerDataChunk.setUserIds(data);
                    serviceBusMessages.add(new ServiceBusMessage(new Gson().toJson(publishCaseWorkerDataChunk)));
                });

        for (ServiceBusMessage message : serviceBusMessages) {
            if (messageBatch.tryAddMessage(message)) {
                continue;
            }

            // The batch is full, so we create a new batch and send the batch.
            serviceBusSenderClient.sendMessages(messageBatch, transactionContext);

            // create a new batch
            messageBatch = serviceBusSenderClient.createMessageBatch();

            // Add that message that we couldn't before.
            if (!messageBatch.tryAddMessage(message)) {
                log.error("{}:: Message is too large for an empty batch. Skipping. Max size: {}",
                        loggingComponentName, messageBatch.getMaxSizeInBytes());
            }
        }

        if (messageBatch.getCount() > 0) {
            serviceBusSenderClient.sendMessages(messageBatch, transactionContext);
            log.info("{}:: Sent a batch of messages count: {} to the topic: {} ::Job id", loggingComponentName,
                    messageBatch.getCount(),topic);
        }
    }

}


