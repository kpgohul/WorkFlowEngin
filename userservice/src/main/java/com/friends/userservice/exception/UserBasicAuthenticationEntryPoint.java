package com.friends.userservice.exception;

import com.friends.userservice.dto.error.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.time.LocalDateTime;

public class UserBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JsonMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String expMsg = (authException != null && authException.getMessage() != null)? authException.getMessage() : "Authentication Failed!";
        String path = request.getRequestURI();
        LocalDateTime dateTime = LocalDateTime.now();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("authorized-error", "Failed to Authenticate");
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        ErrorResponseDto dto = new ErrorResponseDto(
                path,
                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                expMsg,
                dateTime
        );
        response.getWriter().write(mapper.writeValueAsString(dto));

    }
}