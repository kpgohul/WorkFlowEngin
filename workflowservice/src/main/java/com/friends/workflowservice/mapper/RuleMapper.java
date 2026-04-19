package com.friends.workflowservice.mapper;

import com.friends.workflowservice.appconstant.RuleType;
import com.friends.workflowservice.dto.ruleconfig.ApprovalRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.AutoApprovalRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.DecisionRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.DelayRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.NotificationRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.RuleConfig;
import com.friends.workflowservice.dto.ruleconfig.TaskRuleConfig;
import com.friends.workflowservice.dto.ruleconfig.WebHookRuleConfig;
import com.friends.workflowservice.dto.workflowrule.WorkflowRuleRequest;
import com.friends.workflowservice.dto.workflowrule.WorkflowRuleResponse;
import com.friends.workflowservice.entity.WorkflowRule;
import com.friends.workflowservice.util.common.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

public class RuleMapper {

    public static WorkflowRule toEntity(Long stepId, WorkflowRuleRequest request){
        return WorkflowRule.builder()
                .stepId(stepId)
                .ruleType(request.getRuleType())
                .ruleConfig(JsonUtils.toJson(request.getRuleConfig()))
                .build();
    }

    public static WorkflowRule toEntity(WorkflowRule existing, WorkflowRuleRequest request) {
        return WorkflowRule.builder()
                .id(existing.getId())
                .stepId(existing.getStepId())
                .ruleType(request.getRuleType())
                .ruleConfig(JsonUtils.toJson(request.getRuleConfig()))
                .build();
    }

    public static WorkflowRuleResponse toResponse(WorkflowRule rule){
        return WorkflowRuleResponse.builder()
                .id(rule.getId())
                .stepId(rule.getStepId())
                .ruleType(rule.getRuleType())
                .ruleConfig(readRuleConfig(rule.getRuleConfig(), rule.getRuleType()))
                .build();
    }

    private static RuleConfig readRuleConfig(String json, RuleType ruleType) {
        if (json == null || json.isBlank() || ruleType == null) {
            return null;
        }

        return switch (ruleType) {
            case TASK -> JsonUtils.fromJson(json, new TypeReference<TaskRuleConfig>() {});
            case APPROVAL -> JsonUtils.fromJson(json, new TypeReference<ApprovalRuleConfig>() {});
            case DECISION -> JsonUtils.fromJson(json, new TypeReference<DecisionRuleConfig>() {});
            case AUTO_APPROVAL -> JsonUtils.fromJson(json, new TypeReference<AutoApprovalRuleConfig>() {});
            case NOTIFICATION -> JsonUtils.fromJson(json, new TypeReference<NotificationRuleConfig>() {});
            case DELAY -> JsonUtils.fromJson(json, new TypeReference<DelayRuleConfig>() {});
            case WEBHOOK -> JsonUtils.fromJson(json, new TypeReference<WebHookRuleConfig>() {});
        };
    }

}
