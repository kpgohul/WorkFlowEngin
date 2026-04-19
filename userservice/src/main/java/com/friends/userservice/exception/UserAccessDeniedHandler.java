package com.friends.userservice.exception;

import com.friends.userservice.dto.error.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.time.LocalDateTime;

public class UserAccessDeniedHandler implements AccessDeniedHandler {

    private final JsonMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String errMsg = (accessDeniedException != null && accessDeniedException.getMessage() != null) ? accessDeniedException.getMessage() : "Forbidden Access!";
        String path = request.getRequestURI();
        LocalDateTime dateTime = LocalDateTime.now();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setHeader("forbidden-error", "failed due to lack of permission");
        response.setContentType("application/json;charset=UTF-8");
        ErrorResponseDto dto = new ErrorResponseDto(
                path,
                String.valueOf(HttpStatus.FORBIDDEN.value()),
                errMsg,
                dateTime
        );
        response.getWriter().write(mapper.writeValueAsString(dto));

    }
}