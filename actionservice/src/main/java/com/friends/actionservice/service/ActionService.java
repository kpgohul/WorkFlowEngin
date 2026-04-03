package com.friends.actionservice.service;

import com.friends.actionservice.actionsdto.ActionRequest;
import com.friends.actionservice.actionsdto.ActionResponse;
import reactor.core.publisher.Mono;

public interface ActionService {

    /**
     * Kafka consumer calls this for incoming action request messages.
     */
    Mono<Void> handleActionProcess(Long executionId, Long executionStepId, ActionRequest action);

    /**
     * Handles action result responses (typically produced by this service). Kept for symmetry/future use.
     */
    Mono<Void> handleActionResult(ActionResponse response);
}
