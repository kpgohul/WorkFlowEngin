package com.friends.workflowservice.service.impl;

import com.friends.workflowservice.entity.Step;
import com.friends.workflowservice.repo.StepRepo;
import com.friends.workflowservice.service.StepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StepServiceImpl implements StepService {
    private final StepRepo stepRepository;

    @Override
    public Mono<Step> createStep(Step step) {
        step.setId(UUID.randomUUID());
        step.setCreatedAt(Instant.now());

        return stepRepository.save(step);
    }

    @Override
    public Flux<Step> getSteps(UUID workflowId) {
        return stepRepository.findByWorkflowId(workflowId);
    }

    @Override
    public Mono<Step> updateStep(UUID id, Step step) {
        return stepRepository.findById(id)
                .flatMap(existing -> {

                    existing.setName(step.getName());
                    existing.setStepType(step.getStepType());
                    existing.setStepOrder(step.getStepOrder());
                    existing.setMetadata(step.getMetadata());

                    return stepRepository.save(existing);

                });
    }

    @Override
    public Mono<Void> deleteStep(UUID id) {
        return stepRepository.deleteById(id);
    }
}
