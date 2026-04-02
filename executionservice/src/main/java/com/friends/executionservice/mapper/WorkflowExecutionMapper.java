package com.friends.executionservice.mapper;

import com.friends.executionservice.appconstant.ExecutionStatus;
import com.friends.executionservice.dto.workflowexecution.CreateWorkflowExecutionRequest;
import com.friends.executionservice.dto.workflowexecution.WorkflowExecutionResponse;
import com.friends.executionservice.dto.workflowexecution.WorkflowExecutionStepResponse;
import com.friends.executionservice.entity.WorkflowExecution;
import com.friends.executionservice.util.common.JsonUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class WorkflowExecutionMapper {

    private WorkflowExecutionMapper() {
    }

    public static WorkflowExecution toEntity(CreateWorkflowExecutionRequest request, Long workflowTypeId, Long workflowId) {
        return WorkflowExecution.builder()
                .workflowId(request.getWorkflowId())
                .workflowTypeId(workflowTypeId)
                .workflowId(workflowId)
                .inputPayload(JsonUtils.toJson(request.getInputPayload()))
                .initiatedAt(Instant.now())
                .build();
    }

    public static WorkflowExecution applyUpdate(WorkflowExecution existing, Map<String, Object> inputPayload) {
        existing.setInputPayload(JsonUtils.toJson(inputPayload));
        return existing;
    }

    public static WorkflowExecutionResponse toResponse(WorkflowExecution entity, List<WorkflowExecutionStepResponse> steps) {
        return WorkflowExecutionResponse.builder()
                .id(entity.getId())
                .workflowId(entity.getWorkflowId())
                .workflowTypeId(entity.getWorkflowTypeId())
                .currentStepId(entity.getCurrentStepId())
                .inputPayload(entity.getInputPayload())
                .status(entity.getStatus())
                .error(entity.getError())
                .startedAt(entity.getInitiatedAt())
                .terminatedAt(entity.getTerminatedAt())
                .initiatedBy(entity.getInitiatedBy())
                .stepExecutionList(steps)
                .build();
    }
}
