package com.friends.executionservice.repo;

import com.friends.executionservice.entity.ExecutionLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface ExecutionLogRepo extends ReactiveCrudRepository<ExecutionLog, UUID> {

    Flux<ExecutionLog> findByExecutionId(UUID executionId);

}
