package com.friends.workflowservice.mapper;

import com.friends.workflowservice.dto.common.WorkflowStepRuleRequest;
import com.friends.workflowservice.dto.common.WorkflowStepRuleResponse;
import com.friends.workflowservice.dto.workflow.CreateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.UpdateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.WorkflowResponse;
import com.friends.workflowservice.entity.Workflow;

import java.util.List;

public class WorkflowMapper {

    public static Workflow toEntity(CreateWorkflowRequest request){
        return Workflow.builder()
                .name(request.getName())
                .description(request.getDescription())
                .workflowTypeId(request.getWorkflowTypeId())
                .isActive(request.getIsActive())
                .status(request.getStatus())
                .version(request.getVersion())
                .build();
    }

    public static Workflow toEntityWithDefaults(CreateWorkflowRequest request) {
        Workflow entity = toEntity(request);
        entity.setVersion(request.getVersion() == null ? 1 : request.getVersion());
        entity.setIsActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive());
        return entity;
    }

    public static Workflow toEntity(UpdateWorkflowRequest request){
        return Workflow.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .workflowTypeId(request.getWorkflowTypeId())
                .isActive(request.getIsActive())
                .status(request.getStatus())
                .version(request.getVersion())
                .build();
    }

    public static Workflow mergeForUpdate(UpdateWorkflowRequest request, Workflow existing) {
        return Workflow.builder()
                .id(existing.getId())
                .name(request.getName() == null ? existing.getName() : request.getName())
                .description(request.getDescription() == null ? existing.getDescription() : request.getDescription())
                .workflowTypeId(request.getWorkflowTypeId() == null ? existing.getWorkflowTypeId() : request.getWorkflowTypeId())
                .status(request.getStatus() == null ? existing.getStatus() : request.getStatus())
                .version(request.getVersion() == null ? existing.getVersion() : request.getVersion())
                .isActive(request.getIsActive() == null ? existing.getIsActive() : request.getIsActive())
                .build();
    }

    public static WorkflowResponse toResponse(Workflow workflow){
        return WorkflowResponse.builder()
                .id(workflow.getId())
                .name(workflow.getName())
                .description(workflow.getDescription())
                .workflowTypeId(workflow.getWorkflowTypeId())
                .isActive(workflow.getIsActive())
                .status(workflow.getStatus())
                .version(workflow.getVersion())
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .createdBy(workflow.getCreatedBy())
                .updatedBy(workflow.getUpdatedBy())
                .build();
    }

    public static WorkflowResponse toResponse(Workflow workflow, List<WorkflowStepRuleResponse> stepRules) {
        WorkflowResponse response = toResponse(workflow);
        response.setStepRule(stepRules);
        return response;
    }

    public static List<WorkflowStepRuleRequest> attachWorkflowId(Long workflowId, List<WorkflowStepRuleRequest> stepRules) {
        return stepRules.stream()
                .peek(item -> item.getStep().setWorkflowId(workflowId))
                .toList();
    }
}
