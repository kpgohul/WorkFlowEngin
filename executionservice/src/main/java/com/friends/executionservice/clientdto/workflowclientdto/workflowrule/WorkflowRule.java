package com.friends.executionservice.clientdto.workflowclientdto.workflowrule;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.friends.executionservice.appconstant.StepType;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.ApprovalRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.AutoApprovalRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.DecisionRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.DelayRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.NotificationRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.RuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.TaskRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.WebHookRuleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowRule {

    private Long id;
    private Long stepId;
    private StepType ruleType;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "ruleType",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TaskRuleConfig.class, name = "TASK"),
            @JsonSubTypes.Type(value = ApprovalRuleConfig.class, name = "APPROVAL"),
            @JsonSubTypes.Type(value = DecisionRuleConfig.class, name = "DECISION"),
            @JsonSubTypes.Type(value = AutoApprovalRuleConfig.class, name = "AUTO_APPROVAL"),
            @JsonSubTypes.Type(value = NotificationRuleConfig.class, name = "NOTIFICATION"),
            @JsonSubTypes.Type(value = DelayRuleConfig.class, name = "DELAY"),
            @JsonSubTypes.Type(value = WebHookRuleConfig.class, name = "WEBHOOK")
    })
    private RuleConfig ruleConfig;
}
