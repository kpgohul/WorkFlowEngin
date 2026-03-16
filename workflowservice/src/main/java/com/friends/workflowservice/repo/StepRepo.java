package com.friends.workflowservice.repo;


import com.friends.workflowservice.entity.Step;
import com.friends.workflowservice.entity.WorkFlow;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface StepRepo extends ReactiveCrudRepository<Step, UUID> {

    Flux<Step> findByWorkflowId(UUID workflowId);

}