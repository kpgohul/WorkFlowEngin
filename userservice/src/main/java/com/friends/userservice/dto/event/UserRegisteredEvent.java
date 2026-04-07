package com.friends.userservice.dto.event;

import java.time.Instant;

public record UserRegisteredEvent(
        Long accountId,
        String username,
        String email,
        Instant registeredAt
) {}

