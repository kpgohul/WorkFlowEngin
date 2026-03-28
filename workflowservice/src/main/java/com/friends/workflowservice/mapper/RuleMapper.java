package com.friends.workflowservice.mapper;

import com.friends.workflowservice.dto.rule.CreateRuleRequest;
import com.friends.workflowservice.dto.rule.RuleResponse;
import com.friends.workflowservice.dto.rule.UpdateRuleRequest;
import com.friends.workflowservice.entity.WorkflowRule;
import com.friends.workflowservice.util.JsonUtils;
import com.friends.workflowservice.util.condition.ConditionExpression;
import tools.jackson.core.type.TypeReference;

public class RuleMapper {

    public static WorkflowRule toEntity(CreateRuleRequest request){
        return WorkflowRule.builder()
                .ruleExpression(JsonUtils.toJson(request.getRuleExpression()))
                .nextStepCodeOnFailure(request.getNextStepCodeOnFailure())
                .failureHandlerType(request.getFailureHandlerType())
                .build();
    }

    public static WorkflowRule toEntity(UpdateRuleRequest request){
        return WorkflowRule.builder()
                .ruleExpression(JsonUtils.toJson(request.getRuleExpression()))
                .nextStepCodeOnFailure(request.getNextStepCodeOnFailure())
                .failureHandlerType(request.getFailureHandlerType())
                .build();
    }

    public static RuleResponse toResponse(WorkflowRule rule){
        return RuleResponse.builder()
                .id(rule.getId())
                .ruleExpression(JsonUtils.fromJson(rule.getRuleExpression(), new TypeReference<ConditionExpression>() {}))
                .failureHandlerType(rule.getFailureHandlerType())
                .stepId(rule.getStepId())
                .nextStepCodeOnFailure(rule.getNextStepCodeOnFailure())
                .build();
    }

}
