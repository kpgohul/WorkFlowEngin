package com.friends.workflowservice.dto.common;

import com.friends.workflowservice.dto.workflowrule.WorkflowRuleRequest;
import com.friends.workflowservice.dto.workflowstep.WorkflowStepRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStepRuleRequest {
    private WorkflowStepRequest step;
    private WorkflowRuleRequest rule;
}
