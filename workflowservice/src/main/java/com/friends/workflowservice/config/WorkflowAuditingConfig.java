package com.friends.workflowservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import reactor.core.publisher.Mono;

@Configuration
public class WorkflowAuditingConfig {

    @Bean
    public ReactiveAuditorAware<Long> reactiveAuditorAware() {
        // For now, workflowservice doesn't have a security context; default to null so auditing fields
        // can be populated externally or left empty. You can later wire SecurityContext-based lookup here.
        return () -> Mono.empty();
    }
}
