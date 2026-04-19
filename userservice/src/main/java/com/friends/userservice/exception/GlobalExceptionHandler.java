package com.friends.userservice.exception;

import com.friends.userservice.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(request.getRequestURI(), HttpStatus.NOT_FOUND.name(),
                        ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceAlreadyExistException(
            ResourceAlreadyExistException ex, HttpServletRequest request) {
        log.warn("Resource conflict at {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(request.getRequestURI(), HttpStatus.CONFLICT.name(),
                        ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMsg = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Validation error at {}: {}", request.getRequestURI(), errorMsg);
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto(request.getRequestURI(), HttpStatus.BAD_REQUEST.name(),
                        errorMsg, LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad request at {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto(request.getRequestURI(), HttpStatus.BAD_REQUEST.name(),
                        ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponseDto(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.name(),
                        "An unexpected error occurred", LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleEnumError(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            if (ife.getTargetType().isEnum()) {
                String fieldName = ife.getPath().get(0).getFieldName();
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid enum value");
                errorResponse.put("field", fieldName);
                if(ife.getTargetType().isEnum()){
                    errorResponse.put("allowedValues", ife.getTargetType().getEnumConstants());
                }
                errorResponse.put("message", "Invalid value for '" + fieldName + "'");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }

        Map<String, Object> defaultError = new HashMap<>();
        defaultError.put("error", "Invalid request");
        defaultError.put("message", "Malformed JSON or invalid data");
        return ResponseEntity.badRequest().body(defaultError);
    }
}

