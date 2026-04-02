package com.friends.executionservice.clientdto.workflowclientdto.condition;

import com.friends.executionservice.appconstant.ExpressionType;
import com.friends.executionservice.appconstant.Operator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleExpression extends ConditionExpression {

    @NotBlank(message = "Rule expression field is required.")
    private String field;

    @NotNull(message = "Rule expression operator is required.")
    private Operator operator;

    private Object value;

    public RuleExpression() {
        setType(ExpressionType.RULE);
    }

}