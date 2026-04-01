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
public class ApprovalRuleConfig implements RuleConfig{
    @NotBlank(message = "Approval name required.")
    private String name;
    @NotNull(message = "Approval channel is required")
    private Channel channel;
    @NotNull(message = "Approval Type is required.")
    private ApprovalType approvalType;

    // REQUIRED FOR ANY_ONE
    private Long approverId;
    //REQUIRED FOR ANY
    private Integer approverRoleId;
    private Integer teamId;

    @NotBlank(message = "Approval subject part is required.")
    private String subject;
    @NotBlank(message = "Approval body part is required.")
    private String body;
}
