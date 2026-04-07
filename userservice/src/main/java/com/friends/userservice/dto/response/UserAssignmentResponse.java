package com.friends.userservice.dto.response;

import com.friends.userservice.appconstant.Role;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserAssignmentResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long teamId;
    private String teamName;
    private Role role;
    private Long assignedBy;
    private Instant assignedAt;
    private Instant updatedAt;
}

