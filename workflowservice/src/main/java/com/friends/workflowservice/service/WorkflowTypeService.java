package com.friends.workflowservice.service;

import com.friends.workflowservice.dto.common.PagedResponse;
import com.friends.workflowservice.dto.workflowtype.CreateWorkflowTypeRequest;
import com.friends.workflowservice.dto.workflowtype.UpdateWorkflowTypeRequest;
import com.friends.workflowservice.dto.workflowtype.WorkflowTypeResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkflowTypeService {

    Mono<WorkflowTypeResponse> createWorkflowType(Mono<CreateWorkflowTypeRequest> createWorkflowTypeReqMono);
    Mono<WorkflowTypeResponse> updateWorkflowType(Mono<UpdateWorkflowTypeRequest> updateWorkflowTypeReqMono);
    Mono<WorkflowTypeResponse> getWorkflowTypeById(Long id);
    Mono<PagedResponse<WorkflowTypeResponse>> getAllWorkflowTypes(int page, int size);
    Mono<Void> deleteWorkflowTypeById(Long id);

}
