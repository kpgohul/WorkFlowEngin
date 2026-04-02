package com.friends.executionservice.clientdto.workflowclientdto.workflowtype;

import com.friends.executionservice.appconstant.WorkflowFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowTypeField {
    private Long id;
    private Long workflowTypeId;
    private String fieldKey; //employeeName, employeeEmail, etc
    private String fieldLabel; //Employee Name, Employee Email, etc
    private WorkflowFieldType fieldType;
    private Boolean isRequired;
    private List<String> allowedValues;
    private String defaultValue; //Optional, can be used to pre-fill forms
    private String validationRegex;
    private Integer displayOrder; //To control the order of fields in forms
}
