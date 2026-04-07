package com.friends.userservice.mapper;

import com.friends.userservice.dto.response.UserAssignmentResponse;
import com.friends.userservice.entity.UserAssignment;

public class UserAssignmentMapper {

    public static UserAssignmentResponse toResponse(UserAssignment assignment) {
        return UserAssignmentResponse.builder()
                .id(assignment.getId())
                .userId(assignment.getUser().getId())
                .username(assignment.getUser().getUsername())
                .teamId(assignment.getTeam() != null ? assignment.getTeam().getId() : null)
                .teamName(assignment.getTeam() != null ? assignment.getTeam().getName() : null)
                .role(assignment.getRole())
                .assignedBy(assignment.getAssignedBy())
                .assignedAt(assignment.getAssignedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}

