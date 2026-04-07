package com.friends.authserver.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {

    private String apiPath;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime errorTime;

}