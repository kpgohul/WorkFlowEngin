package com.friends.executionservice.dto.workflowexecution;

import java.time.Instant;
import java.util.List;

import com.friends.executionservice.appconstant.ExecutionStatus;
import com.friends.executionservice.clientdto.workflowclientdto.workflow.Workflow;

import com.friends.executionservice.clientdto.workflowclientdto.workflowtype.WorkflowType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowExecutionResponse {
    private Long id;
    private Long workflowId;
    private Long workflowTypeId;
    private Long currentStepId;
    private String inputPayload;
    private ExecutionStatus status;
    private String error;
    private Instant startedAt;
    private Instant terminatedAt;
    private Long initiatedBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private List<WorkflowExecutionStepResponse> stepExecutionList;
    private Workflow workflow;
    private WorkflowType workflowType;
}
