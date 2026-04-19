package com.friends.gatewayserver.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("Request: " + exchange.getRequest().getURI());

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    System.out.println("Response Status: " + exchange.getResponse().getStatusCode());
                }));
    }
}