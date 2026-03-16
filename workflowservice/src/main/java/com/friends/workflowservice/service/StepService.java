package com.friends.workflowservice.service;

import com.friends.workflowservice.entity.Step;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface StepService {

    public Mono<Step> createStep(Step step);
    public Flux<Step> getSteps(UUID workflowId);
    public Mono<Step> updateStep(UUID id, Step step);
    public Mono<Void> deleteStep(UUID id);

}