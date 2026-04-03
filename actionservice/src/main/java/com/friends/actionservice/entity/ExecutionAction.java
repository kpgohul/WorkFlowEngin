package com.friends.actionservice.entity;

import com.friends.actionservice.appconstant.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "execution_actions")
public class ExecutionAction {
    @Id
    private Long id;
    private Long executionId;
    private Long executionStepId;
    private ActionType actionType;
    private Boolean isActive;

    /** JSON string of action details (kept flexible for future changes). */
    private String actionMeta;

    private Instant initiatedAt;
    private Instant completedAt;
}
