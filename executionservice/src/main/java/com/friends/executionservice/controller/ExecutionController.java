package com.friends.executionservice.controller;

import com.friends.executionservice.dto.common.PagedResponse;
import com.friends.executionservice.dto.workflowexecution.CreateWorkflowExecutionRequest;
import com.friends.executionservice.dto.workflowexecution.WorkflowExecutionResponse;
import com.friends.executionservice.service.WorkflowExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/executions")
@RequiredArgsConstructor
public class ExecutionController {

    private final WorkflowExecutionService workflowExecutionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<WorkflowExecutionResponse> createWorkflowExecution(
            @Valid @RequestBody Mono<CreateWorkflowExecutionRequest> requestMono
    ) {
        return workflowExecutionService.createWorkflowExecution(requestMono);
    }

    @GetMapping("/{id}")
    public Mono<WorkflowExecutionResponse> getWorkflowExecutionById(@PathVariable Long id) {
        return workflowExecutionService.getWorkflowExecutionById(id);
    }

    @GetMapping
    public Mono<PagedResponse<WorkflowExecutionResponse>> getAllWorkflowExecutions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        validatePaging(page, size);
        return workflowExecutionService.getAllWorkflowExecutions(page - 1, size);
    }

    @GetMapping(params = "workflowId")
    public Mono<PagedResponse<WorkflowExecutionResponse>> getAllWorkflowExecutionsByWorkflowId(
            @RequestParam Long workflowId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        validatePaging(page, size);
        return workflowExecutionService.getAllWorkflowExecutionsByWorkflowId(workflowId, page - 1, size);
    }

    @GetMapping(params = "initiatedBy")
    public Mono<PagedResponse<WorkflowExecutionResponse>> getAllWorkflowExecutionsByInitiatedBy(
            @RequestParam Long initiatedBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        validatePaging(page, size);
        return workflowExecutionService.getAllWorkflowExecutionsByInitiatedBy(initiatedBy, page - 1, size);
    }

    @PostMapping("/{id}/cancel")
    public Mono<WorkflowExecutionResponse> cancelWorkflowExecution(@PathVariable Long id) {
        return workflowExecutionService.cancelWorkflowExecution(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteWorkflowExecution(@PathVariable Long id) {
        return workflowExecutionService.deleteWorkflowExecution(id);
    }

    private static void validatePaging(int page, int size) {
        if (page <= 0) {
            throw new IllegalArgumentException("page must be >= 1");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }
    }
}
