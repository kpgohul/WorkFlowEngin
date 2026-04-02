package com.friends.executionservice.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import com.friends.executionservice.appconstant.ExecutionStepStatus;
import com.friends.executionservice.appconstant.StepType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Table("workflow_execution_steps")
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowExecutionStep {

    @Id
    private Long id;
    private Long workflowExecutionId;
    private Long stepId;
    private String stepName;
    private StepType steptype;
    private Integer orderOfExecution;
    private ExecutionStepStatus status;
    private String message; //Step completion message should be added here
    private String error; //If any error occurred, then the same should be added here
    private Instant initiatedAt; // should be the exact time when the step is picked for execution
    private Instant terminatedAt; // should be the exact time when the step execution is completed irrespective of success or failure
}
