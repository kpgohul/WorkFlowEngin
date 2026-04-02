package com.friends.executionservice.service;

import com.friends.executionservice.dto.common.PagedResponse;
import com.friends.executionservice.dto.workflowexecution.CreateWorkflowExecutionRequest;
import com.friends.executionservice.dto.workflowexecution.WorkflowExecutionResponse;
import reactor.core.publisher.Mono;

public interface WorkflowExecutionService {
    Mono<WorkflowExecutionResponse> createWorkflowExecution(Mono<CreateWorkflowExecutionRequest> request);
    Mono<WorkflowExecutionResponse> getWorkflowExecutionById(Long id);
    Mono<PagedResponse<WorkflowExecutionResponse>> getAllWorkflowExecutions(int page, int size);
    Mono<PagedResponse<WorkflowExecutionResponse>> getAllWorkflowExecutionsByWorkflowId(Long workflowId, int page, int size);
    Mono<PagedResponse<WorkflowExecutionResponse>> getAllWorkflowExecutionsByInitiatedBy(Long user, int page, int size);
    Mono<WorkflowExecutionResponse> cancelWorkflowExecution(Long id);
    Mono<Void> deleteWorkflowExecution(Long id);
    Mono<Void> applyStepResult(Long workflowExecutionId, Long executionStepId, boolean success, String message, String error);
}
