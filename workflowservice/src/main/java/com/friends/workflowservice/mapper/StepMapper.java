package com.friends.workflowservice.mapper;

import com.friends.workflowservice.dto.step.CreateStepRequest;
import com.friends.workflowservice.dto.step.StepResponse;
import com.friends.workflowservice.dto.step.UpdateStepRequest;
import com.friends.workflowservice.entity.WorkflowStep;

public class StepMapper {

    public static WorkflowStep toEntity(CreateStepRequest request, Long workflowId){
        return WorkflowStep.builder()
                .name(request.getName())
                .stepCode(request.getStepCode())
                .additionInfo(request.getAdditionInfo())
                .status(request.getStatus())
                .stepOrder(request.getStepOrder())
                .notifyTo(request.getNotifyTo())
                .workflowId(workflowId)
                .timeOutInMillis(request.getTimeOutInMillis())
                .stepType(request.getStepType())
                .build();
    }

    public static WorkflowStep toEntity(UpdateStepRequest request, Long workflowId){
        return WorkflowStep.builder()
                .name(request.getName())
                .stepCode(request.getStepCode())
                .additionInfo(request.getAdditionInfo())
                .status(request.getStatus())
                .stepOrder(request.getStepOrder())
                .notifyTo(request.getNotifyTo())
                .workflowId(workflowId)
                .timeOutInMillis(request.getTimeOutInMillis())
                .stepType(request.getStepType())
                .build();
    }

    public static StepResponse toResponse(WorkflowStep step){
        return StepResponse.builder()
                .id(step.getId())
                .name(step.getName())
                .stepCode(step.getStepCode())
                .additionInfo(step.getAdditionInfo())
                .status(step.getStatus())
                .stepOrder(step.getStepOrder())
                .notifyTo(step.getNotifyTo())
                .workflowId(step.getWorkflowId())
                .timeOutInMillis(step.getTimeOutInMillis())
                .stepType(step.getStepType())
                .build();
    }
}
