package com.friends.workflowservice.mapper;

import com.friends.workflowservice.dto.workflowtype.CreateWorkflowTypeRequest;
import com.friends.workflowservice.dto.workflowtype.UpdateWorkflowTypeRequest;
import com.friends.workflowservice.dto.workflowtype.WorkflowTypeResponse;
import com.friends.workflowservice.entity.WorkflowType;

public class WorkflowTypeMapper {

    public static WorkflowType toEntity(CreateWorkflowTypeRequest request){
        return WorkflowType.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .version(request.getVersion())
                .status(request.getStatus())
                .build();
    }

    public static WorkflowType toEntity(UpdateWorkflowTypeRequest request, WorkflowType original){
        original.setCode(request.getCode());
        original.setName(request.getName());
        original.setDescription(request.getDescription());
        original.setVersion(request.getVersion());
        original.setStatus(request.getStatus());
        return original;
    }

    public static WorkflowTypeResponse toResponse(WorkflowType workflowType){
        return WorkflowTypeResponse.builder()
                .id(workflowType.getId())
                .name(workflowType.getName())
                .code(workflowType.getCode())
                .description(workflowType.getDescription())
                .version(workflowType.getVersion())
                .status(workflowType.getStatus())
                .createdAt(workflowType.getCreatedAt())
                .updatedAt(workflowType.getUpdatedAt())
                .createdBy(workflowType.getCreatedBy())
                .updatedBy(workflowType.getUpdatedBy())
                .build();
    }
}
