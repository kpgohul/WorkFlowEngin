package com.friends.workflowservice.service;

import com.friends.workflowservice.dto.workflowrule.WorkflowRuleRequest;
import com.friends.workflowservice.dto.workflowrule.WorkflowRuleResponse;
import reactor.core.publisher.Mono;

public interface WorkflowRuleService {
    Mono<WorkflowRuleResponse> createWorkflowRules(Long stepId, Mono<WorkflowRuleRequest> requestMono);
    Mono<WorkflowRuleResponse> updateWorkflowRules(Long stepId, Mono<WorkflowRuleRequest> requestMono);
    Mono<WorkflowRuleResponse> getWorkflowRuleById(Long ruleId);
    Mono<Void> deleteWorkflowRuleById(Long ruleId);

}
