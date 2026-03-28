package com.friends.workflowservice.dto.common;

import com.friends.workflowservice.dto.rule.RuleResponse;
import com.friends.workflowservice.dto.step.StepResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StepRuleResponse {
    private StepResponse step;
    private RuleResponse rule;
}
