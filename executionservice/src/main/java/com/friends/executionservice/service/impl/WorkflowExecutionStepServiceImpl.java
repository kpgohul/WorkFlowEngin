package com.friends.executionservice.service.impl;

import com.friends.executionservice.appconstant.ExecutionStepStatus;
import com.friends.executionservice.clientdto.workflowclientdto.common.WorkflowStepRule;
import com.friends.executionservice.clientdto.workflowclientdto.workflow.Workflow;
import com.friends.executionservice.dto.workflowexecution.WorkflowExecutionStepResponse;
import com.friends.executionservice.entity.WorkflowExecution;
import com.friends.executionservice.entity.WorkflowExecutionStep;
import com.friends.executionservice.mapper.WorkflowExecutionStepMapper;
import com.friends.executionservice.repo.WorkflowExecutionStepRepository;
import com.friends.executionservice.service.WorkflowExecutionStepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowExecutionStepServiceImpl implements WorkflowExecutionStepService {

    private final WorkflowExecutionStepRepository stepRepository;

    @Override
    public Mono<List<WorkflowExecutionStepResponse>> createdAllWorkflowExecutionSteps(Workflow workflow, WorkflowExecution workflowExecution) {
        List<WorkflowStepRule> stepRules = workflow.getStepRule();
        stepRules.sort(Comparator.comparingInt(sr -> sr.getStep().getStepLine()));
        return Flux.fromIterable(stepRules)
                .flatMap(stepRule -> {
                    WorkflowExecutionStep step = WorkflowExecutionStepMapper.toEntity(workflowExecution.getId(), stepRule);
                    step.setStatus(ExecutionStepStatus.NOT_STARTED);
                    return stepRepository.save(step);

                })
                .map(WorkflowExecutionStepMapper::toResponse)
                .collectList();
    }

    @Override
    public Flux<WorkflowExecutionStep> getAllStepsByExecutionId(Long workflowExecutionId) {
        return stepRepository.findAllByWorkflowExecutionIdOrderById(workflowExecutionId);
    }

    @Override
    public Mono<List<WorkflowExecutionStepResponse>> getAllStepResponsesByExecutionId(Long workflowExecutionId) {
        return getAllStepsByExecutionId(workflowExecutionId)
                .map(WorkflowExecutionStepMapper::toResponse)
                .collectList();
    }

    @Override
    public Mono<Void> markNotStartedStepsAsCancelled(Long workflowExecutionId, Instant now) {
        return bulkUpdateStatusForNotStarted(workflowExecutionId, ExecutionStepStatus.CANCELLED, now);
    }

    @Override
    public Mono<Void> markNotStartedStepsAsIgnored(Long workflowExecutionId, Instant now) {
        return bulkUpdateStatusForNotStarted(workflowExecutionId, ExecutionStepStatus.IGNORED, now);
    }

    @Override
    public Mono<Void> markNotStartedStepsAsSkipped(Long workflowExecutionId, Instant now) {
        return bulkUpdateStatusForNotStarted(workflowExecutionId, ExecutionStepStatus.SKIPPED, now);
    }

    @Override
    public Mono<Void> markInProgressStepAsCancelled(Long workflowExecutionId, Instant now) {
        return stepRepository.findAllByWorkflowExecutionIdOrderById(workflowExecutionId)
                .filter(step -> step.getStatus() == ExecutionStepStatus.IN_PROGRESS)
                .next()
                .flatMap(step -> {
                    step.setStatus(ExecutionStepStatus.CANCELLED);
                    step.setTerminatedAt(now);
                    return stepRepository.save(step);
                })
                .then();
    }

    private Mono<Void> bulkUpdateStatusForNotStarted(Long workflowExecutionId, ExecutionStepStatus targetStatus, Instant now) {
        return stepRepository.findAllByWorkflowExecutionIdOrderById(workflowExecutionId)
                .filter(step -> step.getStatus() == ExecutionStepStatus.NOT_STARTED)
                .flatMap(step -> {
                    step.setStatus(targetStatus);
                    step.setTerminatedAt(now);
                    return stepRepository.save(step);
                })
                .then();
    }
}
