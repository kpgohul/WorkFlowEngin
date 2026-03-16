package com.friends.workflowservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("step")
public class Step extends BaseEntity {

    @Id
    private UUID id;
    private UUID workflowId;
    private String name;
    private String stepType;
    private Integer stepOrder;
    private String metadata;

}
