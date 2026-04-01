package com.friends.workflowservice.dto.condition;

import com.friends.workflowservice.appconstant.ConditionType;
import com.friends.workflowservice.appconstant.ExpressionType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class GroupExpression extends ConditionExpression {

    @NotNull(message = "Group expression conditionType is required.")
    private ConditionType conditionType;
    @NotEmpty(message = "Group expression must contain at least one condition.")
    @Valid
    private List<ConditionExpression> conditions;

    public GroupExpression() {
        setType(ExpressionType.GROUP);
    }

}