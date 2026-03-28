package com.friends.workflowservice.repo;

import com.friends.workflowservice.entity.WorkflowType;
import com.friends.workflowservice.entity.WorkflowTypeField;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkflowTypeFieldRepository extends R2dbcRepository<WorkflowTypeField, Long> {

    Flux<WorkflowTypeField> findAllByWorkflowTypeId(Long workflowTypeId);
    Mono<Void> deleteAllByWorkflowTypeId(Long workflowTypeId);
    Mono<Void> deleteAllByWorkflowTypeId(WorkflowType workflowType);
}
