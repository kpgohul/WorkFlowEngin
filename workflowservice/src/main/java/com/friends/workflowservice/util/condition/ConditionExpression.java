package com.friends.workflowservice.util.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.friends.workflowservice.appconstant.steprule.ExpressionType;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleExpression.class, name = "RULE"),
        @JsonSubTypes.Type(value = GroupExpression.class, name = "GROUP")
})
@Getter
@Setter
public abstract class ConditionExpression {

    private ExpressionType type;

}