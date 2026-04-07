package com.friends.authserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountCreateRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 32, message = "Username must be between 4 and 32 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, '.', '_' and '-'")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    @Pattern(
            regexp = ".*\\d.*",
            message = "Password must contain at least one number"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}