package com.friends.userservice.dto.request;

import com.friends.userservice.appconstant.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignUserRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    private Long teamId;

    @NotNull(message = "role is required")
    private Role role;
}

