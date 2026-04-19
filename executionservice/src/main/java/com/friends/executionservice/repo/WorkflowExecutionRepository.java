package com.friends.executionservice.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import com.friends.executionservice.entity.WorkflowExecution;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkflowExecutionRepository extends R2dbcRepository<WorkflowExecution, Long> {
    Flux<WorkflowExecution> findAllByWorkflowId(Long workflowId);

    @Query("SELECT * FROM workflow_executions WHERE workflow_id = :workflowId LIMIT :size OFFSET :offset")
    Flux<WorkflowExecution> findAllByWorkflowId(@Param("workflowId") Long workflowId, @Param("size") int size,
            @Param("offset") long offset);

    Mono<Long> countByWorkflowId(Long workflowId);

    @Query("SELECT * FROM workflow_executions LIMIT :size OFFSET :offset")
    Flux<WorkflowExecution> findAllPaged(@Param("size") int size, @Param("offset") long offset);

    Mono<Long> count();

    @Query("SELECT * FROM workflow_executions WHERE initiated_by = :initiatedBy LIMIT :size OFFSET :offset")
    Flux<WorkflowExecution> findAllByInitiatedBy(@Param("initiatedBy") Long initiatedBy, @Param("size") int size,
            @Param("offset") long offset);

    Mono<Long> countByInitiatedBy(Long initiatedBy);
}
