package com.friends.workflowservice.controller;

import com.friends.workflowservice.entity.Step;
import com.friends.workflowservice.service.StepService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/workflows/{workflowId}/steps")
@RequiredArgsConstructor
public class StepController {

    private final StepService stepService;

    @PostMapping
    public Mono<Step> createStep(@PathVariable UUID workflowId,
                                 @RequestBody Step step) {
        step.setWorkflowId(workflowId);
        return stepService.createStep(step);
    }

    @GetMapping
    public Flux<Step> getSteps(@PathVariable UUID workflowId) {
        return stepService.getSteps(workflowId);
    }

}
