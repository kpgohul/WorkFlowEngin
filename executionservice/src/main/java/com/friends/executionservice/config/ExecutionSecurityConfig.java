package com.friends.executionservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import com.friends.executionservice.exception.ExecutionAccessDeniedHandler;
import com.friends.executionservice.exception.ExecutionAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExecutionSecurityConfig {

    private final ExecutionAuthenticationEntryPoint authenticationEntryPoint;
    private final ExecutionAccessDeniedHandler accessDeniedHandler;
//
//    @Bean
//    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
//            ReactiveClientRegistrationRepository clientRegistrationRepository) {
//        var clientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
//        var manager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
//                clientRegistrationRepository, clientService);
//        manager.setAuthorizedClientProvider(
//                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
//                        .clientCredentials()
//                        .build());
//        return manager;
//    }

    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        var provider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        var manager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository,
                        authorizedClientService);

        manager.setAuthorizedClientProvider(provider);

        return manager;
    }

    @Bean
    public WebClient webClient(ReactiveOAuth2AuthorizedClientManager manager, @Value("${app.gateway-url}") String gatewayBaseUrl) {

        var oauth2 = new ServerOAuth2AuthorizedClientExchangeFilterFunction(manager);
        oauth2.setDefaultClientRegistrationId("executionservice");
        return WebClient.builder()
                .filter(oauth2)
                .baseUrl(gatewayBaseUrl)
                .build();
    }

    @Bean
    public ReactiveAuditorAware<Long> reactiveAuditorAware() {
        return () -> ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(authentication -> {
                    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                        Long userId = (Long) jwtAuth.getToken().getClaims().get("account_id");
                        if (userId != null) {
                            return userId;
                        }
                        Object clientId = jwtAuth.getToken().getClaims().get("client_id");
                        if (clientId != null) {
                            log.info("Service: '{}' extracted from the JWT Token", clientId);
                            return 0L;
                        }
                    }
                    log.info("JWT does't contain the oauth user (user or service)");
                    return 0L;
                });
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(reactiveJwtAuthenticationConverter()))
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter reactiveJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("authorities");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(
                new ReactiveJwtGrantedAuthoritiesConverterAdapter(authoritiesConverter)
        );
        return converter;
    }
}
