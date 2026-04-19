package com.friends.workflowservice.repo;

import com.friends.workflowservice.entity.Workflow;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkflowRepository extends R2dbcRepository<Workflow, Long> {

    Mono<Boolean> existsByName(String name);
    Mono<Boolean> existsByWorkflowTypeId(Long workflowTypeId);

    @Query("""
            SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
            FROM workflow
            WHERE name = :name AND id <> :id
            """)
    Mono<Boolean> existsByNameAndIdNot(String name, Long id);

    @Query("""
            SELECT *
            FROM workflow
            ORDER BY id DESC
            LIMIT :size OFFSET :offset
            """)
    Flux<Workflow> findAllPaged(int size, long offset);

    @Query("SELECT COUNT(*) FROM workflow")
    Mono<Long> countAll();
}
