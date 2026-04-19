package com.friends.workflowservice.dto.workflowtype;

import com.friends.workflowservice.appconstant.WorkflowTypeStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorkflowTypeRequest {

    @NotBlank(message = "WorkflowType should have a code")
    private String code; //EMPLOYEE_ONBOARDING, LEAVE_REQUEST, ETC
    @NotBlank(message = "WorkflowType should have a name")
    private String name; //Employee Onboarding, Leave Request, etc //Human Readabble
    private String description; //Optional
    @Builder.Default
    private WorkflowTypeStatus status = WorkflowTypeStatus.ACTIVE;
    private Integer version = 1;
    @Valid
    private List<WorkflowTypeFieldRequest> fields;

}
