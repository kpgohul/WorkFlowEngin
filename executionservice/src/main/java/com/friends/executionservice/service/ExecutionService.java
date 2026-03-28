package com.friends.executionservice.service;

import com.friends.executionservice.client.WorkflowClient;
import com.friends.executionservice.entity.Execution;
import com.friends.executionservice.repo.ExecutionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionService {

    private final ExecutionRepo executionRepository;
    private final WorkflowClient workflowClient;
    private final R2dbcEntityTemplate entityTemplate;

    public Mono<Execution> startExecution(UUID workflowId,
                                          Map<String, Object> inputData) {

        Execution execution = Execution.builder()
                .id(UUID.randomUUID())
                .workflowId(workflowId)
                .status("IN_PROGRESS")
                .data(inputData.toString())
                .startedAt(Instant.now())
                .build();

        return entityTemplate.insert(Execution.class).using(execution)
                .doOnNext(saved -> log.info("Execution inserted: {}", saved.getId()))
                .flatMap(exec ->
                        workflowClient.getWorkflow(workflowId)
                                .flatMap(workflow -> {
                                    exec.setCurrentStepId(workflow.getStartStepId());
                                    return executionRepository.save(exec); // update existing row — correct
                                })
                )
                .doOnError(err -> log.error("Error starting execution for workflow {}", workflowId, err));
    }

}