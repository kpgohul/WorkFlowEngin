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
@Table("execution_logs")
public class ExecutionLog {

    @Id
    private UUID id;

    private UUID executionId;

    private String stepName;

    private String stepType;

    private String evaluatedRules;

    private String selectedNextStep;

    private String status;

    private String approverId;

    private String errorMessage;

    private Instant startedAt;

    private Instant endedAt;
}
