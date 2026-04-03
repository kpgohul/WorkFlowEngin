package com.friends.actionservice.repo;

import com.friends.actionservice.appconstant.ApprovalStatus;
import com.friends.actionservice.entity.ApprovalRequest;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface ApprovalRequestRepository extends R2dbcRepository<ApprovalRequest, Long> {
    Mono<ApprovalRequest> findByToken(String token);

    @Modifying
    @Query("UPDATE approval_requests SET status = :status, is_approved = :isApproved, approver_id = :approverId, responded_at = :respondedAt WHERE token = :token AND status = 'PENDING'")
    Mono<Integer> resolveIfPending(
            @Param("token") String token,
            @Param("status") ApprovalStatus status,
            @Param("isApproved") Boolean isApproved,
            @Param("approverId") Long approverId,
            @Param("respondedAt") Instant respondedAt
    );
}

