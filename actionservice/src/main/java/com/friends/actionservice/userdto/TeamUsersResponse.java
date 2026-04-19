package com.friends.actionservice.userdto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class TeamUsersResponse {
    private Long teamId;
    private String teamName;
    private String teamDescription;
    private List<UserRoleResponse> users;
}
