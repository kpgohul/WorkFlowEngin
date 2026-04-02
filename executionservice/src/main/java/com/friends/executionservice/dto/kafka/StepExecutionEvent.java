package com.friends.executionservice.dto.kafka;

import com.friends.executionservice.appconstant.StepType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StepExecutionEvent {
    private StepEventType eventType;
    private Long workflowExecutionId;
    private Long stepId;
    private StepType stepType;
    private Boolean success;
    private String message;
    private String error;
    private Map<String, Object> payload;
    private Instant publishedAt;
}

