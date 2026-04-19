package com.friends.userservice.service;

import com.friends.userservice.dto.common.PagedResponse;
import com.friends.userservice.dto.request.AssignUserRequest;
import com.friends.userservice.dto.request.CreateTeamRequest;
import com.friends.userservice.dto.response.TeamResponse;
import com.friends.userservice.dto.response.UserAssignmentResponse;

public interface TeamService {

    TeamResponse createTeam(CreateTeamRequest request);

    TeamResponse getTeamById(Long id);

    PagedResponse<TeamResponse> getAllTeams(int page, int size);

    UserAssignmentResponse assignUser(AssignUserRequest request);

    void removeUserFromTeam(Long accountId);

    PagedResponse<UserAssignmentResponse> getUsersInTeam(Long teamId, int page, int size);

    UserAssignmentResponse getAssignmentByAccountId(Long accountId);

    com.friends.userservice.dto.response.TeamUsersResponse getTeamUsers(Long teamId, Integer roleId);
}

