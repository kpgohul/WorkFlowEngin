package com.friends.workflowservice.entity;

import com.friends.workflowservice.appconstant.workflowtype.WorkflowFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workflow_type_field")
public class WorkflowTypeField {
    @Id
    @ReadOnlyProperty
    private Long id;
    private Long workflowTypeId;
    private String fieldKey; //employeeName, employeeEmail, etc
    private String fieldLabel; //Employee Name, Employee Email, etc
    private WorkflowFieldType fieldType;
    private Boolean isRequired;
    private String defaultValue; //Optional, can be used to pre-fill forms
    private List<String> allowedValues;
    private String validationRegex;
    private Integer displayOrder; //To control the order of fields in forms

}
