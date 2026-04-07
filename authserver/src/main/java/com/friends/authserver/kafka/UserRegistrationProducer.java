package com.friends.authserver.kafka;

import tools.jackson.databind.ObjectMapper;
import com.friends.authserver.dto.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationProducer {

    private static final String TOPIC = "user.registered";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(UserRegisteredEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.accountId().toString(), payload)
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
            log.error("Error serializing UserRegisteredEvent for accountId={}: {}", event.accountId(), e.getMessage(), e);
        }
    }
}
