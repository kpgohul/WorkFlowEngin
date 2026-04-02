package com.friends.executionservice.clientdto.workflowclientdto.ruleconfig;

import com.friends.executionservice.appconstant.ApprovalType;
import com.friends.executionservice.appconstant.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalRuleConfig implements RuleConfig{
    private String name;
    private Channel channel;
    private ApprovalType approvalType;
    private Long approverId;
    private Integer approverRoleId;
    private Integer teamId;
    private String subject;
    private String body;
}
