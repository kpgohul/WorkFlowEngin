package com.friends.workflowservice.repo;

import com.friends.workflowservice.entity.WorkflowRule;
import com.friends.workflowservice.entity.WorkflowType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface WorkflowRuleRepository extends R2dbcRepository<WorkflowRule, Long> {
}
