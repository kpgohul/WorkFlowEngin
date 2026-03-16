package com.friends.workflowservice.controller;

import com.friends.workflowservice.entity.WorkFlow;
import com.friends.workflowservice.service.WorkFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkFlowController {

    private final WorkFlowService workflowService;

    @PostMapping
    public Mono<WorkFlow> createWorkflow(@RequestBody WorkFlow workflow) {
        return workflowService.createWorkFlow(workflow);
    }

    @GetMapping
    public Flux<WorkFlow> getAllWorkflows() {
        return workflowService.getAllWorkFlows();
    }

    @GetMapping("/{id}")
    public Mono<WorkFlow> getWorkflow(@PathVariable UUID id) {
        return workflowService.getWorkFlow(id);
    }

    @PutMapping("/{id}")
    public Mono<WorkFlow> updateWorkflow(
            @PathVariable UUID id,
            @RequestBody WorkFlow workflow) {

        return workflowService.updateWorkFlow(id, workflow);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteWorkflow(@PathVariable UUID id) {
        return workflowService.deleteWorkFlow(id);
    }
}
