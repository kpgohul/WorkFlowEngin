package com.friends.actionservice.userdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserContact {
    private Long userId;
    private Integer roleId;
    private Integer teamId;
    private String email;
    private String phone;
}

