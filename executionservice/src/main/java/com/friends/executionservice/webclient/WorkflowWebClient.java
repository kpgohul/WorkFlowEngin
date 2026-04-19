package com.friends.executionservice.webclient;

import com.friends.executionservice.clientdto.workflowclientdto.workflow.Workflow;
import com.friends.executionservice.clientdto.workflowclientdto.workflowtype.WorkflowType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Client for communicating with the Workflow Service via the API Gateway.
 * Base URL is configured on the shared {@link WebClient} bean in ExecutionAppConfig.
 */
@Component
@RequiredArgsConstructor
public class WorkflowWebClient {

    private final WebClient webClient;

    /**
     * GET /api/v1/workflows/{id}?field=true
     * Fetches a full workflow including its type fields (required for input validation).
     */
    public Mono<Workflow> getWorkflowById(Long workflowId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/workflows/{id}")
                        .queryParam("field", "true")
                        .build(workflowId))
                .retrieve()
                .bodyToMono(Workflow.class);
    }

    /**
     * GET /api/v1/workflow-types/{id}
     */
    public Mono<WorkflowType> getWorkflowTypeById(Long workflowTypeId) {
        return webClient.get()
                .uri("/api/v1/workflow-types/{id}", workflowTypeId)
                .retrieve()
                .bodyToMono(WorkflowType.class);
    }
}
