package com.friends.executionservice.util.rule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.friends.executionservice.appconstant.ConditionType;
import com.friends.executionservice.appconstant.Operator;
import com.friends.executionservice.clientdto.workflowclientdto.condition.ConditionExpression;
import com.friends.executionservice.clientdto.workflowclientdto.condition.GroupExpression;
import com.friends.executionservice.clientdto.workflowclientdto.condition.RuleExpression;
import com.friends.executionservice.util.common.JsonUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class ConditionEvaluator {

    private ConditionEvaluator() {
    }

    public static boolean evaluate(ConditionExpression expression, String inputPayloadJson) {
        Map<String, Object> payload = JsonUtils.fromJson(inputPayloadJson, new TypeReference<>() {
        });
        return evaluateExpression(expression, payload);
    }

    private static boolean evaluateExpression(ConditionExpression expression, Map<String, Object> payload) {
        if (expression instanceof RuleExpression ruleExpression) {
            return evaluateRule(ruleExpression, payload);
        }
        if (expression instanceof GroupExpression groupExpression) {
            boolean and = groupExpression.getConditionType() == ConditionType.AND;
            return and
                    ? groupExpression.getConditions().stream().allMatch(cond -> evaluateExpression(cond, payload))
                    : groupExpression.getConditions().stream().anyMatch(cond -> evaluateExpression(cond, payload));
        }
        return false;
    }

    private static boolean evaluateRule(RuleExpression ruleExpression, Map<String, Object> payload) {
        Object fieldValue = payload.get(ruleExpression.getField());
        Object expectedValue = ruleExpression.getValue();
        Operator operator = ruleExpression.getOperator();

        return switch (operator) {
            case EQ -> Objects.equals(fieldValue, expectedValue);
            case NE -> !Objects.equals(fieldValue, expectedValue);
            case GT -> compare(fieldValue, expectedValue) > 0;
            case GTE -> compare(fieldValue, expectedValue) >= 0;
            case LT -> compare(fieldValue, expectedValue) < 0;
            case LTE -> compare(fieldValue, expectedValue) <= 0;
            case IN -> expectedValue instanceof Collection<?> c && c.contains(fieldValue);
            case NOT_IN -> expectedValue instanceof Collection<?> c && !c.contains(fieldValue);
            case CONTAINS -> fieldValue != null && expectedValue != null && String.valueOf(fieldValue).contains(String.valueOf(expectedValue));
            case STARTS_WITH -> fieldValue != null && expectedValue != null && String.valueOf(fieldValue).startsWith(String.valueOf(expectedValue));
            case ENDS_WITH -> fieldValue != null && expectedValue != null && String.valueOf(fieldValue).endsWith(String.valueOf(expectedValue));
            case IS_NULL -> fieldValue == null;
            case IS_NOT_NULL -> fieldValue != null;
        };
    }

    private static int compare(Object left, Object right) {
        double l = left == null ? 0 : Double.parseDouble(String.valueOf(left));
        double r = right == null ? 0 : Double.parseDouble(String.valueOf(right));
        return Double.compare(l, r);
    }
}

