package com.friends.workflowservice.service.impl;

import com.friends.workflowservice.appconstant.ExpressionType;
import com.friends.workflowservice.appconstant.Operator;
import com.friends.workflowservice.appconstant.RuleType;
import com.friends.workflowservice.dto.condition.ConditionExpression;
import com.friends.workflowservice.dto.condition.GroupExpression;
import com.friends.workflowservice.dto.condition.RuleExpression;
import com.friends.workflowservice.dto.ruleconfig.DecisionRuleConfig;
import com.friends.workflowservice.dto.workflowrule.WorkflowRuleRequest;
import com.friends.workflowservice.dto.workflowrule.WorkflowRuleResponse;
import com.friends.workflowservice.entity.WorkflowStep;
import com.friends.workflowservice.entity.WorkflowTypeField;
import com.friends.workflowservice.exception.ResourceAlreadyExistException;
import com.friends.workflowservice.exception.ResourceNotFoundException;
import com.friends.workflowservice.mapper.RuleMapper;
import com.friends.workflowservice.repo.WorkflowRepository;
import com.friends.workflowservice.repo.WorkflowRuleRepository;
import com.friends.workflowservice.repo.WorkflowStepRepository;
import com.friends.workflowservice.repo.WorkflowTypeFieldRepository;
import com.friends.workflowservice.service.WorkflowRuleService;
import com.friends.workflowservice.util.rule.RuleFieldValidator;
import com.friends.workflowservice.util.step.WorkflowStepUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowRuleServiceImpl implements WorkflowRuleService {

        private final WorkflowRuleRepository ruleRepository;
        private final WorkflowStepRepository stepRepository;
        private final WorkflowRepository workflowRepository;
        private final WorkflowTypeFieldRepository fieldRepository;

        @Override
        public Mono<WorkflowRuleResponse> createWorkflowRules(Long stepId, Mono<WorkflowRuleRequest> requestMono) {
                return stepRepository.findById(stepId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowStep", "id", stepId.toString())))
                                .flatMap(step -> requestMono
                                                .flatMap(request -> validateDecisionRule(step, request)
                                                                .then(ruleRepository.findByStepId(stepId)
                                                                                .flatMap(existing -> Mono
                                                                                                .<WorkflowRuleResponse>error(new ResourceAlreadyExistException("WorkflowRule", "stepId", stepId.toString())))
                                                                                .switchIfEmpty(ruleRepository.save(
                                                                                                RuleMapper.toEntity(stepId, request))
                                                                                                .map(RuleMapper::toResponse)))));
        }

        @Override
        public Mono<WorkflowRuleResponse> updateWorkflowRules(Long stepId, Mono<WorkflowRuleRequest> requestMono) {
                return stepRepository.findById(stepId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowStep", "id", stepId.toString())))
                                .flatMap(step -> requestMono
                                                .flatMap(request -> validateDecisionRule(step, request)
                                                                .then(ruleRepository.findByStepId(stepId)
                                                                                .flatMap(existing -> ruleRepository
                                                                                                .save(RuleMapper.toEntity(existing, request))
                                                                                                .map(RuleMapper::toResponse))
                                                                                .switchIfEmpty(ruleRepository.save(RuleMapper.toEntity(stepId, request))
                                                                                                .map(RuleMapper::toResponse)))));
        }

        @Override
        public Mono<WorkflowRuleResponse> getWorkflowRuleById(Long ruleId) {
                return ruleRepository.findById(ruleId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowRule", "id", ruleId.toString())))
                                .map(RuleMapper::toResponse);
        }

        @Override
        public Mono<Void> deleteWorkflowRuleById(Long ruleId) {
                return ruleRepository.findById(ruleId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("WorkflowRule", "id", ruleId.toString())))
                                .flatMap(existing -> ruleRepository.deleteById(existing.getId()));
        }

        private Mono<Void> validateDecisionRule(WorkflowStep currentStep, WorkflowRuleRequest request) {
                if (request.getRuleType() != RuleType.DECISION) {
                        return Mono.empty();
                }

                if (!(request.getRuleConfig() instanceof DecisionRuleConfig config)) {
                        return Mono.error(new IllegalArgumentException("ruleConfig must be DecisionRuleConfig when ruleType is DECISION"));
                }

                try {
                        validateDecisionConditionExpression(config.getConditionExpression());
                } catch (IllegalArgumentException ex) {
                        return Mono.error(ex);
                }

                // Validate condition fields against workflow type fields
                return validateConditionFieldsWithMetadata(currentStep.getWorkflowId(), config.getConditionExpression())
                                .flatMap(v -> {
                                        String successStepCode = WorkflowStepUtil.normalizeStepCode(config.getOnSuccessStepCode());
                                        String failureStepCode = WorkflowStepUtil.normalizeStepCode(config.getOnFailureStepCode());

                                        if (successStepCode != null && successStepCode.equals(failureStepCode)) {
                                                return Mono.error(new IllegalArgumentException("Decision success and failure stepCode must be different"));
                                        }

                                        return stepRepository.findAllByWorkflowId(currentStep.getWorkflowId())
                                .collectList()
                                .flatMap(steps -> {
//                                        long nextLineCount = steps.stream()
//                                                        .filter(step -> step.getStepLine() != null
//                                                                        && currentStep.getStepLine() != null)
//                                                        .filter(step -> step.getStepLine()
//                                                                        .equals(currentStep.getStepLine() + 1))
//                                                        .count();
//
//                                        if (nextLineCount != 2) {
//                                                return Mono.error(new IllegalArgumentException("Decision step requires exactly two steps at next stepLine"));
//                                        }
// Rule's failure or success can point to the decendent steps, not necessarily the immediate next line. So relaxing this validation for now.

                                        Map<String, WorkflowStep> stepsByCode = steps.stream()
                                                        .collect(Collectors.toMap(
                                                                        step -> WorkflowStepUtil.normalizeStepCode(
                                                                                        step.getStepCode()),
                                                                        Function.identity(),
                                                                        (a, b) -> a));

                                        WorkflowStep successStep = stepsByCode.get(successStepCode);
                                        WorkflowStep failureStep = stepsByCode.get(failureStepCode);

                                        if (successStep == null || failureStep == null) {
                                                return Mono.error(new IllegalArgumentException("Decision onSuccessStepCode and onFailureStepCode must exist in workflow steps."));
                                        }

                                        Integer currentLine = currentStep.getStepLine();
                                        if (currentLine == null || successStep.getStepLine() == null || failureStep.getStepLine() == null) {
                                                return Mono.error(new IllegalArgumentException("stepLine is required for decision validation"));
                                        }

                                        if (successStep.getStepLine() <= currentLine || failureStep.getStepLine() <= currentLine) {
                                                return Mono.error(new IllegalArgumentException("Decision target stepLine must be greater than current stepLine"));
                                        }

                                        return Mono.empty();
                                });
                                });
        }

        /**
         * Validates all condition expressions (RuleExpressions) against workflow type field definitions.
         * This ensures field names exist, data types match, operators are supported, and values are valid.
         */
        private Mono<Void> validateConditionFieldsWithMetadata(Long workflowId, ConditionExpression conditionExpression) {
                return workflowRepository.findById(workflowId)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", "id", workflowId.toString())))
                                .flatMap(workflow -> fieldRepository.findAllByWorkflowTypeId(workflow.getWorkflowTypeId())
                                                .collectList()
                                                .flatMap(fields -> {
                                                        if (fields.isEmpty()) {
                                                                return Mono.error(new IllegalArgumentException("No fields defined for workflow type"));
                                                        }

                                                        try {
                                                                validateConditionExpressionFields(conditionExpression, fields, "conditionExpression");
                                                                return Mono.empty();
                                                        } catch (IllegalArgumentException ex) {
                                                                return Mono.error(ex);
                                                        }
                                                }));
        }

        /**
         * Recursively validates all RuleExpressions within a condition expression tree against field metadata.
         */
        private static void validateConditionExpressionFields(ConditionExpression expression, List<WorkflowTypeField> fields, String path) {
                if (expression instanceof RuleExpression rule) {
                        validateRuleExpressionFields(rule, fields, path);
                } else if (expression instanceof GroupExpression group) {
                        validateGroupExpressionFields(group, fields, path);
                }
        }

        /**
         * Validates a single RuleExpression against the available workflow type fields.
         */
        private static void validateRuleExpressionFields(RuleExpression rule, List<WorkflowTypeField> fields, String path) {
                String fieldKey = rule.getField();
                Operator operator = rule.getOperator();
                Object value = rule.getValue();

                // Use RuleFieldValidator to validate field, operator, and value
                RuleFieldValidator.validateFieldValue(fieldKey, value, operator, fields, path);
        }

        /**
         * Recursively validates all nested RuleExpressions within a GroupExpression.
         */
        private static void validateGroupExpressionFields(GroupExpression group, List<WorkflowTypeField> fields, String path) {
                List<ConditionExpression> conditions = group.getConditions();
                for (int index = 0; index < conditions.size(); index++) {
                        validateConditionExpressionFields(conditions.get(index), fields, path + ".conditions[" + index + "]");
                }
        }

        static void validateDecisionConditionExpression(ConditionExpression conditionExpression) {
                validateDecisionConditionExpression(conditionExpression, "conditionExpression");
        }

        private static void validateDecisionConditionExpression(ConditionExpression conditionExpression, String path) {
                if (conditionExpression == null) {
                        throw new IllegalArgumentException("Decision " + path + " is required");
                }

                ExpressionType expressionType = conditionExpression.getType();
                if (expressionType == null) {
                        throw new IllegalArgumentException("Decision " + path + ".type is required");
                }

                if (conditionExpression instanceof RuleExpression ruleExpression) {
                        validateRuleExpression(ruleExpression, path, expressionType);
                        return;
                }

                if (conditionExpression instanceof GroupExpression groupExpression) {
                        validateGroupExpression(groupExpression, path, expressionType);
                        return;
                }

                throw new IllegalArgumentException("Decision " + path + " has unsupported expression type");
        }

        private static void validateRuleExpression(RuleExpression ruleExpression, String path, ExpressionType expressionType) {
                if (expressionType != ExpressionType.RULE) {
                        throw new IllegalArgumentException("Decision " + path + ".type must be RULE");
                }

                if (ruleExpression.getField() == null || ruleExpression.getField().isBlank()) {
                        throw new IllegalArgumentException("Decision " + path + ".field is required");
                }

                Operator operator = ruleExpression.getOperator();
                if (operator == null) {
                        throw new IllegalArgumentException("Decision " + path + ".operator is required");
                }

                // If operator is IS_NULL or IS_NOT_NULL, value must be null. For other operators, value must be non-null.
                Object value = ruleExpression.getValue();
                if (operator == Operator.IS_NULL || operator == Operator.IS_NOT_NULL) {
                        if (value != null) {
                                throw new IllegalArgumentException("Decision " + path + ".value must be null for " + operator);
                        }
                        return;
                }

                if (value == null) {
                        throw new IllegalArgumentException("Decision " + path + ".value is required for " + operator);
                }
        }

        private static void validateGroupExpression(GroupExpression groupExpression, String path, ExpressionType expressionType) {
                if (expressionType != ExpressionType.GROUP) {
                        throw new IllegalArgumentException("Decision " + path + ".type must be GROUP");
                }

                if (groupExpression.getConditionType() == null) {
                        throw new IllegalArgumentException("Decision " + path + ".conditionType is required");
                }

                List<ConditionExpression> childConditions = groupExpression.getConditions();
                if (childConditions == null || childConditions.isEmpty()) {
                        throw new IllegalArgumentException("Decision " + path + ".conditions must contain at least one expression");
                }

                for (int index = 0; index < childConditions.size(); index++) {
                        validateDecisionConditionExpression(childConditions.get(index), path + ".conditions[" + index + "]");
                }
        }
}
