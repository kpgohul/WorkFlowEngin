package com.friends.workflowservice.service;

import com.friends.workflowservice.dto.workflowtype.WorkflowTypeFieldRequest;
import com.friends.workflowservice.dto.workflowtype.WorkflowTypeFieldResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WorkflowTypeFieldService {

    Mono<List<WorkflowTypeFieldResponse>> createWorkflowTypeFields(Long workflowId, Mono<List<WorkflowTypeFieldRequest>> requests);
    Mono<List<WorkflowTypeFieldResponse>> updateWorkflowTypeFields(Long workflowId, Mono<List<WorkflowTypeFieldRequest>> request);
    Mono<List<WorkflowTypeFieldResponse>> getWorkflowTypeFieldById(Long id);
    Mono<Void> deleteWorkflowTypeFieldById(Long id);

}
