package uk.gov.hmcts.reform.judicialapi.elinks.servicebus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.partition;
import static uk.gov.hmcts.reform.judicialapi.elinks.util.RefDataElinksConstants.UNAUTHORIZED_ERROR;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusMessageBatch;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.ServiceBusTransactionContext;
import com.google.gson.Gson;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.judicialapi.elinks.configuration.PublishingData;
import uk.gov.hmcts.reform.judicialapi.elinks.exception.ElinksException;
import uk.gov.hmcts.reform.judicialapi.elinks.util.ElinkDataIngestionSchedularAudit;

@Slf4j
@Component
public class ElinkTopicPublisher {

    @Value("${logging-component-name}")
    String loggingComponentName;
    @Value("${jrd.publisher.jrd-message-batch-size}")
    int jrdMessageBatchSize;
    @Value("${jrd.publisher.azure.service.bus.topic}")
    String topic;

    @Value("${jrd.publisher.jrd-message-transaction-size}")
    int maxMessagesPerTransaction;

    @Autowired
    ElinkDataIngestionSchedularAudit elinkDataIngestionSchedularAudit;

    @Autowired
    private ServiceBusSenderClient elinkserviceBusSenderClient;

    public void sendMessage(@NotNull List<String> judicalIds, String jobId) {
        ServiceBusTransactionContext elinktransactionContext = null;
        ServiceBusMessageBatch elinkmessageBatch = null;
        List<ServiceBusMessage> currentBatch = new ArrayList<>();
        List<ServiceBusMessage> serviceBusMessages = new ArrayList<>();
        try {
            // Split the incoming judicial IDs into smaller chunks based on the configured batch size
            partition(judicalIds, jrdMessageBatchSize)
                .forEach(data -> {
                    PublishingData judicialDataChunk = new PublishingData();
                    judicialDataChunk.setUserIds(data);
                    serviceBusMessages.add(new ServiceBusMessage(new Gson().toJson(judicialDataChunk)));
                });

            // Iterate through the prepared Service Bus messages
            for (ServiceBusMessage messageRecord : serviceBusMessages) {
                elinkmessageBatch = elinkserviceBusSenderClient.createMessageBatch();
                // Add the message to the current batch
                // Check if the message can be added to the batch based on the maxbatchsize preconfigured
                // or if the batch size exceeds the transaction limit(MAX_MESSAGES_PER_TRANSACTION= 100)
                currentBatch.add(messageRecord);
                if (elinkmessageBatch.tryAddMessage(messageRecord) || currentBatch.size()
                    < maxMessagesPerTransaction) {
                    continue;// Continue adding messages to the batch
                }
                // Create a new transaction for the current batch
                elinktransactionContext = elinkserviceBusSenderClient.createTransaction();
                // Send the current batch of messages to the Service Bus and commit the transaction
                sendMessageToAsb(elinkserviceBusSenderClient, elinktransactionContext, elinkmessageBatch, jobId);
                elinkserviceBusSenderClient.commitTransaction(elinktransactionContext);
                // Clear the current batch for the next set of messages
                currentBatch.clear();
            }
        } catch (Exception exception) {
            log.error("{}:: Publishing message to service bus topic failed with exception: {}:: Job Id {}",
                loggingComponentName, exception.getMessage(), jobId);
            if (Objects.nonNull(elinktransactionContext) || Objects.nonNull(elinkserviceBusSenderClient)) {
                elinkserviceBusSenderClient.rollbackTransaction(elinktransactionContext);
            }
            // Throw exception to indicate the failure
            throw new ElinksException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_ERROR, UNAUTHORIZED_ERROR);
        }
        // Commit the transaction for any remaining messages
        elinkserviceBusSenderClient.commitTransaction(elinktransactionContext);
    }

    private void sendMessageToAsb(ServiceBusSenderClient serviceBusSenderClient,
                                  ServiceBusTransactionContext transactionContext,
                                  ServiceBusMessageBatch messageBatch,
                                  String jobId) {
        if (messageBatch.getCount() > 0) {
            serviceBusSenderClient.sendMessages(messageBatch, transactionContext);
            log.info("{}:: Sent a batch of messages to the topic: {} ::Job id::{}", loggingComponentName, topic, jobId);
        }
    }
}
