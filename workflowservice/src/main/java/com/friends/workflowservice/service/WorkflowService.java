package com.friends.workflowservice.service;

import com.friends.workflowservice.dto.common.PagedResponse;
import com.friends.workflowservice.dto.workflow.CreateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.UpdateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.WorkflowResponse;
import reactor.core.publisher.Mono;

public interface WorkflowService {

    Mono<WorkflowResponse> createWorkflow(Mono<CreateWorkflowRequest> requestMono);
    Mono<WorkflowResponse> updateWorkflow(Mono<UpdateWorkflowRequest> requestMono);
    Mono<WorkflowResponse> getWorkflowById(Long id);
    Mono<PagedResponse<WorkflowResponse>> getAllWorkflows(int page, int size);
    Mono<Void> deleteWorkflowById(Long id);
}

