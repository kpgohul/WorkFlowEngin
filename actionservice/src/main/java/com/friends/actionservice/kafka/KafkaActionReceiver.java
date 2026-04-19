package com.friends.actionservice.kafka;

import com.friends.actionservice.actionsdto.ActionRequest;
import com.friends.actionservice.service.ActionService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import jakarta.annotation.PostConstruct;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class KafkaActionReceiver {

    private static final Logger log = LoggerFactory.getLogger(KafkaActionReceiver.class);

    private static final String EXECUTION_ID_HEADER = "executionId";
    private static final String EXECUTION_STEP_ID_HEADER = "executionStepId";

    private final KafkaReceiver<String, String> receiver;
    private final JsonMapper objectMapper;
    private final ActionService actionService;

    @Value("${app.kafka.receiver.enabled}")
    private boolean receiverEnabled;

    @PostConstruct
    public void start() {
        if (!receiverEnabled) {
            log.info("KafkaActionReceiver is disabled (app.kafka.receiver.enabled=false)");
            return;
        }

        receiver.receive()
                .concatMap(this::handleRecord)
                .onErrorContinue((ex, obj) -> log.error("Error in KafkaActionReceiver", ex))
                .subscribe();
    }

    private Mono<Void> handleRecord(ReceiverRecord<String, String> record) {
        Long executionId = headerLong(record, EXECUTION_ID_HEADER);
        Long executionStepId = headerLong(record, EXECUTION_STEP_ID_HEADER);

        return Mono.fromCallable(() -> objectMapper.readValue(record.value(), ActionRequest.class))
                .flatMap(action -> actionService.handleActionProcess(executionId, executionStepId, action))
                .doOnSuccess(v -> record.receiverOffset().acknowledge())
                .doOnError(ex -> log.error("Failed handling action message key={} value={}", record.key(), record.value(), ex));
    }

    private Long headerLong(ReceiverRecord<String, String> record, String name) {
        Header header = record.headers() == null ? null : record.headers().lastHeader(name);
        if (header == null || header.value() == null) {
            return null;
        }
        try {
            return Long.parseLong(new String(header.value(), StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            return null;
        }
    }
}
