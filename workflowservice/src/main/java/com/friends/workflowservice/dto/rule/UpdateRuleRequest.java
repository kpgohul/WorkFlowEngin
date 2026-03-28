package com.friends.workflowservice.dto.rule;

import com.friends.workflowservice.appconstant.steprule.RuleFailureHandlerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRuleRequest {
    private String ruleExpression;
    private RuleFailureHandlerType failureHandlerType;
    private String nextStepCodeOnFailure;
}
