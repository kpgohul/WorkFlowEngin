package com.friends.gatewayserver.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.channel.ConnectTimeoutException;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

/**
 * Global exception handler for the API Gateway.
 * Handles:
 *   - 404 No Route Found (e.g. /api/v1workflows instead of /api/v1/workflows)
 *   - 404 No Static Resource (gateway falls through to resource handler)
 *   - Connection Timeout (downstream service unreachable)
 *   - Response Timeout (downstream service too slow)
 *   - Service Unavailable (downstream not registered in Eureka)
 *   - Generic errors
 */
@Primary
@Order(-2)  // Must be lower than DefaultErrorWebExceptionHandler's @Order(-1) to take priority
@Component
public class GatewayGlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GatewayGlobalExceptionHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        String path = exchange.getRequest().getPath().value();

        HttpStatus status;
        String error;
        String message;

        // --- Connection Timeout (service is unreachable / refused) ---
        if (ex instanceof ConnectTimeoutException || isCausedBy(ex, ConnectTimeoutException.class)) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            error = "Gateway Timeout";
            message = "The upstream service is not reachable. Please try again later.";

        // --- Response Timeout (service took too long to respond) ---
        } else if (ex instanceof TimeoutException || isCausedBy(ex, TimeoutException.class)) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            error = "Gateway Timeout";
            message = "The upstream service did not respond in time. Please try again later.";

        // --- Service not registered in Eureka / load balancer has no instances ---
        } else if (isNoInstanceAvailable(ex)) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            error = "Service Unavailable";
            message = "The requested service is currently unavailable. It may not be registered yet.";

        // --- Premature connection close from downstream ---
        } else if (ex instanceof WebClientRequestException
                || isCausedBy(ex, WebClientRequestException.class)) {
            status = HttpStatus.BAD_GATEWAY;
            error = "Bad Gateway";
            message = "The upstream service closed the connection unexpectedly. Please try again.";

        // --- No Route Found (Spring Cloud Gateway specific) ---
        } else if (ex instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            error = "Not Found";
            message = "No gateway route found for: " + path + ". Please check the request path.";

        // --- No Static Resource (falls through Gateway route matching) ---
        } else if (ex instanceof NoResourceFoundException) {
            status = HttpStatus.NOT_FOUND;
            error = "Not Found";
            message = "No route matched for path: " + path + ". Please check the request path.";

        // --- Explicit HTTP status exceptions ---
        } else if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            error = status.getReasonPhrase();
            message = rse.getReason() != null ? rse.getReason() : ex.getMessage();

        // --- Fallback: 503 Service Unavailable ---
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            error = "Service Unavailable";
            message = "An unexpected error occurred at the gateway. Please try again later.";
        }

        return writeErrorResponse(exchange, response, path, status, error, message);
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange,
                                          ServerHttpResponse response,
                                          String path,
                                          HttpStatus status,
                                          String error,
                                          String message) {
        if (response.isCommitted()) {
            return Mono.error(new IllegalStateException("Response already committed"));
        }

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        GatewayErrorResponse body = GatewayErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .build();

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = ("{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Failed to serialize error response\"}")
                    .getBytes();
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Traverses the exception cause chain looking for a specific type.
     */
    private boolean isCausedBy(Throwable ex, Class<? extends Throwable> type) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (type.isInstance(cause)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    /**
     * Checks if the exception message indicates no Eureka instances are available
     * for the requested service (covers Spring Cloud LoadBalancer's IllegalStateException).
     */
    private boolean isNoInstanceAvailable(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            String msg = current.getMessage();
            if (msg != null && (msg.contains("No instances available") || msg.contains("Load balancer does not contain an instance"))) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
