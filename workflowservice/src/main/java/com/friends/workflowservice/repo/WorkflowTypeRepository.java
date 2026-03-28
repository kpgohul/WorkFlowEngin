package com.friends.workflowservice.repo;

import com.friends.workflowservice.entity.WorkflowType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WorkflowTypeRepository extends R2dbcRepository<WorkflowType, Long> {

    @Query("""
                SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
                FROM workflow_type
                WHERE code = :code OR name = :name
            """)
    Mono<Boolean> existsByCodeOrName(String code, String name);

    @Query("""
                SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
                FROM workflow_type
                WHERE (code = :code OR name = :name)
                AND id NOT IN (:ids)
            """)
    Mono<Boolean> existsByCodeOrNameExcludingIds(String code, String name, List<Long> ids);

    @Query("""
                SELECT * 
                FROM workflow_type
                ORDER BY id DESC
                LIMIT :size OFFSET :offset
            """)
    Flux<WorkflowType> findAllPaged(int size, long offset);

    @Query("SELECT COUNT(*) FROM workflow_type")
    Mono<Long> countAll();
}
