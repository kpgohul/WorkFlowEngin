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
public class UpdateWorkflowExecutionRequest {

    @NotNull(message = "id is required")
    private Long id;
    @NotNull(message = "inputPayload is required")
    private Map<String, Object> inputPayload;

}
