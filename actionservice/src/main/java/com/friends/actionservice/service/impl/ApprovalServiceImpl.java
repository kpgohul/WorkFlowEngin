package com.friends.actionservice.service.impl;

import com.friends.actionservice.actionsdto.ActionResponse;
import com.friends.actionservice.appconstant.ApprovalStatus;
import com.friends.actionservice.entity.ExecutionAction;
import com.friends.actionservice.exception.ResourceNotFoundException;
import com.friends.actionservice.kafka.KafkaActionResultProducer;
import com.friends.actionservice.repo.ApprovalRequestRepository;
import com.friends.actionservice.repo.ExecutionActionRepository;
import com.friends.actionservice.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final ExecutionActionRepository executionActionRepository;
    private final KafkaActionResultProducer resultProducer;

    private Mono<Void> completeAndPublish(ExecutionAction execAction, boolean approved, Instant now) {
        execAction.setIsActive(false);
        execAction.setCompletedAt(now);

        ActionResponse response = ActionResponse.builder()
                .executionId(execAction.getExecutionId())
                .executionStepId(execAction.getExecutionStepId())
                .isSuccess(approved)
                .message(approved ? "Approved" : "Rejected")
                .error(approved ? null : "Approval rejected")
                .build();

        return executionActionRepository.save(execAction)
                .then(resultProducer.send(response));
    }

    private boolean isApprove(String approval) {
        if (approval == null) {
            throw new IllegalArgumentException("approval is required (accept/reject)");
        }
        String val = approval.trim().toLowerCase();
        return val.equals("accept") || val.equals("approved") || val.equals("approve") || val.equals("true") || val.equals("yes");
    }

    @Override
    public Mono<ApprovalResult> respond(String token, String approval, Long approverId) {
        if (token == null || token.isBlank()) {
            return Mono.error(new IllegalArgumentException("token is required"));
        }

        boolean isApprove = isApprove(approval);
        Instant now = Instant.now();
        ApprovalStatus newStatus = isApprove ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;

        return approvalRequestRepository.resolveIfPending(token, newStatus, isApprove, approverId, now)
                .flatMap(updated -> {
                    if (updated > 0) {
                        return approvalRequestRepository.findByToken(token)
                                .switchIfEmpty(Mono.error(new ResourceNotFoundException("ApprovalRequest", "token", token)))
                                .flatMap(req -> executionActionRepository.findById(req.getExecutionActionId())
                                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("ExecutionAction", "id", req.getExecutionActionId().toString())))
                                        .flatMap(execAction -> completeAndPublish(execAction, isApprove, now)
                                                .thenReturn(new ApprovalResult(
                                                        true,
                                                        "Recorded",
                                                        isApprove ? "Thanks! Your approval was recorded." : "Thanks! Your rejection was recorded.",
                                                        execAction.getExecutionId(),
                                                        execAction.getExecutionStepId()
                                                )))
                                );
                    }

                    return approvalRequestRepository.findByToken(token)
                            .flatMap(existing -> executionActionRepository.findById(existing.getExecutionActionId())
                                    .map(execAction -> new ApprovalResult(
                                            false,
                                            "Already resolved",
                                            "This approval request was already resolved and your response was ignored.",
                                            execAction.getExecutionId(),
                                            execAction.getExecutionStepId()
                                    ))
                                    .switchIfEmpty(Mono.just(new ApprovalResult(false, "Already resolved", "This approval request was already resolved.", null, null)))
                            )
                            .switchIfEmpty(Mono.just(new ApprovalResult(false, "Not found", "Invalid approval token.", null, null)));
                });
    }
}
