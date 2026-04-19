package com.friends.gatewayserver.config;

import com.friends.gatewayserver.filter.LoggingFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

        @Value("${app.auth-url}")
        private String authServiceUri;

        @Value("${app.client.user-url}")
        private String userServiceUri;

        @Value("${app.client.workflow-url}")
        private String workflowServiceUri;

        @Value("${app.client.execution-url}")
        private String executionServiceUri;

        @Value("${app.client.action-url}")
        private String actionServiceUri;

        @Bean
        public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {

                return builder.routes()

                                // AUTH SERVICE
                                .route("auth-service", r -> r
                                                .path("/api/v1/auth/**",
                                                                "/api/v1/accounts/**",
                                                                "/oauth2/**",
                                                                "/login/**",
                                                                "/.well-known/openid-configuration/**")
                                                .filters(f -> f
                                                                .filter(new LoggingFilter()))
                                                .uri(authServiceUri))

                                // USER SERVICE
                                .route("user-service", r -> r
                                                .path("/api/v1/users/**",
                                                                "/api/v1/teams/**")
                                                .filters(f -> f
                                                                .filter(new LoggingFilter()))
                                                .uri(userServiceUri))

                                // WORKFLOW SERVICE
                                .route("workflow-service", r -> r
                                                .path("/api/v1/workflows/**",
                                                                "/api/v1/workflow-types/**")
                                                .filters(f -> f
                                                                .filter(new LoggingFilter()))
                                                .uri(workflowServiceUri))

                                // EXECUTION SERVICE
                                .route("execution-service", r -> r
                                                .path("/api/v1/executions/**")
                                                .filters(f -> f
                                                                .filter(new LoggingFilter()))
                                                .uri(executionServiceUri))

                                // ACTION SERVICE
                                .route("action-service", r -> r
                                                .path("/api/v1/approval/**")
                                                .filters(f -> f
                                                                .filter(new LoggingFilter()))
                                                .uri(actionServiceUri))

                                .build();
        }
}