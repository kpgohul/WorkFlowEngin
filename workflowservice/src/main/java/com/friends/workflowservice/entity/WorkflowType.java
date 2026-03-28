package com.friends.workflowservice.entity;

import com.friends.workflowservice.appconstant.workflowtype.WorkflowTypeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workflow_type")
public class WorkflowType {

    @Id
    private Long id;
    private String code; //EMPLOYEE_ONBOARDING, LEAVE_REQUEST, ETC
    private String name; //Employee Onboarding, Leave Request, etc //Human Readabble
    private String description; //Optional
    private WorkflowTypeStatus status;
    private Integer version;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    @CreatedBy
    private Long createdBy;
    @LastModifiedBy
    private Long updatedBy;

}
