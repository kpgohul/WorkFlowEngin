package com.friends.executionservice.kafkaclient;

import com.friends.executionservice.clientdto.actionclientdto.ActionResponse;
import com.friends.executionservice.service.WorkflowExecutionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import com.fasterxml.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaStepResultConsumer {

    private final KafkaReceiver<String, String> kafkaReceiver;
    private final WorkflowExecutionService workflowExecutionService;
    private final JsonMapper objectMapper;

    @PostConstruct
    public void subscribe() {
        kafkaReceiver.receive()
                .concatMap(record -> parse(record.value())
                        .flatMap(response -> workflowExecutionService.applyStepResult(
                                response.getExecutionId(),
                                response.getExecutionStepId(),
                                Boolean.TRUE.equals(response.getIsSuccess()),
                                response.getMessage(),
                                response.getError()))
                        .doOnError(ex -> log.error("Failed processing step result", ex))
                        .onErrorResume(ex -> Mono.empty())
                        .doFinally(signal -> record.receiverOffset().acknowledge()))
                .subscribe();
    }

    private Mono<ActionResponse> parse(String payload) {
        try {
            return Mono.just(objectMapper.readValue(payload, ActionResponse.class));
        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }
}
