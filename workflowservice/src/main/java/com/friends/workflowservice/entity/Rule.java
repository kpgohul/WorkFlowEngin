package com.friends.workflowservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("rule")
public class Rule extends BaseEntity {

    @Id
    private UUID id;
    private UUID stepId;
    private String condition;
    private UUID nextStepId;
    private Integer priority;

}