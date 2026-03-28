package com.friends.workflowservice.util.condition;

import com.friends.workflowservice.appconstant.steprule.ExpressionType;
import com.friends.workflowservice.appconstant.steprule.Operator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleExpression extends ConditionExpression {

    private String field;
    private Operator operator;
    private Object value;

    public RuleExpression() {
        setType(ExpressionType.RULE);
    }

}