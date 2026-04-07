package com.friends.userservice.mapper;

import com.friends.userservice.dto.event.UserRegisteredEvent;
import com.friends.userservice.dto.request.UpdateUserRequest;
import com.friends.userservice.dto.response.UserResponse;
import com.friends.userservice.entity.User;

public class UserMapper {

    public static User fromEvent(UserRegisteredEvent event) {
        return User.builder()
                .accountId(event.accountId())
                .username(event.username())
                .email(event.email())
                .isActive(true)
                .build();
    }

    public static User mergeForUpdate(UpdateUserRequest request, User existing) {
        if (request.getGender() != null) existing.setGender(request.getGender());
        if (request.getDateOfBirth() != null) existing.setDateOfBirth(request.getDateOfBirth());
        if (request.getBloodGroup() != null) existing.setBloodGroup(request.getBloodGroup());
        if (request.getAge() != null) existing.setAge(request.getAge());
        if (request.getCountryCode() != null) existing.setCountryCode(request.getCountryCode());
        if (request.getMobile() != null) existing.setMobile(request.getMobile());
        if (request.getCountry() != null) existing.setCountry(request.getCountry());
        if (request.getState() != null) existing.setState(request.getState());
        if (request.getAddress() != null) existing.setAddress(request.getAddress());
        if (request.getPincode() != null) existing.setPincode(request.getPincode());
        return existing;
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .accountId(user.getAccountId())
                .username(user.getUsername())
                .email(user.getEmail())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .bloodGroup(user.getBloodGroup())
                .age(user.getAge())
                .countryCode(user.getCountryCode())
                .mobile(user.getMobile())
                .country(user.getCountry())
                .state(user.getState())
                .address(user.getAddress())
                .pincode(user.getPincode())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

