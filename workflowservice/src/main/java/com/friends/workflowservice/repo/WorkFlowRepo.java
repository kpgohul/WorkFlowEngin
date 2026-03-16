package com.friends.workflowservice.repo;


import com.friends.workflowservice.entity.Rule;
import com.friends.workflowservice.entity.WorkFlow;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface WorkFlowRepo extends ReactiveCrudRepository<WorkFlow, UUID> {
}