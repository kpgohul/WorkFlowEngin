package com.friends.executionservice.service;

import com.friends.executionservice.dto.RuleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StepProcessorService {

    private final RuleEngineService ruleEngine;

    public UUID determineNextStep(
            List<RuleDTO> rules,
            Map<String, Object> input) {

        return rules.stream()
                .sorted(Comparator.comparing(RuleDTO::getPriority))
                .filter(rule -> ruleEngine.evaluate(rule.getCondition(), input))
                .findFirst()
                .map(RuleDTO::getNextStepId)
                .orElse(null);

    }

}