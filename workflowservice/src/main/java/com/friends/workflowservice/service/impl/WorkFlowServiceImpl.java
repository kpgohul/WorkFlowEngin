package com.friends.workflowservice.service.impl;

import com.friends.workflowservice.entity.WorkFlow;
import com.friends.workflowservice.repo.WorkFlowRepo;
import com.friends.workflowservice.service.WorkFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkFlowServiceImpl implements WorkFlowService {

    private final WorkFlowRepo repository;

    public Mono<WorkFlow> createWorkFlow(WorkFlow WorkFlow) {

        WorkFlow.setId(UUID.randomUUID());
        WorkFlow.setCreatedAt(Instant.now());
        WorkFlow.setUpdatedAt(Instant.now());

        return repository.save(WorkFlow);
    }

    public Flux<WorkFlow> getAllWorkFlows() {
        return repository.findAll();
    }

    public Mono<WorkFlow> getWorkFlow(UUID id) {
        return repository.findById(id);
    }

    public Mono<WorkFlow> updateWorkFlow(UUID id, WorkFlow WorkFlow) {

        return repository.findById(id)
                .flatMap(existing -> {

                    existing.setName(WorkFlow.getName());
                    existing.setIsActive(WorkFlow.getIsActive());
                    existing.setInputSchema(WorkFlow.getInputSchema());
                    existing.setUpdatedAt(Instant.now());

                    return repository.save(existing);
                });
    }

    public Mono<Void> deleteWorkFlow(UUID id) {
        return repository.deleteById(id);
    }
}