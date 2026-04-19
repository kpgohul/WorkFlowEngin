package com.friends.executionservice.kafkaclient;

import com.friends.executionservice.clientdto.actionclientdto.ActionRequest;
import com.friends.executionservice.util.common.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStepExecutionProducer {

    private static final String EXECUTION_ID_HEADER = "executionId";
    private static final String EXECUTION_STEP_ID_HEADER = "executionStepId";

    private final KafkaSender<String, String> kafkaSender;

    @Value("${kafka.execution.topic}")
    private String executionTopic;

    public Mono<Void> publishStepAction(ActionRequest actionRequest, Long executionId, Long executionStepId) {
        String payload = JsonUtils.toJson(actionRequest);
        RecordHeaders headers = new RecordHeaders();
        headers.add(EXECUTION_ID_HEADER, String.valueOf(executionId).getBytes(StandardCharsets.UTF_8));
        headers.add(EXECUTION_STEP_ID_HEADER, String.valueOf(executionStepId).getBytes(StandardCharsets.UTF_8));

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
                executionTopic,
                null,
                String.valueOf(executionId),
                payload,
                headers
        );

        return kafkaSender.send(Mono.just(SenderRecord.create(producerRecord, executionStepId)))
                .doOnNext(result -> log.info(
                        "Published step action. topic={}, executionId={}, executionStepId={}, offset={}",
                        executionTopic,
                        executionId,
                        executionStepId,
                        result.recordMetadata().offset()
                ))
                .then();
    }
}
