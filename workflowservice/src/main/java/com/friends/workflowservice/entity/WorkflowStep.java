package com.friends.workflowservice.entity;

import com.friends.workflowservice.appconstant.workflowstep.StepStatus;
import com.friends.workflowservice.appconstant.workflowstep.StepType;
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
    private String stepCode; //LEAVE_APPROVAL, DOCUMENT_SUBMISSION, ETC
    private String name; //Leave Approval, Document Submission, etc
    private StepStatus status;
    private StepType stepType;
    private Integer stepOrder;
    private Long timeOutInMillis;
    private Long notifyTo;
    private String additionInfo; //JSON

}
