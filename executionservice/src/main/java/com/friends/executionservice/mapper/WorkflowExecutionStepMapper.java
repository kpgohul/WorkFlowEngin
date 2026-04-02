package com.friends.executionservice.mapper;

import com.friends.executionservice.appconstant.ExecutionStepStatus;
import com.friends.executionservice.clientdto.workflowclientdto.common.WorkflowStepRule;
import com.friends.executionservice.dto.workflowexecution.WorkflowExecutionStepResponse;
import com.friends.executionservice.entity.WorkflowExecutionStep;

public class WorkflowExecutionStepMapper {

    private WorkflowExecutionStepMapper() {
    }

    public static WorkflowExecutionStep toEntity(Long workflowExecutionId, WorkflowStepRule stepRule) {
        return WorkflowExecutionStep.builder()
                .workflowExecutionId(workflowExecutionId)
                .stepId(stepRule.getStep().getId())
                .stepName(stepRule.getStep().getName())
                .steptype(stepRule.getRule().getRuleType())
                .build();
    }

    public static WorkflowExecutionStepResponse toResponse(WorkflowExecutionStep entity) {
        return WorkflowExecutionStepResponse.builder()
                .id(entity.getId())
                .workflowExecutionId(entity.getWorkflowExecutionId())
                .stepId(entity.getStepId())
                .stepName(entity.getStepName())
                .steptype(entity.getSteptype())
                .status(entity.getStatus())
                .message(entity.getMessage())
                .error(entity.getError())
                .initiatedAt(entity.getInitiatedAt())
                .terminatedAt(entity.getTerminatedAt())
                .build();
    }
}
