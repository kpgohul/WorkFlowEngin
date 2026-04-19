package com.friends.userservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class UserRoleResponse {
    private Long id;
    private Long accountId;
    private String username;
    private Integer roleId;
    private String roleName;
    private String email;
    private String gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private Integer age;
    private String countryCode;
    private String mobile;
    private String country;
    private String state;
    private String address;
    private String pincode;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
