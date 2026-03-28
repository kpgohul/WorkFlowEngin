package com.friends.workflowservice.mapper;

import com.friends.workflowservice.dto.workflowtype.WorkflowTypeFieldRequest;
import com.friends.workflowservice.dto.workflowtype.WorkflowTypeFieldResponse;
import com.friends.workflowservice.entity.WorkflowTypeField;

public class WorkflowTypeFieldMapper {

    public static WorkflowTypeField toEntity(WorkflowTypeFieldRequest request, Long workflowTypeId){
        return WorkflowTypeField.builder()
                .workflowTypeId(workflowTypeId)
                .fieldKey(request.getFieldKey())
                .fieldLabel(request.getFieldLabel())
                .defaultValue(request.getDefaultValue())
                .displayOrder(request.getDisplayOrder())
                .validationRegex(request.getValidationRegex())
                .allowedValues(request.getAllowedValues())
                .isRequired(request.getIsRequired())
                .fieldType(request.getFieldType())
                .build();
    }

    public static WorkflowTypeFieldResponse toResponse(WorkflowTypeField field){
        return WorkflowTypeFieldResponse.builder()
                .id(field.getId())
                .workflowTypeId(field.getWorkflowTypeId())
                .fieldKey(field.getFieldKey())
                .fieldLabel(field.getFieldLabel())
                .defaultValue(field.getDefaultValue())
                .displayOrder(field.getDisplayOrder())
                .allowedValues(field.getAllowedValues())
                .validationRegex(field.getValidationRegex())
                .isRequired(field.getIsRequired())
                .fieldType(field.getFieldType())
                .build();
    }


}
