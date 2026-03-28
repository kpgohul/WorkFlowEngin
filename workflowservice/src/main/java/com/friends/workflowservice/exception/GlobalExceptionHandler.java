package com.friends.workflowservice.exception;

import com.friends.workflowservice.dto.error.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    @SuppressWarnings("unused")
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            ServerWebExchange exchange) {

        log.warn("Resource not found at {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());

        ErrorResponseDto dto = new ErrorResponseDto(
                exchange.getRequest().getPath().value(),
                HttpStatus.NOT_FOUND.name(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto));
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(ResourceAlreadyExistException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleResourceAlreadyExistException(
            ResourceAlreadyExistException ex,
            ServerWebExchange exchange) {

        log.warn("Resource conflict at {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());

        ErrorResponseDto dto = new ErrorResponseDto(
                exchange.getRequest().getPath().value(),
                HttpStatus.CONFLICT.name(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(dto));
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleValidationException(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {

        String errorMsg = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Validation error at {}: {}", exchange.getRequest().getPath().value(), errorMsg);

        ErrorResponseDto dto = new ErrorResponseDto(
                exchange.getRequest().getPath().value(),
                HttpStatus.BAD_REQUEST.name(),
                errorMsg,
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity.badRequest().body(dto));
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            ServerWebExchange exchange) {

        log.warn("Bad request at {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());

        ErrorResponseDto dto = new ErrorResponseDto(
                exchange.getRequest().getPath().value(),
                HttpStatus.BAD_REQUEST.name(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity.badRequest().body(dto));
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleGlobalException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Unhandled exception at {}", exchange.getRequest().getPath().value(), ex);

        ErrorResponseDto dto = new ErrorResponseDto(
                exchange.getRequest().getPath().value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto));
    }
}