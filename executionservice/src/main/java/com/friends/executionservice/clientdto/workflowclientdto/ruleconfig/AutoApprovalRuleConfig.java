package com.friends.executionservice.clientdto.workflowclientdto.ruleconfig;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoApprovalRuleConfig implements RuleConfig {
    @NotBlank(message = "Auto Approval name required.")
    private String name;
}
