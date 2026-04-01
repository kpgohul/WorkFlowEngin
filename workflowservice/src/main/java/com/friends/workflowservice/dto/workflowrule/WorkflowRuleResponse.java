package com.friends.workflowservice.dto.workflowrule;

import com.friends.workflowservice.appconstant.RuleType;
import com.friends.workflowservice.dto.condition.ConditionExpression;
import com.friends.workflowservice.dto.ruleconfig.RuleConfig;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowRuleResponse {

    private Long id;
    private Long stepId;
    private RuleType ruleType;
    private RuleConfig ruleConfig;

}
