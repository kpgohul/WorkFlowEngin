package com.friends.userservice.service;

import com.friends.userservice.dto.common.PagedResponse;
import com.friends.userservice.dto.event.UserRegisteredEvent;
import com.friends.userservice.dto.request.UpdateUserRequest;
import com.friends.userservice.dto.response.UserResponse;

public interface UserService {

    void createUserFromEvent(UserRegisteredEvent event);

    UserResponse getUserById(Long id);

    UserResponse getUserByAccountId(Long accountId);

    PagedResponse<UserResponse> getAllUsers(int page, int size);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deactivateUser(Long id);
}

