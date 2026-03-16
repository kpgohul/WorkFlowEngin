package com.friends.workflowservice.service;

import com.friends.workflowservice.entity.WorkFlow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface WorkFlowService {

    public Mono<WorkFlow> createWorkFlow(WorkFlow WorkFlow);
    public Flux<WorkFlow> getAllWorkFlows();
    public Mono<WorkFlow> getWorkFlow(UUID id);
    public Mono<WorkFlow> updateWorkFlow(UUID id, WorkFlow WorkFlow) ;
    public Mono<Void> deleteWorkFlow(UUID id);

}
