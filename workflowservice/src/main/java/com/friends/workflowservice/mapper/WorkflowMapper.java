package com.friends.workflowservice.mapper;

import com.friends.workflowservice.dto.workflow.CreateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.UpdateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.WorkflowResponse;
import com.friends.workflowservice.entity.Workflow;

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
}
