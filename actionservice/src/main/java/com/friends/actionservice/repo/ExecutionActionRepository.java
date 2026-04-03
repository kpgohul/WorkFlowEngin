package com.friends.actionservice.repo;

import com.friends.actionservice.entity.ExecutionAction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ExecutionActionRepository extends R2dbcRepository<ExecutionAction, Long> {
    Mono<ExecutionAction> findByExecutionIdAndExecutionStepId(Long executionId, Long executionStepId);
}

