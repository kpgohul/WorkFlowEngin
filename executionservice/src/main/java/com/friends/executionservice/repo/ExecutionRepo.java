package com.friends.executionservice.repo;

import com.friends.executionservice.entity.Execution;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import java.util.UUID;

public interface ExecutionRepo extends ReactiveCrudRepository<Execution, UUID> {
}
