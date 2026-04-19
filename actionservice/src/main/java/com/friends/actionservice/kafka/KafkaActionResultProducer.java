package com.friends.actionservice.kafka;

import com.friends.actionservice.actionsdto.ActionResponse;
import com.friends.actionservice.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class KafkaActionResultProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaActionResultProducer.class);

    private final KafkaSender<String, String> sender;
    private final KafkaProperties props;
    private final JsonMapper objectMapper;

    public Mono<Void> send(ActionResponse response) {
        if (response == null) {
            return Mono.empty();
        }

        String payload;
        try {
            payload = objectMapper.writeValueAsString(response);
        } catch (JacksonException e) {
            return Mono.error(e);
        }

        String key = response.getExecutionId() + ":" + response.getExecutionStepId();
        ProducerRecord<String, String> record = new ProducerRecord<>(props.getResults().getTopic(), key, payload);

        return sender.send(Mono.just(SenderRecord.create(record, key)))
                .next()
                .doOnNext(r -> log.info("Published action result key={} topic={}", key, props.getResults().getTopic()))
                .then();
    }
}
