package com.friends.workflowservice.service;

import com.friends.workflowservice.dto.common.WorkflowStepRuleRequest;
import com.friends.workflowservice.dto.common.WorkflowStepRuleResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkflowStepService {

    Flux<WorkflowStepRuleResponse> createStepRules(Flux<WorkflowStepRuleRequest> requestFlux);
    Flux<WorkflowStepRuleResponse> updateStepRules(Flux<WorkflowStepRuleRequest> requestFlux);
    Flux<WorkflowStepRuleResponse> replaceStepRulesByWorkflowId(Long workflowId, Flux<WorkflowStepRuleRequest> requestFlux);
    Flux<WorkflowStepRuleResponse> getStepRulesById(Long stepId);
    Mono<Void> deleteStepRulesById(Long stepId);

}
