package com.friends.workflowservice.dto.workflowrule;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.friends.workflowservice.appconstant.RuleType;
import com.friends.workflowservice.dto.ruleconfig.ApprovalRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.AutoApprovalRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.DecisionRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.DelayRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.NotificationRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.RuleConfig;
import com.friends.workflowservice.dto.ruleconfig.TaskRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.WebHookRuleConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowRuleRequest {

    @NotNull(message = "ruleType is required.")
    private RuleType ruleType;

    @NotNull(message = "rule config si required.")
    @Valid
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
