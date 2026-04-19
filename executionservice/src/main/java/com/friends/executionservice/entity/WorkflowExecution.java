package com.friends.executionservice.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import com.friends.executionservice.appconstant.ExecutionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Table("workflow_executions")
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowExecution {

    @Id
    private Long id;
    private Long workflowId;
    private Long workflowTypeId;
    private Long currentStepId; // Should be sysnc with the currently executuing step in workflow_execution_steps table (StepType = IN_PROGRESS)
    private String error; //If any error occured at any step of workflow execution, then the same should be added here
    private String inputPayload; // will contain the json string of the user input
    private ExecutionStatus status;
    @CreatedBy
    private Long initiatedBy;
    private Instant initiatedAt;
    private Instant terminatedAt;

}
