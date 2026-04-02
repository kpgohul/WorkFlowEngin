package com.friends.executionservice.repo;

import com.friends.executionservice.entity.WorkflowExecutionStep;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkflowExecutionStepRepository extends R2dbcRepository<WorkflowExecutionStep, Long> {
    Flux<WorkflowExecutionStep> findAllByWorkflowExecutionIdOrderById(Long workflowExecutionId);

    Mono<WorkflowExecutionStep> findByWorkflowExecutionIdAndStepId(Long workflowExecutionId, Long stepId);
}
