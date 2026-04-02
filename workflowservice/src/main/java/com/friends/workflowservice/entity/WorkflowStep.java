package com.friends.workflowservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workflow_step")
public class WorkflowStep {

    @Id
    @ReadOnlyProperty
    private Long id;
    private Long workflowId;
    private String stepCode;
    private String name;
    private Boolean isLast;
    private Integer stepLine;
    private Long stepTimeoutInMillis;

}
