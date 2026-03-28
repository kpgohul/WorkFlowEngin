package com.friends.workflowservice.util.condition;

import com.friends.workflowservice.appconstant.steprule.ConditionType;
import com.friends.workflowservice.appconstant.steprule.ExpressionType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupExpression extends ConditionExpression {

    private ConditionType conditionType;
    private List<ConditionExpression> conditions;

    public GroupExpression() {
        setType(ExpressionType.GROUP);
    }

}