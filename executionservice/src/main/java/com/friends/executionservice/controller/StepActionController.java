package com.friends.executionservice.controller;

import com.friends.executionservice.appconstant.StepType;
import com.friends.executionservice.dto.action.StepActionRequest;
import com.friends.executionservice.service.ActionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/executions/{workflowExecutionId}/steps")
@RequiredArgsConstructor
public class StepActionController {

    private final ActionService actionService;

    @PostMapping("/task")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> task(
            @PathVariable Long workflowExecutionId,
            @Valid @RequestBody Mono<StepActionRequest> requestMono
    ) {
        return requestMono.flatMap(request -> actionService.submitStepResult(workflowExecutionId, StepType.TASK, request));
    }

    @PostMapping("/approval")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> approval(
            @PathVariable Long workflowExecutionId,
            @Valid @RequestBody Mono<StepActionRequest> requestMono
    ) {
        return requestMono.flatMap(request -> actionService.submitStepResult(workflowExecutionId, StepType.APPROVAL, request));
    }

    @PostMapping("/decision")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> decision(
            @PathVariable Long workflowExecutionId,
            @Valid @RequestBody Mono<StepActionRequest> requestMono
    ) {
        return requestMono.flatMap(request -> actionService.submitStepResult(workflowExecutionId, StepType.DECISION, request));
    }

    @PostMapping("/auto-approval")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> autoApproval(
            @PathVariable Long workflowExecutionId,
            @Valid @RequestBody Mono<StepActionRequest> requestMono
    ) {
        return requestMono.flatMap(request -> actionService.submitStepResult(workflowExecutionId, StepType.AUTO_APPROVAL, request));
    }

    @PostMapping("/notification")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> notification(
            @PathVariable Long workflowExecutionId,
            @Valid @RequestBody Mono<StepActionRequest> requestMono
    ) {
        return requestMono.flatMap(request -> actionService.submitStepResult(workflowExecutionId, StepType.NOTIFICATION, request));
    }

    @PostMapping("/delay")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> delay(
            @PathVariable Long workflowExecutionId,
            @Valid @RequestBody Mono<StepActionRequest> requestMono
    ) {
        return requestMono.flatMap(request -> actionService.submitStepResult(workflowExecutionId, StepType.DELAY, request));
    }

    @PostMapping("/webhook")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> webHook(
            @PathVariable Long workflowExecutionId,
            @Valid @RequestBody Mono<StepActionRequest> requestMono
    ) {
        return requestMono.flatMap(request -> actionService.submitStepResult(workflowExecutionId, StepType.WEBHOOK, request));
    }
}

