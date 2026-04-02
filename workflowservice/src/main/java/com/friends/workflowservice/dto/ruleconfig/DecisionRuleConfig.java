package com.friends.workflowservice.dto.ruleconfig;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.friends.workflowservice.dto.condition.ConditionExpression;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecisionRuleConfig implements RuleConfig{

    @NotBlank(message = "Decision name required.")
    private String name;
    @JsonAlias("conditionalExpression")
    @NotNull(message = "Decision conditionExpression is required.")
    @Valid
    private ConditionExpression conditionExpression;
    @NotBlank(message = "Decision success stepCode is required.")
    private String onSuccessStepCode;
    @NotBlank(message = "Decision failure stepCode is required.")
    private String onFailureStepCode;

}
