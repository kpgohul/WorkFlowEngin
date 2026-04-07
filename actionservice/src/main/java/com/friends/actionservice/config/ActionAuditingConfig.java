package com.friends.actionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class ActionAuditingConfig {

    @Bean
    public ReactiveAuditorAware<String> reactiveAuditorAware() {
        return () -> ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(authentication -> {
                    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                        Object userId = jwtAuth.getToken().getClaims().get("account_id");
                        if (userId != null) {
                            return "user:" + userId;
                        }
                        Object clientId = jwtAuth.getToken().getClaims().get("client_id");
                        if (clientId != null) {
                            return "client:" + clientId;
                        }
                    }
                    String name = authentication.getName();
                    return name != null ? name : "client:actionservice";
                })
                .defaultIfEmpty("client:actionservice");
    }
}
