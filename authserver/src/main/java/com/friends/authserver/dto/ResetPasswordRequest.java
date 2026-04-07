package com.friends.authserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 72, message = "New password must be between 8 and 72 characters")
    @Pattern(regexp = ".*\\d.*", message = "New password must contain at least one number")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}

