package com.friends.executionservice.client;

import com.friends.executionservice.dto.WorkFlowDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkflowClient {

    private final WebClient webClient;

    public Mono<WorkFlowDTO> getWorkflow(UUID workflowId) {

        return webClient
                .get()
                .uri("http://workflow-service/workflows/" + workflowId)
                .retrieve()
                .bodyToMono(WorkFlowDTO.class);
    }

}