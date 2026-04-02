package com.friends.executionservice.clientdto.workflowclientdto.ruleconfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRuleConfig implements RuleConfig{
    private String name;
}
