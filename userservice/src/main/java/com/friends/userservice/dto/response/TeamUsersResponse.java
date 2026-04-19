package com.friends.userservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamUsersResponse {
    private Long teamId;
    private String teamName;
    private String teamDescription;
    private List<UserRoleResponse> users;
}
