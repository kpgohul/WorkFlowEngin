package com.friends.workflowservice.repo;


import com.friends.workflowservice.entity.Rule;
import com.friends.workflowservice.entity.WorkFlow;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface RuleRepo extends ReactiveCrudRepository<Rule, UUID> {

    Flux<Rule> findByStepId(UUID stepId);
}