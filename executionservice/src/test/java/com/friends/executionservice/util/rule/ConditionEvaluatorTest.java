package com.friends.executionservice.util.rule;

import com.friends.executionservice.appconstant.Operator;
import com.friends.executionservice.clientdto.workflowclientdto.condition.RuleExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConditionEvaluatorTest {

    @Test
    void evaluatesSimpleEqualsRule() {
        RuleExpression expression = new RuleExpression();
        expression.setField("amount");
        expression.setOperator(Operator.EQ);
        expression.setValue(100);

        boolean result = ConditionEvaluator.evaluate(expression, "{\"amount\":100}");

        Assertions.assertTrue(result);
    }
}

