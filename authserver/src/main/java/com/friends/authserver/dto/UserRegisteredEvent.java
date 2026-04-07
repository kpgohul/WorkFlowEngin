package com.friends.authserver.dto;

import java.time.Instant;

public record UserRegisteredEvent(
        Long accountId,
        String username,
        String email,
        Instant registeredAt
) {}

