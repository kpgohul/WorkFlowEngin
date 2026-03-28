package com.friends.workflowservice.dto.rule;

import com.friends.workflowservice.appconstant.steprule.RuleFailureHandlerType;
import com.friends.workflowservice.util.condition.ConditionExpression;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleResponse {
    private Long id;
    private Long stepId;
    private ConditionExpression ruleExpression;
    private RuleFailureHandlerType failureHandlerType;
    private String nextStepCodeOnFailure;
}
