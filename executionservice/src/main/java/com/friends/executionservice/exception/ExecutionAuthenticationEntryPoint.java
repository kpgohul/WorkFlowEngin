package com.friends.executionservice.exception;

import com.friends.executionservice.dto.error.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ExecutionAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final JsonMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ErrorResponseDto dto = new ErrorResponseDto(
                exchange.getRequest().getPath().value(),
                HttpStatus.UNAUTHORIZED.name(),
                "Unauthorized: You need to login first or provide a valid token",
                Instant.now()
        );
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(dto);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
