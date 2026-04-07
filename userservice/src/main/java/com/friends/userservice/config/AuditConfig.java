package com.friends.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        // Returns the current user's internal userId from the JWT (account_id claim)
        return () -> {
            try {
                var context = SecurityContextHolder.getContext();
                var authentication = context.getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                    return Optional.empty();
                }
                if (authentication.getPrincipal() instanceof Jwt jwt) {
                    Long accountId = jwt.getClaim("account_id");
                    return Optional.ofNullable(accountId);
                }
            } catch (Exception ignored) {
            }
            return Optional.empty();
        };
    }
}

