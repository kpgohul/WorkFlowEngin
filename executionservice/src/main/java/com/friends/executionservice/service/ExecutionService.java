package com.friends.executionservice.service;

import com.friends.executionservice.client.WorkflowClient;
import com.friends.executionservice.entity.Execution;
import com.friends.executionservice.repo.ExecutionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExecutionService {

    private final ExecutionRepo executionRepository;

    private final WorkflowClient workflowClient;

    public Mono<Execution> startExecution(UUID workflowId,
                                          Map<String, Object> inputData) {

        Execution execution = Execution.builder()
                .id(UUID.randomUUID())
                .workflowId(workflowId)
                .status("IN_PROGRESS")
                .data(inputData.toString())
                .startedAt(Instant.now())
                .build();

        return executionRepository.save(execution)
                .flatMap(exec ->
                        workflowClient.getWorkflow(workflowId)
                                .flatMap(workflow -> {
                                    exec.setCurrentStepId(workflow.getStartStepId());
                                    return executionRepository.save(exec);
                                })
                );

    }

}