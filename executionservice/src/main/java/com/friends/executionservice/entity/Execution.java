package com.friends.executionservice.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("execution")
public class Execution {

    @Id
    private UUID id;
    private UUID workflowId;
    private Integer workflowVersion;
    private String status;
    private String data;
    private UUID currentStepId;
    private Integer retries;
    private UUID triggeredBy;
    private Instant startedAt;
    private Instant endedAt;

}
