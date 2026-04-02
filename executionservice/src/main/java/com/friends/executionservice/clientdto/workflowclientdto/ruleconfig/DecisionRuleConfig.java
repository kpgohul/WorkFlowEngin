package com.friends.executionservice.clientdto.workflowclientdto.ruleconfig;

import com.friends.executionservice.clientdto.workflowclientdto.condition.ConditionExpression;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecisionRuleConfig implements RuleConfig {
    private String name;
    private ConditionExpression conditionExpression;
    private String onSuccessStepCode;
    private String onFailureStepCode;
}
