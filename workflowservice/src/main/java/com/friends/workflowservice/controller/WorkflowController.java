package com.friends.workflowservice.controller;

import com.friends.workflowservice.dto.common.PagedResponse;
import com.friends.workflowservice.dto.workflow.CreateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.UpdateWorkflowRequest;
import com.friends.workflowservice.dto.workflow.WorkflowResponse;
import com.friends.workflowservice.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<WorkflowResponse> createWorkflow(
            @Valid @RequestBody Mono<CreateWorkflowRequest> requestMono
    ) {
        return workflowService.createWorkflow(requestMono);
    }

    @PutMapping
    public Mono<WorkflowResponse> updateWorkflow(
            @Valid @RequestBody Mono<UpdateWorkflowRequest> requestMono
    ) {
        return workflowService.updateWorkflow(requestMono);
    }

    @GetMapping("/{id}")
    public Mono<WorkflowResponse> getWorkflowById(@PathVariable Long id) {
        return workflowService.getWorkflowById(id);
    }

    @GetMapping
    public Mono<PagedResponse<WorkflowResponse>> getAllWorkflows(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page <= 0 || size <= 0) {
            return Mono.error(new IllegalArgumentException("Page must be >= 1 and size must be > 0"));
        }
        return workflowService.getAllWorkflows(page - 1, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteWorkflowById(@PathVariable Long id) {
        return workflowService.deleteWorkflowById(id);
    }
}

