package com.friends.userservice.service;

import com.friends.userservice.dto.common.PagedResponse;
import com.friends.userservice.dto.request.AssignUserRequest;
import com.friends.userservice.dto.request.CreateTeamRequest;
import com.friends.userservice.dto.response.TeamResponse;
import com.friends.userservice.dto.response.UserAssignmentResponse;

public interface TeamService {

    TeamResponse createTeam(CreateTeamRequest request, Long creatorUserId);

    TeamResponse getTeamById(Long id);

    PagedResponse<TeamResponse> getAllTeams(int page, int size);

    UserAssignmentResponse assignUser(AssignUserRequest request, Long assignedByUserId);

    void removeUserFromTeam(Long userId);

    PagedResponse<UserAssignmentResponse> getUsersInTeam(Long teamId, int page, int size);

    UserAssignmentResponse getAssignmentByUserId(Long userId);
}

