package com.friends.executionservice.webclient;

import com.friends.executionservice.clientdto.workflowclientdto.workflow.Workflow;
import com.friends.executionservice.clientdto.workflowclientdto.workflowtype.WorkflowType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WorkflowWebClient {

    private final WebClient webClient;

    @Value("${clients.workflow-service.base-url}")
    private String workflowServiceBaseUrl;


    public Mono<Workflow> getWorkflowById(Long workflowId) {
        return webClient.get()
                .uri(workflowServiceBaseUrl + "/workflows/{id}", workflowId)
                .retrieve()
                .bodyToMono(Workflow.class);
    }

    public Mono<WorkflowType> getWorkflowTypeById(Long workflowTypeId) {
        return webClient.get()
                .uri(workflowServiceBaseUrl + "/workflow-types/{id}", workflowTypeId)
                .retrieve()
                .bodyToMono(WorkflowType.class);
    }
}

