package com.friends.workflowservice.dto.workflow;

import com.friends.workflowservice.appconstant.workflow.WorkflowStatus;
import com.friends.workflowservice.dto.common.UpdateStepRuleRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWorkflowRequest {
    private Long id;
    private Long workflowTypeId;
    private String name;
    private String description;
    private WorkflowStatus status;
    private Integer version;
    private Boolean isActive;
    private List<UpdateStepRuleRequest> stepRule;
}
