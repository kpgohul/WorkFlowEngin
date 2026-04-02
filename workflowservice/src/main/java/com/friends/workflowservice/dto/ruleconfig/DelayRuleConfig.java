package com.friends.workflowservice.dto.ruleconfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DelayRuleConfig implements RuleConfig{
    private String name;
    private Long delayDurationInMillis;
}
