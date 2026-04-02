package com.friends.executionservice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.friends.executionservice.appconstant.ActionType;
import com.friends.executionservice.appconstant.ExecutionStatus;
import com.friends.executionservice.appconstant.ExecutionStepStatus;
import com.friends.executionservice.appconstant.StepType;
import com.friends.executionservice.appconstant.WorkflowFieldType;
import com.friends.executionservice.clientdto.actionclientdto.ActionRequest;
import com.friends.executionservice.clientdto.actionclientdto.actions.ApprovalAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.AutoApprovalAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.DelayAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.NotificationAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.TaskAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.WebHookAction;
import com.friends.executionservice.clientdto.workflowclientdto.common.WorkflowStepRule;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.ApprovalRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.AutoApprovalRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.DelayRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.DecisionRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.NotificationRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.RuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.TaskRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.ruleconfig.WebHookRuleConfig;
import com.friends.executionservice.clientdto.workflowclientdto.workflow.Workflow;
import com.friends.executionservice.clientdto.workflowclientdto.workflowrule.WorkflowRule;
import com.friends.executionservice.clientdto.workflowclientdto.workflowstep.WorkflowStep;
import com.friends.executionservice.clientdto.workflowclientdto.workflowtype.WorkflowTypeField;
import com.friends.executionservice.dto.common.PagedResponse;
import com.friends.executionservice.dto.workflowexecution.CreateWorkflowExecutionRequest;
import com.friends.executionservice.dto.workflowexecution.WorkflowExecutionResponse;
import com.friends.executionservice.dto.workflowexecution.WorkflowExecutionStepResponse;
import com.friends.executionservice.entity.WorkflowExecution;
import com.friends.executionservice.entity.WorkflowExecutionStep;
import com.friends.executionservice.exception.ResourceNotFoundException;
import com.friends.executionservice.mapper.WorkflowExecutionMapper;
import com.friends.executionservice.repo.WorkflowExecutionRepository;
import com.friends.executionservice.repo.WorkflowExecutionStepRepository;
import com.friends.executionservice.service.ActionService;
import com.friends.executionservice.service.WorkflowExecutionService;
import com.friends.executionservice.service.WorkflowExecutionStepService;
import com.friends.executionservice.util.common.JsonUtils;
import com.friends.executionservice.util.rule.ConditionEvaluator;
import com.friends.executionservice.webclient.WorkflowWebClient;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class WorkflowExecutionServiceImpl implements WorkflowExecutionService {

    private final WorkflowExecutionRepository executionRepository;
    private final WorkflowExecutionStepRepository stepRepository;
    private final WorkflowWebClient workflowServiceClient;
    private final ActionService actionService;
    private final WorkflowExecutionStepService stepService;

    @Override
    public Mono<WorkflowExecutionResponse> createWorkflowExecution(Mono<CreateWorkflowExecutionRequest> requestMono) {
        return requestMono.flatMap(request ->
            workflowServiceClient.getWorkflowById(request.getWorkflowId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", "id", request.getWorkflowId().toString())))
                .flatMap(workflow -> {
                    validateInputPayload(request.getInputPayload(), workflow.getWorkflowType().getFields());
                    WorkflowExecution execution = WorkflowExecutionMapper.toEntity(request, workflow.getWorkflowTypeId(), workflow.getId());
                    execution.setInitiatedBy(ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L));
                    execution.setStatus(ExecutionStatus.NOT_STARTED);

                    WorkflowStepRule firstStepRule = resolveFirstStepExecution(workflow);
                    Long firstStepId = firstStepRule.getStep().getId();

                    return executionRepository.save(execution)
                        .flatMap(savedExecution ->
                            stepService.createdAllWorkflowExecutionSteps(workflow, savedExecution)
                                .flatMap(stepList ->
                                    stepRepository.findByWorkflowExecutionIdAndStepId(savedExecution.getId(), firstStepId)
                                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowExecutionStep", "stepId", firstStepId.toString())))
                                        .flatMap(firstStepExecution -> {
                                            firstStepExecution.setStatus(ExecutionStepStatus.IN_PROGRESS);
                                            firstStepExecution.setOrderOfExecution(1);
                                            return stepRepository.save(firstStepExecution)
                                                .flatMap(updatedFirstStep -> {
                                                    List<WorkflowExecutionStepResponse> updatedStepList = stepList.stream()
                                                        .map(step -> {
                                                            if (firstStepId.equals(step.getStepId())) {
                                                                step.setStatus(ExecutionStepStatus.IN_PROGRESS);
                                                                step.setOrderOfExecution(1);
                                                            }
                                                            return step;
                                                        })
                                                        .toList();

                                                    savedExecution.setStatus(ExecutionStatus.IN_PROGRESS);
                                                    savedExecution.setCurrentStepId(firstStepId);

                                                    return executionRepository.save(savedExecution)
                                                        .map(updatedExecution -> WorkflowExecutionMapper.toResponse(updatedExecution, updatedStepList))
                                                        .doAfterTerminate(() -> triggerStepExecutionAsync(firstStepRule, savedExecution, updatedFirstStep.getId()));
                                                });
                                        })
                                )
                        );
                })
        );
    }

    @Override
    public Mono<WorkflowExecutionResponse> getWorkflowExecutionById(Long id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("WorkflowExecution id cannot be null"));
        }

        return executionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowExecution", "id", id.toString())))
                .flatMap(execution -> stepService.getAllStepResponsesByExecutionId(execution.getId())
                        .map(steps -> WorkflowExecutionMapper.toResponse(execution, steps)));
    }

    @Override
    public Mono<PagedResponse<WorkflowExecutionResponse>> getAllWorkflowExecutionsByWorkflowId(Long workflowId, int page, int size) {
        if (workflowId == null) {
            return Mono.error(new IllegalArgumentException("workflowId cannot be null"));
        }

        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        long offset = (long) normalizedPage * normalizedSize;

        Mono<Long> totalMono = executionRepository.countByWorkflowId(workflowId);
        Mono<List<WorkflowExecutionResponse>> contentMono = executionRepository.findAllByWorkflowId(workflowId, normalizedSize, offset)
                .flatMap(execution -> stepService.getAllStepResponsesByExecutionId(execution.getId())
                        .map(steps -> WorkflowExecutionMapper.toResponse(execution, steps)))
                .collectList();

        return Mono.zip(totalMono, contentMono)
                .map(tuple -> toPagedResponse(tuple.getT2(), tuple.getT1(), normalizedPage, normalizedSize));
    }

    @Override
    public Mono<PagedResponse<WorkflowExecutionResponse>> getAllWorkflowExecutions(int page, int size) {
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        long offset = (long) normalizedPage * normalizedSize;

        Mono<Long> totalMono = executionRepository.count();
        Mono<List<WorkflowExecutionResponse>> contentMono = executionRepository.findAllPaged(normalizedSize, offset)
                .flatMap(execution -> stepService.getAllStepResponsesByExecutionId(execution.getId())
                        .map(steps -> WorkflowExecutionMapper.toResponse(execution, steps)))
                .collectList();

        return Mono.zip(totalMono, contentMono)
                .map(tuple -> toPagedResponse(tuple.getT2(), tuple.getT1(), normalizedPage, normalizedSize));
    }

    @Override
    public Mono<PagedResponse<WorkflowExecutionResponse>> getAllWorkflowExecutionsByInitiatedBy(Long user, int page, int size) {
        if (user == null) {
            return Mono.error(new IllegalArgumentException("initiatedBy (user) cannot be null"));
        }

        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        long offset = (long) normalizedPage * normalizedSize;

        Mono<Long> totalMono = executionRepository.countByInitiatedBy(user);
        Mono<List<WorkflowExecutionResponse>> contentMono = executionRepository.findAllByInitiatedBy(user, normalizedSize, offset)
                .flatMap(execution -> stepService.getAllStepResponsesByExecutionId(execution.getId())
                        .map(steps -> WorkflowExecutionMapper.toResponse(execution, steps)))
                .collectList();

        return Mono.zip(totalMono, contentMono)
                .map(tuple -> toPagedResponse(tuple.getT2(), tuple.getT1(), normalizedPage, normalizedSize));
    }

    @Override
    public Mono<WorkflowExecutionResponse> cancelWorkflowExecution(Long id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("WorkflowExecution id cannot be null"));
        }

        return executionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowExecution", "id", id.toString())))
                .flatMap(execution -> {
                    ExecutionStatus status = execution.getStatus();
                    if (status == ExecutionStatus.CANCELLED) {
                        return stepService.getAllStepResponsesByExecutionId(execution.getId())
                                .map(steps -> WorkflowExecutionMapper.toResponse(execution, steps));
                    }
                    if (status == ExecutionStatus.SUCCESS || status == ExecutionStatus.FAILED) {
                        return Mono.error(new IllegalStateException("Cannot cancel a terminal workflow execution. status=" + status));
                    }

                    Instant now = Instant.now();
                    execution.setStatus(ExecutionStatus.CANCELLED);
                    execution.setError(null);
                    execution.setTerminatedAt(now);

                    Mono<Void> cancelSteps = Mono.when(
                            stepService.markNotStartedStepsAsCancelled(execution.getId(), now),
                            stepService.markInProgressStepAsCancelled(execution.getId(), now)
                    );

                    return cancelSteps
                            .then(executionRepository.save(execution))
                            .then(stepService.getAllStepResponsesByExecutionId(execution.getId()))
                            .map(steps -> WorkflowExecutionMapper.toResponse(execution, steps));
                });
    }

    private int normalizePage(int page) {
        return Math.max(0, page);
    }

    private int normalizeSize(int size) {
        int s = size <= 0 ? 10 : size;
        return Math.min(s, 100);
    }

    private PagedResponse<WorkflowExecutionResponse> toPagedResponse(
            List<WorkflowExecutionResponse> content,
            long totalElements,
            int page,
            int size
    ) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / (double) size);
        return PagedResponse.<WorkflowExecutionResponse>builder()
                .content(content)
                .totalElements(totalElements)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }

    @Override
    public Mono<Void> deleteWorkflowExecution(Long id) {
        return Mono.error(new UnsupportedOperationException("deleteWorkflowExecution is not implemented yet"));
    }

    @Override
    public Mono<Void> applyStepResult(Long workflowExecutionId, Long executionStepId, boolean success, String message, String error) {
        return Mono.fromRunnable(() -> {
            WorkflowExecution execution = executionRepository.findById(workflowExecutionId)
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowExecution", "id", workflowExecutionId.toString())))
                    .block();

            if (execution == null) {
                throw new ResourceNotFoundException("WorkflowExecution", "id", workflowExecutionId.toString());
            }

            // Ignore late Kafka results once workflow is terminal.
            if (execution.getStatus() == ExecutionStatus.FAILED || execution.getStatus() == ExecutionStatus.CANCELLED) {
                return;
            }

            List<WorkflowExecutionStep> executionSteps = stepRepository
                    .findAllByWorkflowExecutionIdOrderById(workflowExecutionId)
                    .collectList()
                    .block();

            if (executionSteps == null || executionSteps.isEmpty()) {
                throw new IllegalStateException("No execution steps found for executionId=" + workflowExecutionId);
            }

            WorkflowExecutionStep currentStep = executionSteps.stream()
                    .filter(step -> executionStepId.equals(step.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("WorkflowExecutionStep", "id", executionStepId.toString()));

            Instant now = Instant.now();
            currentStep.setMessage(message);
            currentStep.setError(error);
            currentStep.setTerminatedAt(now);

            if (!success) {
                currentStep.setStatus(ExecutionStepStatus.FAILED);
                markRemainingNotStartedAsIgnored(executionSteps, currentStep.getId(), now);
                execution.setStatus(ExecutionStatus.FAILED);
                execution.setError(error);
                execution.setTerminatedAt(now);

                Flux.fromIterable(executionSteps)
                        .flatMap(stepRepository::save)
                        .then()
                        .block();
                executionRepository.save(execution).block();
                return;
            }

            currentStep.setStatus(ExecutionStepStatus.COMPLETED);

            Workflow workflow = workflowServiceClient.getWorkflowById(execution.getWorkflowId())
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", "id", execution.getWorkflowId().toString())))
                    .block();

            if (workflow == null) {
                throw new IllegalStateException("Workflow not found for execution=" + workflowExecutionId);
            }

            WorkflowStepRule currentStepRule = workflow.getStepRule().stream()
                    .filter(sr -> currentStep.getStepId().equals(sr.getStep().getId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("WorkflowStep", "id", currentStep.getStepId().toString()));

            boolean isLastStep = Boolean.TRUE.equals(currentStepRule.getStep().getIsLast());
            if (isLastStep) {
                markRemainingNotStartedAsSkipped(executionSteps, currentStep.getId(), now);
                execution.setStatus(ExecutionStatus.SUCCESS);
                execution.setError(null);
                execution.setTerminatedAt(now);

                Flux.fromIterable(executionSteps)
                        .flatMap(stepRepository::save)
                        .then()
                        .block();
                executionRepository.save(execution).block();
                return;
            }

            Long nextStepId = resolveNextStepId(workflow, currentStepRule.getStep().getStepLine());
            WorkflowExecutionStep nextExecutionStep = executionSteps.stream()
                    .filter(step -> nextStepId.equals(step.getStepId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("WorkflowExecutionStep", "stepId", nextStepId.toString()));

            WorkflowStepRule nextStepRule = workflow.getStepRule().stream()
                    .filter(sr -> nextStepId.equals(sr.getStep().getId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("WorkflowStep", "id", nextStepId.toString()));

            int nextOrder = currentStep.getOrderOfExecution() == null ? 1 : currentStep.getOrderOfExecution() + 1;
            nextExecutionStep.setStatus(ExecutionStepStatus.IN_PROGRESS);
            nextExecutionStep.setOrderOfExecution(nextOrder);
            nextExecutionStep.setInitiatedAt(now);

            execution.setCurrentStepId(nextStepId);
            execution.setStatus(ExecutionStatus.IN_PROGRESS);
            execution.setError(null);

            Flux.fromIterable(List.of(currentStep, nextExecutionStep))
                    .flatMap(stepRepository::save)
                    .then()
                    .block();
            executionRepository.save(execution).block();

            triggerStepExecution(nextStepRule, execution, nextExecutionStep.getId());
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private void validateInputPayload(Map<String, Object> inputPayload, List<WorkflowTypeField> workflowTypeFields) {
        if (inputPayload == null) {
            throw new IllegalArgumentException("Input payload cannot be null");
        }

        if (workflowTypeFields == null || workflowTypeFields.isEmpty()) {
            throw new IllegalArgumentException("Workflow type must have at least one field definition");
        }

        Map<String, WorkflowTypeField> fieldDefinitionMap = new java.util.HashMap<>();
        for (WorkflowTypeField field : workflowTypeFields) {
            fieldDefinitionMap.put(field.getFieldKey(), field);
        }

        for (String payloadKey : inputPayload.keySet()) {
            if (!fieldDefinitionMap.containsKey(payloadKey)) {
                throw new IllegalArgumentException(
                    String.format("Extra field '%s' not defined in workflow type. Allowed fields: %s",
                        payloadKey, fieldDefinitionMap.keySet())
                );
            }
        }

        for (WorkflowTypeField field : workflowTypeFields) {
            String fieldKey = field.getFieldKey();
            Object value = inputPayload.get(fieldKey);

            if (Boolean.TRUE.equals(field.getIsRequired()) && (value == null || isEmpty(value))) {
                throw new IllegalArgumentException(
                    String.format("Required field '%s' (%s) is missing or empty",
                        fieldKey, field.getFieldLabel())
                );
            }

            if (value == null || isEmpty(value)) {
                continue;
            }

            validateFieldDataType(fieldKey, field, value);

            if (field.getFieldType() == WorkflowFieldType.ENUM) {
                validateEnumField(fieldKey, field, value);
            }

            if (field.getValidationRegex() != null && !field.getValidationRegex().isEmpty()) {
                validateRegexPattern(fieldKey, field, value);
            }
        }
    }

    private void validateFieldDataType(String fieldKey, WorkflowTypeField field, Object value) {
        WorkflowFieldType fieldType = field.getFieldType();

        switch (fieldType) {
            case STRING:
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' expects STRING type but received %s. Value: %s",
                            fieldKey, value.getClass().getSimpleName(), value)
                    );
                }
                break;

            case NUMBER:
                if (!(value instanceof Number)) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' expects NUMBER type but received %s. Value: %s",
                            fieldKey, value.getClass().getSimpleName(), value)
                    );
                }
                try {
                    Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' value '%s' is not a valid number", fieldKey, value)
                    );
                }
                break;

            case BOOLEAN:
                if (!(value instanceof Boolean)) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' expects BOOLEAN type but received %s. Value: %s",
                            fieldKey, value.getClass().getSimpleName(), value)
                    );
                }
                break;

            case DATE:
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' expects DATE (as string) type but received %s. Value: %s",
                            fieldKey, value.getClass().getSimpleName(), value)
                    );
                }
                // Validate date format (ISO 8601: YYYY-MM-DD)
                try {
                    java.time.LocalDate.parse((String) value);
                } catch (java.time.format.DateTimeParseException e) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' value '%s' is not a valid DATE. Expected format: YYYY-MM-DD",
                            fieldKey, value)
                    );
                }
                break;

            case DATETIME:
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' expects DATETIME (as string) type but received %s. Value: %s",
                            fieldKey, value.getClass().getSimpleName(), value)
                    );
                }
                // Validate datetime format (ISO 8601)
                try {
                    java.time.Instant.parse((String) value);
                } catch (java.time.format.DateTimeParseException e) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' value '%s' is not a valid DATETIME. Expected format: ISO 8601 (e.g., 2026-04-02T10:00:00Z)",
                            fieldKey, value)
                    );
                }
                break;

            case ENUM:
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' expects ENUM (as string) type but received %s. Value: %s",
                            fieldKey, value.getClass().getSimpleName(), value)
                    );
                }
                break;

            case JSON:
                if (!(value instanceof Map<?, ?>) && !(value instanceof List<?>) && !(value instanceof String)) {
                    throw new IllegalArgumentException(
                        String.format("Field '%s' expects JSON type (object/array/string) but received %s. Value: %s",
                            fieldKey, value.getClass().getSimpleName(), value)
                    );
                }
                // If it's a string, validate it's valid JSON
                if (value instanceof String) {
                    try {
                        JsonUtils.fromJson((String) value, new TypeReference<Object>() {});
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                            String.format("Field '%s' value is not valid JSON: %s", fieldKey, e.getMessage())
                        );
                    }
                }
                break;

            default:
                throw new IllegalArgumentException(
                    String.format("Unknown field type '%s' for field '%s'", fieldType, fieldKey)
                );
        }
    }

    private void validateEnumField(String fieldKey, WorkflowTypeField field, Object value) {
        List<String> allowedValues = field.getAllowedValues();

        if (allowedValues == null || allowedValues.isEmpty()) {
            throw new IllegalArgumentException(
                String.format("ENUM field '%s' has no allowed values defined", fieldKey)
            );
        }

        String stringValue = value.toString();
        if (!allowedValues.contains(stringValue)) {
            throw new IllegalArgumentException(
                String.format("Field '%s' value '%s' is not in allowed values: %s",
                    fieldKey, stringValue, allowedValues)
            );
        }
    }

    private void validateRegexPattern(String fieldKey, WorkflowTypeField field, Object value) {
        String regex = field.getValidationRegex();
        String stringValue = value.toString();

        try {
            if (!stringValue.matches(regex)) {
                throw new IllegalArgumentException(
                    String.format("Field '%s' value '%s' does not match required pattern: %s",
                        fieldKey, stringValue, regex)
                );
            }
        } catch (java.util.regex.PatternSyntaxException e) {
            throw new IllegalArgumentException(
                String.format("Invalid regex pattern for field '%s': %s", fieldKey, e.getMessage())
            );
        }
    }

    private boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }
        if (value instanceof Collection) {
            return ((java.util.Collection<?>) value).isEmpty();
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }
        return false;
    }

    private void markRemainingNotStartedAsIgnored(List<WorkflowExecutionStep> steps, Long currentExecutionStepId, Instant now) {
        steps.stream()
                .filter(step -> !step.getId().equals(currentExecutionStepId))
                .filter(step -> step.getStatus() == ExecutionStepStatus.NOT_STARTED)
                .forEach(step -> {
                    step.setStatus(ExecutionStepStatus.IGNORED);
                    step.setTerminatedAt(now);
                });
    }

    private void markRemainingNotStartedAsSkipped(List<WorkflowExecutionStep> steps, Long currentExecutionStepId, Instant now) {
        steps.stream()
                .filter(step -> !step.getId().equals(currentExecutionStepId))
                .filter(step -> step.getStatus() == ExecutionStepStatus.NOT_STARTED)
                .forEach(step -> {
                    step.setStatus(ExecutionStepStatus.SKIPPED);
                    step.setTerminatedAt(now);
                });
    }

    // Implement the code logic for finding the next step id
    private Long resolveNextStepId(Workflow workflow, Integer currentStepLine) {
        return workflow.getStepRule().stream()
                .filter(sr -> sr.getStep() != null && sr.getStep().getStepLine() != null)
                .filter(sr -> sr.getStep().getStepLine() > currentStepLine)
                .min(Comparator.comparingInt(sr -> sr.getStep().getStepLine()))
                .map(sr -> sr.getStep().getId())
                .orElseThrow(() -> new IllegalStateException("No next step found after line " + currentStepLine));
    }

    private WorkflowStepRule resolveFirstStepExecution(Workflow workflow) {
        return workflow.getStepRule().stream().sorted(Comparator.comparing(sr -> sr.getStep().getStepLine())).findFirst()
                .orElseThrow(() -> new IllegalStateException("Workflow has no steps defined"));
    }

    private void triggerStepExecutionAsync(WorkflowStepRule stepRule, WorkflowExecution execution, Long executionStepId) {
        Mono.fromRunnable(() -> triggerStepExecution(stepRule, execution, executionStepId))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> Mono.empty())
                .subscribe();
    }

    private void triggerStepExecution(WorkflowStepRule stepRule, WorkflowExecution execution, Long executionStepId){
        if (stepRule.getRule().getRuleType() == StepType.DECISION) {
            handleDecisionStep(stepRule, execution, executionStepId);
            return;
        }
        ActionRequest actionRequest = buildActionRequest(stepRule);
        actionService.handleStepExecution(actionRequest, execution.getId(), executionStepId).block();
    }

    private void handleDecisionStep(WorkflowStepRule decisionStepRule, WorkflowExecution execution, Long decisionExecutionStepId) {
        DecisionRuleConfig config = requireRuleConfig(decisionStepRule.getRule(), DecisionRuleConfig.class);
        String nextStepCode = resolveNextStepCode(config, execution.getInputPayload());

        Workflow workflow = workflowServiceClient.getWorkflowById(execution.getWorkflowId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", "id", execution.getWorkflowId().toString())))
                .block();

        if (workflow == null) {
            throw new IllegalStateException("Workflow not found for execution: " + execution.getId());
        }

        WorkflowStepRule nextStepRule = resolveNextStepRule(workflow, nextStepCode);
        Long decisionStepId = decisionStepRule.getStep().getId();
        Long nextStepId = nextStepRule.getStep().getId();
        if (decisionStepId.equals(nextStepId)) {
            throw new IllegalStateException("Decision step cannot route to itself. stepId=" + decisionStepId);
        }

        List<WorkflowExecutionStep> executionSteps = stepRepository
                .findAllByWorkflowExecutionIdOrderById(execution.getId())
                .collectList()
                .block();

        if (executionSteps == null || executionSteps.isEmpty()) {
            throw new IllegalStateException("No execution steps found for executionId=" + execution.getId());
        }

        WorkflowExecutionStep decisionExecutionStep = executionSteps.stream()
                .filter(step -> decisionExecutionStepId.equals(step.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowExecutionStep", "id", decisionExecutionStepId.toString()));

        WorkflowExecutionStep nextExecutionStep = executionSteps.stream()
                .filter(step -> nextStepId.equals(step.getStepId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowExecutionStep", "stepId", nextStepId.toString()));

        int currentLine = decisionStepRule.getStep().getStepLine();
        int targetLine = nextStepRule.getStep().getStepLine();
        if (targetLine <= currentLine) {
            throw new IllegalStateException("Decision target line must be greater than current line");
        }
        Instant now = Instant.now();

        Map<Long, WorkflowStepRule> workflowStepRuleByStepId = workflow.getStepRule().stream()
                .collect(java.util.stream.Collectors.toMap(sr -> sr.getStep().getId(), sr -> sr));

        List<WorkflowExecutionStep> stepsToSave = executionSteps.stream()
                .filter(step -> !step.getId().equals(decisionExecutionStep.getId()) && !step.getId().equals(nextExecutionStep.getId()))
                .filter(step -> shouldSkipStep(step, workflowStepRuleByStepId, currentLine, targetLine, decisionStepId, nextStepId))
                .filter(step -> step.getStatus() == ExecutionStepStatus.NOT_STARTED
                        || step.getStatus() == ExecutionStepStatus.STARTED
                        || step.getStatus() == ExecutionStepStatus.IN_PROGRESS)
                .peek(step -> {
                    step.setStatus(ExecutionStepStatus.SKIPPED);
                    step.setTerminatedAt(now);
                })
                .toList();

        decisionExecutionStep.setStatus(ExecutionStepStatus.COMPLETED);
        decisionExecutionStep.setMessage("Decision evaluated. Next step code: " + nextStepCode);
        decisionExecutionStep.setError(null);
        decisionExecutionStep.setTerminatedAt(now);
        stepsToSave = new ArrayList<>(stepsToSave);
        stepsToSave.add(decisionExecutionStep);

        int nextOrder = decisionExecutionStep.getOrderOfExecution() == null ? 1 : decisionExecutionStep.getOrderOfExecution() + 1;
        nextExecutionStep.setStatus(ExecutionStepStatus.IN_PROGRESS);
        nextExecutionStep.setOrderOfExecution(nextOrder);
        if (nextExecutionStep.getInitiatedAt() == null) {
            nextExecutionStep.setInitiatedAt(now);
        }
        stepsToSave.add(nextExecutionStep);

        Flux.fromIterable(stepsToSave)
                .flatMap(stepRepository::save)
                .then()
                .block();

        execution.setCurrentStepId(nextStepId);
        executionRepository.save(execution).block();

        triggerStepExecution(nextStepRule, execution, nextExecutionStep.getId());
    }

    private String resolveNextStepCode(DecisionRuleConfig config, String inputPayloadJson) {
        if (config.getConditionExpression() == null) {
            throw new IllegalArgumentException("Decision condition expression cannot be null");
        }
        boolean conditionResult = ConditionEvaluator.evaluate(config.getConditionExpression(), inputPayloadJson);
        String nextStepCode = conditionResult ? config.getOnSuccessStepCode() : config.getOnFailureStepCode();
        if (nextStepCode == null || nextStepCode.isBlank()) {
            throw new IllegalArgumentException("Decision result did not resolve to a valid next step code");
        }
        return nextStepCode;
    }

    private WorkflowStepRule resolveNextStepRule(Workflow workflow, String stepCode) {
        return workflow.getStepRule().stream()
                .filter(sr -> sr.getStep().getStepCode() != null)
                .filter(sr -> sr.getStep().getStepCode().trim().equalsIgnoreCase(stepCode.trim()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("WorkflowStep", "stepCode", stepCode));
    }

    private boolean shouldSkipStep(
            WorkflowExecutionStep executionStep,
            Map<Long, WorkflowStepRule> workflowStepRuleByStepId,
            int currentLine,
            int targetLine,
            Long currentStepId,
            Long nextStepId
    ) {
        if (executionStep == null || executionStep.getStepId() == null) {
            return false;
        }
        if (targetLine <= currentLine) {
            return false;
        }
        if (executionStep.getStepId().equals(currentStepId) || executionStep.getStepId().equals(nextStepId)) {
            return false;
        }
        WorkflowStepRule stepRule = workflowStepRuleByStepId.get(executionStep.getStepId());
        if (stepRule == null || stepRule.getStep() == null || stepRule.getStep().getStepLine() == null) {
            return false;
        }

        int stepLine = stepRule.getStep().getStepLine();
        return (stepLine > currentLine && stepLine < targetLine) || stepLine == targetLine;
    }

    private ActionRequest buildActionRequest(WorkflowStepRule stepRule) {
        WorkflowStep step = stepRule.getStep();
        WorkflowRule rule = stepRule.getRule();

        if (rule.getRuleType() == StepType.APPROVAL) {
            ApprovalRuleConfig config = requireRuleConfig(rule, ApprovalRuleConfig.class);
            ApprovalAction action = new ApprovalAction();
            action.setActionType(ActionType.APPROVAL);
            action.setName(config.getName() != null ? config.getName() : step.getName());
            action.setChannel(config.getChannel());
            action.setApprovalType(config.getApprovalType());
            action.setApproverId(config.getApproverId());
            action.setApproverRoleId(config.getApproverRoleId());
            action.setTeamId(config.getTeamId());
            action.setSubject(config.getSubject());
            action.setBody(config.getBody());
            return action;
        }

        if (rule.getRuleType() == StepType.AUTO_APPROVAL) {
            AutoApprovalRuleConfig config = requireRuleConfig(rule, AutoApprovalRuleConfig.class);
            AutoApprovalAction action = new AutoApprovalAction();
            action.setActionType(ActionType.AUTO_APPROVAL);
            action.setName(config.getName() != null ? config.getName() : step.getName());
            return action;
        }

        if (rule.getRuleType() == StepType.NOTIFICATION) {
            NotificationRuleConfig config = requireRuleConfig(rule, NotificationRuleConfig.class);
            NotificationAction action = new NotificationAction();
            action.setActionType(ActionType.NOTIFICATION);
            action.setName(config.getName() != null ? config.getName() : step.getName());
            action.setChannel(config.getChannel());
            action.setNotifyTo(config.getNotifyTo());
            action.setSubject(config.getSubject());
            action.setBody(config.getBody());
            return action;
        }

        if (rule.getRuleType() == StepType.TASK) {
            TaskRuleConfig config = requireRuleConfig(rule, TaskRuleConfig.class);
            TaskAction action = new TaskAction();
            action.setActionType(ActionType.TASK);
            action.setName(config.getName() != null ? config.getName() : step.getName());
            return action;
        }

        if (rule.getRuleType() == StepType.WEBHOOK) {
            WebHookRuleConfig config = requireRuleConfig(rule, WebHookRuleConfig.class);
            WebHookAction action = new WebHookAction();
            action.setActionType(ActionType.WEBHOOK);
            action.setName(config.getName() != null ? config.getName() : step.getName());
            action.setUri(config.getUri());
            action.setMethod(config.getMethod());
            action.setHeader(config.getHeader());
            action.setBody(config.getBody());
            return action;
        }

        if (rule.getRuleType() == StepType.DELAY) {
            DelayRuleConfig config = requireRuleConfig(rule, DelayRuleConfig.class);
            DelayAction action = new DelayAction();
            action.setActionType(ActionType.DELAY);
            action.setName(config.getName() != null ? config.getName() : step.getName());
            action.setDelayDurationInMillis(config.getDelayDurationInMillis());
            return action;
        }

        if (rule.getRuleType() == StepType.DECISION) {
            throw new UnsupportedOperationException("DECISION step is handled locally");
        }

        throw new IllegalArgumentException("Invalid step type: " + rule.getRuleType());
    }


    private <T extends RuleConfig> T requireRuleConfig(WorkflowRule rule, Class<T> configType) {
        RuleConfig config = rule.getRuleConfig();
        if (!configType.isInstance(config)) {
            throw new IllegalArgumentException(
                    "Rule config mismatch for step type " + rule.getRuleType() + ". Expected: " + configType.getSimpleName()
            );
        }
        return configType.cast(config);
    }
}

