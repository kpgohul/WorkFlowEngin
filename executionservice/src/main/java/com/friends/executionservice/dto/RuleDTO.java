package com.friends.executionservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RuleDTO {

    private UUID id;
    private String condition;
    private UUID nextStepId;
    private Integer priority;

}