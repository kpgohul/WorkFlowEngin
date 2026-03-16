package com.friends.executionservice.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class WorkFlowDTO {

    private UUID id;
    private Integer version;
    private UUID startStepId;

}