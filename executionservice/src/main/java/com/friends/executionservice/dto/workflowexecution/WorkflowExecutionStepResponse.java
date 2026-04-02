package com.friends.executionservice.dto.workflowexecution;

import java.time.Instant;

import com.friends.executionservice.appconstant.ExecutionStepStatus;
import com.friends.executionservice.appconstant.StepType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowExecutionStepResponse {
    private Long id;
    private Long workflowExecutionId;
    private Long stepId;
    private String stepName;
    private StepType steptype;
    private Integer orderOfExecution;
    private ExecutionStepStatus status;
    private String inputPayload;
    private String message;
    private String error;
    private Instant initiatedAt;
    private Instant terminatedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
