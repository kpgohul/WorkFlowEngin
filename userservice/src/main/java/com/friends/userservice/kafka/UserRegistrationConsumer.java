package com.friends.userservice.kafka;

import tools.jackson.databind.ObjectMapper;
import com.friends.userservice.dto.event.UserRegisteredEvent;
import com.friends.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user.registered", groupId = "user-service-group")
    @Transactional
    public void onUserRegistered(String message) {
        log.info("Received Kafka message on topic 'user.registered': {}", message);
        try {
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);
            userService.createUserFromEvent(event);
        } catch (Exception e) {
            log.error("Failed to process user.registered event. Payload: {}. Error: {}", message, e.getMessage(), e);
            throw new RuntimeException("Failed to process user.registered event", e);
        }
    }
}
