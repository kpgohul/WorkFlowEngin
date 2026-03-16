package com.friends.workflowservice.controller;

import com.friends.workflowservice.entity.Rule;
import com.friends.workflowservice.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/steps/{stepId}/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @PostMapping
    public Mono<Rule> createRule(@PathVariable UUID stepId,
                                 @RequestBody Rule rule) {
        rule.setStepId(stepId);
        return ruleService.createRule(rule);
    }

    @GetMapping
    public Flux<Rule> getRules(@PathVariable UUID stepId) {
        return ruleService.getRules(stepId);
    }

}

