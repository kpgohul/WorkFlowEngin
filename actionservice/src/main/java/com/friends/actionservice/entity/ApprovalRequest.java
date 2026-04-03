package com.friends.actionservice.entity;

import com.friends.actionservice.appconstant.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "approval_requests")
public class ApprovalRequest {
    @Id
    private Long id;
    private Long executionActionId;
    private String token;
    private Boolean isApproved;
    private Long approverId;
    private ApprovalStatus status;
    @CreatedDate
    private Instant createdAt;
    private Instant respondedAt;
}
