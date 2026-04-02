package com.friends.executionservice.clientdto.workflowclientdto.workflowstep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStep {

    private Long id;
    private Long workflowId;
    private String stepCode;
    private String name;
    private Integer stepLine;
    private Boolean isLast;
    private Long stepTimeoutInMillis;

}
