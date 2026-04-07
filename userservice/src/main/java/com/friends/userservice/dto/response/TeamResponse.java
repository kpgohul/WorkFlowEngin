package com.friends.userservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TeamResponse {
    private Long id;
    private String name;
    private String description;
    private Long createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}

