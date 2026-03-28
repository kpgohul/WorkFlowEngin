package com.friends.workflowservice.controller;

import com.friends.workflowservice.dto.common.PagedResponse;
import com.friends.workflowservice.dto.workflowtype.CreateWorkflowTypeRequest;
import com.friends.workflowservice.dto.workflowtype.UpdateWorkflowTypeRequest;
import com.friends.workflowservice.dto.workflowtype.WorkflowTypeResponse;
import com.friends.workflowservice.service.WorkflowTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/workflow-types")
@RequiredArgsConstructor
public class WorkflowTypeController {

    private final WorkflowTypeService workflowTypeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<WorkflowTypeResponse> createWorkflowType(
            @Valid @RequestBody Mono<CreateWorkflowTypeRequest> requestMono
    ) {
        return workflowTypeService.createWorkflowType(requestMono);
    }

    @PutMapping
    public Mono<WorkflowTypeResponse> updateWorkflowType(
            @Valid @RequestBody Mono<UpdateWorkflowTypeRequest> requestMono
    ) {
        return workflowTypeService.updateWorkflowType(requestMono);
    }

    @GetMapping("/{id}")
    public Mono<WorkflowTypeResponse> getWorkflowTypeById(@PathVariable Long id) {
        return workflowTypeService.getWorkflowTypeById(id);
    }

    @GetMapping
    public Mono<PagedResponse<WorkflowTypeResponse>> getAllWorkflowTypes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if(page <= 0 || size <= 0) {
            return Mono.error(new IllegalArgumentException("Page must be >= 0 and size must be > 0"));
        }
        return workflowTypeService.getAllWorkflowTypes(page - 1, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteWorkflowTypeById(@PathVariable Long id) {
        return workflowTypeService.deleteWorkflowTypeById(id);
    }
}