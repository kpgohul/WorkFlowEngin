package com.friends.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@JsonPropertyOrder({
        "id",
        "name",
        "description",
        "createdBy",
        "createdAt",
        "updatedAt"
})
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

