package com.friends.userservice.mapper;

import com.friends.userservice.dto.request.CreateTeamRequest;
import com.friends.userservice.dto.response.TeamResponse;
import com.friends.userservice.entity.Team;

public class TeamMapper {

    public static Team toEntity(CreateTeamRequest request, Long creatorUserId) {
        return Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(creatorUserId)
                .build();
    }

    public static TeamResponse toResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .createdBy(team.getCreatedBy())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }
}

