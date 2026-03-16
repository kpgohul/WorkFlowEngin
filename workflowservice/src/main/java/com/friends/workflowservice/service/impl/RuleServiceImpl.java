package com.friends.workflowservice.service.impl;

import com.friends.workflowservice.entity.Rule;
import com.friends.workflowservice.repo.RuleRepo;
import com.friends.workflowservice.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {
    private final RuleRepo ruleRepository;

    @Override
    public Mono<Rule> createRule(Rule rule) {
        rule.setId(UUID.randomUUID());
        rule.setCreatedAt(Instant.now());

        return ruleRepository.save(rule);
    }

    @Override
    public Flux<Rule> getRules(UUID stepId) {
        return ruleRepository.findByStepId(stepId);
    }

    @Override
    public Mono<Rule> updateRule(UUID id, Rule rule) {
        return ruleRepository.findById(id)
                .flatMap(existing -> {

                    existing.setCondition(rule.getCondition());
                    existing.setNextStepId(rule.getNextStepId());
                    existing.setPriority(rule.getPriority());

                    return ruleRepository.save(existing);

                });
    }

    @Override
    public Mono<Void> deleteRule(UUID id) {
        return ruleRepository.deleteById(id);
    }
}
