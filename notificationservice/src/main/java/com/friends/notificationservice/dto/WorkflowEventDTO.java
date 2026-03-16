package com.friends.notificationservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class WorkflowEventDTO {

    private UUID executionId;
    private UUID workflowId;
    private String step;
    private String status;

}