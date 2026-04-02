package com.friends.workflowservice.dto.ruleconfig;

import com.friends.workflowservice.appconstant.ApprovalType;
import com.friends.workflowservice.appconstant.Channel;
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
public class AutoApprovalRuleConfig implements RuleConfig{
    @NotBlank(message = "Auto Approval name required.")
    private String name;
}
