package com.friends.authserver.kafka;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.friends.authserver.dto.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationProducer {

    @Value("${kafka.topic.user-registered}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    public void publish(UserRegisteredEvent event) {
        try {
            String payload = jsonMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.accountId().toString(), payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to send user.registered event for accountId={}: {}",
                                    event.accountId(), ex.getMessage(), ex);
                        } else {
                            log.info("Published user.registered event for accountId={} to partition={} offset={}",
                                    event.accountId(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error serializing UserRegisteredEvent for accountId={}: {}", event.accountId(), e.getMessage(),
                    e);
        }
    }
}
