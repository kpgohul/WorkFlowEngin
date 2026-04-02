package com.friends.executionservice.dto.action;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StepActionRequest {

    @NotNull(message = "stepId is required")
    private Long stepId;

    private Boolean success;
    private String message;
    private String error;
    private Map<String, Object> outputPayload;
}

