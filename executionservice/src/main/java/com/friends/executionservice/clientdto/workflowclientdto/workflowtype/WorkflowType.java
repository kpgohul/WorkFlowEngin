package com.friends.executionservice.clientdto.workflowclientdto.workflowtype;

import com.friends.executionservice.appconstant.WorkflowTypeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowType {
    private Long id;
    private String code; //EMPLOYEE_ONBOARDING, LEAVE_REQUEST, ETC
    private String name; //Employee Onboarding, Leave Request, etc //Human Readabble
    private String description; //Optional
    private WorkflowTypeStatus status;
    private Integer version;
    private List<WorkflowTypeField> fields;
    private Instant createdAt;
    private Instant updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
