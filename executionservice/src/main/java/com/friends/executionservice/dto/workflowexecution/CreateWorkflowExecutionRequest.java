package com.friends.executionservice.dto.workflowexecution;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorkflowExecutionRequest {

    @NotNull(message = "workflowId is required")
    private Long workflowId;
    @NotNull(message = "inputPayload is required")
    private Map<String, Object> inputPayload;
}
