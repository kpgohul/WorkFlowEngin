package com.friends.workflowservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("workflow")
public class WorkFlow extends BaseEntity{

    @Id
    private UUID id;
    private String name;
    private Integer version;
    private Boolean isActive;
    private String inputSchema;
    private UUID startStepId;
    private Instant createdAt;
    private Instant updatedAt;

}