package com.friends.executionservice.service;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class RuleEngineService {

    private final ExpressionParser parser = new SpelExpressionParser();

    public boolean evaluate(String condition, Map<String, Object> inputData) {

        if (condition == null || condition.equalsIgnoreCase("DEFAULT")) {
            return true;
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        inputData.forEach(context::setVariable);
        Expression expression = parser.parseExpression(condition);
        Boolean result = expression.getValue(context, Boolean.class);
        return Boolean.TRUE.equals(result);
    }

}