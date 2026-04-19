package com.friends.executionservice.service;

import com.friends.executionservice.appconstant.StepType;
import com.friends.executionservice.clientdto.actionclientdto.ActionRequest;
import com.friends.executionservice.clientdto.actionclientdto.ActionResponse;
import com.friends.executionservice.dto.action.StepActionRequest;
import reactor.core.publisher.Mono;

public interface ActionService {

    Mono<Void> handleStepExecution(ActionRequest request, Long executionId, Long executionStepId);

}
