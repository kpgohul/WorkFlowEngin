package com.friends.workflowservice.dto.workflowtype;

import com.friends.workflowservice.appconstant.workflowtype.WorkflowTypeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowTypeResponse {
    private Long id;
    private String code; //EMPLOYEE_ONBOARDING, LEAVE_REQUEST, ETC
    private String name; //Employee Onboarding, Leave Request, etc //Human Readabble
    private String description; //Optional
    private WorkflowTypeStatus status;
    private Integer version;
    private List<WorkflowTypeFieldResponse> fields;
    private Instant createdAt;
    private Instant updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
