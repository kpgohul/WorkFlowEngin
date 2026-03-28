package com.friends.workflowservice.dto.workflowtype;

import com.friends.workflowservice.appconstant.workflowtype.WorkflowFieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowTypeFieldRequest {

    @NotBlank(message = "fieldKey is required")
    private String fieldKey; //employeeName, employeeEmail, etc
    @NotBlank(message = "fieldLabel is required")
    private String fieldLabel; //Employee Name, Employee Email, etc
    @NotNull(message = "fieldType is required")
    private WorkflowFieldType fieldType;
    @NotNull(message = "isRequired is required")
    private Boolean isRequired;
    private List<String> allowedValues;
    private String defaultValue; //Optional, can be used to pre-fill forms
    private String validationRegex;
    @NotNull(message = "displayOrder is required")
    @Positive(message = "displayOrder must be a positive integer")
    private Integer displayOrder; //To control the order of fields in forms

}
