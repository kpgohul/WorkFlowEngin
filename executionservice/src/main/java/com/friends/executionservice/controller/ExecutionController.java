package com.friends.executionservice.controller;

import com.friends.executionservice.entity.Execution;
import com.friends.executionservice.service.ExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class ExecutionController {

    private final ExecutionService executionService;

    @PostMapping("/{workflowId}/execute")
    public Mono<Execution> executeWorkflow(
            @PathVariable UUID workflowId,
            @RequestBody Map<String, Object> data) {

        return executionService.startExecution(workflowId, data);
    }

}