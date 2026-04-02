package com.friends.workflowservice.mapper;

import com.friends.workflowservice.dto.common.WorkflowStepRuleResponse;
import com.friends.workflowservice.dto.workflowrule.WorkflowRuleResponse;
import com.friends.workflowservice.dto.workflowstep.WorkflowStepRequest;
import com.friends.workflowservice.dto.workflowstep.WorkflowStepResponse;
import com.friends.workflowservice.entity.WorkflowStep;
import com.friends.workflowservice.util.step.WorkflowStepUtil;

public class StepMapper {

    public static WorkflowStep toEntity(Long workflowId, WorkflowStepRequest request){
        return WorkflowStep.builder()
                .workflowId(workflowId)
                .name(request.getName())
                .stepCode(request.getStepCode())
                .stepLine(request.getStepLine())
                .isLast(request.getIsLast())
                .stepTimeoutInMillis(request.getStepTimeoutInMillis())
                .build();
    }

    public static WorkflowStepResponse toResponse(WorkflowStep step){
        return WorkflowStepResponse.builder()
                .id(step.getId())
                .name(step.getName())
                .workflowId(step.getWorkflowId())
                .stepCode(step.getStepCode())
                .stepLine(step.getStepLine())
                .isLast(step.getIsLast())
                .stepTimeoutInMillis(step.getStepTimeoutInMillis())
                .build();
    }

    public static WorkflowStepRuleResponse toStepRuleResponse(WorkflowStep step, WorkflowRuleResponse rule) {
        return WorkflowStepRuleResponse.builder()
                .step(toResponse(step))
                .rule(rule)
                .build();
    }

    public static WorkflowStepRequest normalizeStepRequest(WorkflowStepRequest step) {
        step.setStepCode(WorkflowStepUtil.normalizeStepCode(step.getStepCode()));
        return step;
    }
}
