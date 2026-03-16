package com.friends.executionservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StepDTO {

    private UUID id;
    private String name;
    private String stepType;

}