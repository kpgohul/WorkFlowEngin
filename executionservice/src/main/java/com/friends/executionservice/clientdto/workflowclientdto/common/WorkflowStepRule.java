package com.friends.executionservice.clientdto.workflowclientdto.common;

import com.friends.executionservice.clientdto.workflowclientdto.workflowrule.WorkflowRule;
import com.friends.executionservice.clientdto.workflowclientdto.workflowstep.WorkflowStep;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStepRule {
    private WorkflowStep step;
    private WorkflowRule rule;
}
