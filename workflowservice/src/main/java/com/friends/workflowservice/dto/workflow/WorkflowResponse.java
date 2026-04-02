package com.friends.workflowservice.dto.workflow;

import com.friends.workflowservice.appconstant.WorkflowStatus;
import com.friends.workflowservice.dto.common.WorkflowStepRuleResponse;
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
public class WorkflowResponse {
    private Long id;
    private Long workflowTypeId;
    private String name;
    private String description;
    private WorkflowStatus status;
    private Integer version;
    private Boolean isActive;
    private List<WorkflowStepRuleResponse> stepRule;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
