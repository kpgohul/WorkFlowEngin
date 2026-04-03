package com.friends.actionservice.service;

import reactor.core.publisher.Mono;

public interface ApprovalService {

    record ApprovalResult(boolean resolved, String title, String message, Long executionId, Long executionStepId) {}

    Mono<ApprovalResult> respond(String token, String approval, Long approverId);
}
