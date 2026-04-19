package com.friends.executionservice.service.impl;

import com.friends.executionservice.appconstant.StepType;
import com.friends.executionservice.clientdto.actionclientdto.ActionRequest;
import com.friends.executionservice.clientdto.actionclientdto.ActionResponse;
import com.friends.executionservice.dto.action.StepActionRequest;
import com.friends.executionservice.kafkaclient.KafkaStepExecutionProducer;
import com.friends.executionservice.service.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {

    private final KafkaStepExecutionProducer kafkaStepExecutionProducer;

    @Override
    public Mono<Void> handleStepExecution(ActionRequest request, Long executionId, Long executionStepId) {
        return kafkaStepExecutionProducer.publishStepAction(request, executionId, executionStepId);
    }

}
