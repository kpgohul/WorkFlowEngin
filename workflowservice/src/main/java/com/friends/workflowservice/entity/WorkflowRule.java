package com.friends.workflowservice.entity;

import com.friends.workflowservice.appconstant.steprule.RuleFailureHandlerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workflow_rule")
public class WorkflowRule {
    @Id
    @ReadOnlyProperty
    private Long id;
    private Long stepId;
    private String ruleExpression;
    private RuleFailureHandlerType failureHandlerType;
    private String nextStepCodeOnFailure;

}
