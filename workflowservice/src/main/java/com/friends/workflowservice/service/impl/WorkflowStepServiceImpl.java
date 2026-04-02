package com.friends.workflowservice.service.impl;

import com.friends.workflowservice.dto.common.WorkflowStepRuleRequest;
import com.friends.workflowservice.dto.common.WorkflowStepRuleResponse;
import com.friends.workflowservice.dto.workflowstep.WorkflowStepRequest;
import com.friends.workflowservice.entity.WorkflowStep;
import com.friends.workflowservice.exception.ResourceNotFoundException;
import com.friends.workflowservice.mapper.RuleMapper;
import com.friends.workflowservice.mapper.StepMapper;
import com.friends.workflowservice.repo.WorkflowRuleRepository;
import com.friends.workflowservice.repo.WorkflowStepRepository;
import com.friends.workflowservice.service.WorkflowRuleService;
import com.friends.workflowservice.service.WorkflowStepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkflowStepServiceImpl implements WorkflowStepService {

    private final WorkflowStepRepository stepRepository;
    private final WorkflowRuleRepository ruleRepository;
    private final WorkflowRuleService ruleService;

    @Override
    public Flux<WorkflowStepRuleResponse> createStepRules(Flux<WorkflowStepRuleRequest> requestFlux) {
        return requestFlux.collectList()
                .doOnNext(this::validateStepRules)
                .flatMapMany(requests -> Flux.fromIterable(requests)
                        .concatMap(request -> {
                            WorkflowStepRequest stepReq = StepMapper.normalizeStepRequest(request.getStep());
                            WorkflowStep stepEntity = StepMapper.toEntity(stepReq.getWorkflowId(), stepReq);

                            return stepRepository.save(stepEntity)
                                    .flatMap(savedStep -> ruleService
                                            .createWorkflowRules(savedStep.getId(), Mono.just(request.getRule()))
                                            .map(ruleResponse -> StepMapper.toStepRuleResponse(savedStep,
                                                    ruleResponse)));
                        }));
    }

    @Override
    public Flux<WorkflowStepRuleResponse> updateStepRules(Flux<WorkflowStepRuleRequest> requestFlux) {
        return requestFlux.collectList()
                .flatMapMany(requests -> {
                    if (requests.isEmpty()) {
                        return Flux.error(new IllegalArgumentException("At least one step is required"));
                    }

                    Long workflowId = requests.get(0).getStep().getWorkflowId();
                    if (workflowId == null) {
                        return Flux.error(new IllegalArgumentException("workflowId is required for update"));
                    }

                    return replaceStepRulesByWorkflowId(workflowId, Flux.fromIterable(requests));
                });
    }

    @Override
    public Flux<WorkflowStepRuleResponse> replaceStepRulesByWorkflowId(Long workflowId,
            Flux<WorkflowStepRuleRequest> requestFlux) {
        return requestFlux.collectList()
                .doOnNext(this::validateStepRules)
                .flatMapMany(requests -> stepRepository.findAllByWorkflowId(workflowId)
                        .flatMap(existing -> deleteStepRulesById(existing.getId()))
                        .thenMany(createStepRules(Flux.fromIterable(requests))));
    }

    @Override
    public Flux<WorkflowStepRuleResponse> getStepRulesById(Long stepId) {
        return stepRepository.findById(stepId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowStep", "id", stepId.toString())))
                .flatMapMany(step -> ruleRepository.findByStepId(stepId)
                        .switchIfEmpty(
                                Mono.error(new ResourceNotFoundException("WorkflowRule", "stepId", stepId.toString())))
                        .map(rule -> StepMapper.toStepRuleResponse(step, RuleMapper.toResponse(rule)))
                        .flux());
    }

    @Override
    public Mono<Void> deleteStepRulesById(Long stepId) {
        return stepRepository.findById(stepId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowStep", "id", stepId.toString())))
                .flatMap(step -> ruleRepository.findByStepId(stepId)
                        .flatMap(rule -> ruleService.deleteWorkflowRuleById(rule.getId()))
                        .onErrorResume(ResourceNotFoundException.class, e -> Mono.empty())
                        .then(stepRepository.deleteById(stepId)));
    }

    private void validateStepRules(List<WorkflowStepRuleRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("At least one step is required");
        }

        Set<String> stepCodes = new HashSet<>();
        Map<Integer, Integer> stepLineCounts = new HashMap<>();

        for (WorkflowStepRuleRequest request : requests) {
            if (request == null || request.getStep() == null || request.getRule() == null) {
                throw new IllegalArgumentException("Each item must include step and rule");
            }

            WorkflowStepRequest step = StepMapper.normalizeStepRequest(request.getStep());
            String stepCode = step.getStepCode();

            if (stepCode.contains(" ")) {
                throw new IllegalArgumentException("stepCode should not contain spaces: " + stepCode);
            }

            if (!stepCodes.add(stepCode)) {
                throw new IllegalArgumentException("Duplicate stepCode in request: " + stepCode);
            }

            Integer line = step.getStepLine();
            if (line == null || line < 1) {
                throw new IllegalArgumentException("stepLine should be >= 1");
            }
            stepLineCounts.merge(line, 1, Integer::sum);
        }

        if (stepLineCounts.getOrDefault(1, 0) != 1) {
            throw new IllegalArgumentException("Exactly one step must have stepLine as 1");
        }

        for (Map.Entry<Integer, Integer> entry : stepLineCounts.entrySet()) {
            Integer line = entry.getKey();
            Integer count = entry.getValue();
            if (line != 1 && count > 2) {
                throw new IllegalArgumentException("stepLine " + line + " can have at most 2 steps");
            }
        }

//        for (WorkflowStepRuleRequest request : requests) {
//            if (request.getRule().getRuleType() == RuleType.DECISION) {
//                Integer currentLine = request.getStep().getStepLine();
//                int nextLineCount = stepLineCounts.getOrDefault(currentLine + 1, 0);
//                if (nextLineCount != 2) {
//                    throw new IllegalArgumentException(
//                            "Decision step at line " + currentLine + " requires exactly 2 steps at line "
//                                    + (currentLine + 1));
//                }
//            }
//        }
// Rule's failure or success can point to the decendent steps, not necessarily the immediate next line. So relaxing this validation for now.

    }
}
