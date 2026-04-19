package com.friends.actionservice.userdto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRoleResponse {
    private Long id;
    private Long accountId;
    private String username;
    private Integer roleId;
    private String roleName;
    private String email;
    private String mobile;
}
