package com.friends.executionservice.exception;

import com.friends.executionservice.dto.error.ErrorResponseDto;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ExecutionAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final JsonMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ErrorResponseDto dto = new ErrorResponseDto(
                exchange.getRequest().getPath().value(),
                HttpStatus.FORBIDDEN.name(),
                "Forbidden: You do not have permission to perform this action",
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
