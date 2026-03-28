package com.friends.workflowservice.repo;

import com.friends.workflowservice.entity.Workflow;
import com.friends.workflowservice.entity.WorkflowType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface WorkflowRepository extends R2dbcRepository<Workflow, Long> {
}
