package com.friends.executionservice.service;

import com.friends.executionservice.clientdto.workflowclientdto.workflow.Workflow;
import com.friends.executionservice.dto.workflowexecution.WorkflowExecutionStepResponse;
import com.friends.executionservice.entity.WorkflowExecution;
import com.friends.executionservice.entity.WorkflowExecutionStep;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

public interface WorkflowExecutionStepService {
    Mono<List<WorkflowExecutionStepResponse>> createdAllWorkflowExecutionSteps(Workflow workflow, WorkflowExecution workflowExecution);

    Flux<WorkflowExecutionStep> getAllStepsByExecutionId(Long workflowExecutionId);

    Mono<List<WorkflowExecutionStepResponse>> getAllStepResponsesByExecutionId(Long workflowExecutionId);

    Mono<Void> markNotStartedStepsAsCancelled(Long workflowExecutionId, Instant now);

    Mono<Void> markInProgressStepAsCancelled(Long workflowExecutionId, Instant now);

    Mono<Void> markNotStartedStepsAsIgnored(Long workflowExecutionId, Instant now);

    Mono<Void> markNotStartedStepsAsSkipped(Long workflowExecutionId, Instant now);
}
