package com.friends.workflowservice.repo;

import com.friends.workflowservice.entity.WorkflowRule;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface WorkflowRuleRepository extends R2dbcRepository<WorkflowRule, Long> {

    Mono<WorkflowRule> findByStepId(Long stepId);
}
