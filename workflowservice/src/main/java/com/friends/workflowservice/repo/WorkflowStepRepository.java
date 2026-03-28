package com.friends.workflowservice.repo;

import com.friends.workflowservice.entity.WorkflowStep;
import com.friends.workflowservice.entity.WorkflowType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface WorkflowStepRepository extends R2dbcRepository<WorkflowStep, Long> {
}
