package com.friends.workflowservice.service;

import com.friends.workflowservice.entity.Rule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RuleService {

    public Mono<Rule> createRule(Rule rule);
    public Flux<Rule> getRules(UUID stepId);
    public Mono<Rule> updateRule(UUID id, Rule rule);
    public Mono<Void> deleteRule(UUID id);

}
