package com.friends.executionservice.clientdto.workflowclientdto.workflow;

import com.friends.executionservice.appconstant.WorkflowStatus;
import com.friends.executionservice.clientdto.workflowclientdto.common.WorkflowStepRule;
import com.friends.executionservice.clientdto.workflowclientdto.workflowtype.WorkflowType;
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
public class Workflow {
    private Long id;
    private Long workflowTypeId;
    private String name;
    private String description;
    private WorkflowStatus status;
    private Integer version;
    private Boolean isActive;
    private List<WorkflowStepRule> stepRule;
    private WorkflowType workflowType;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
