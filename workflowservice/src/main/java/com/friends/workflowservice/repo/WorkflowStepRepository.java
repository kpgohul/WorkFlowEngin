package com.friends.workflowservice.repo;

import com.friends.workflowservice.entity.WorkflowStep;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkflowStepRepository extends R2dbcRepository<WorkflowStep, Long> {

    Flux<WorkflowStep> findAllByWorkflowId(Long workflowId);
    Mono<Void> deleteAllByWorkflowId(Long workflowId);
    Mono<WorkflowStep> findByWorkflowIdAndStepCode(Long workflowId, String stepCode);
}
