package com.friends.workflowservice.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDto {

    private String apiPath;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime errorTime;

}