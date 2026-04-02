package com.friends.workflowservice.dto.common;

import com.friends.workflowservice.dto.workflowrule.WorkflowRuleResponse;
import com.friends.workflowservice.dto.workflowstep.WorkflowStepResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStepRuleResponse {
    private WorkflowStepResponse step;
    private WorkflowRuleResponse rule;
}
