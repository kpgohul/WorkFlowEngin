package com.friends.userservice.controller;

import com.friends.userservice.dto.common.PagedResponse;
import com.friends.userservice.dto.request.UpdateUserRequest;
import com.friends.userservice.dto.response.UserResponse;
import com.friends.userservice.path.ApiRoutes;
import com.friends.userservice.service.UserService;
import com.friends.userservice.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiRoutes.BASE_USERS)
// Resolves to /api/v1/users
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /users/me — returns the profile of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile() {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return ResponseEntity.ok(userService.getUserByAccountId(accountId));
    }

    /**
     * GET /users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * GET /users/account/{accountId}
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<UserResponse> getUserByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(userService.getUserByAccountId(accountId));
    }

    /**
     * GET /users?page=1&size=10
     */
    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page <= 0 || size <= 0) throw new IllegalArgumentException("Page must be >= 1 and size must be > 0");
        return ResponseEntity.ok(userService.getAllUsers(page - 1, size));
    }

    /**
     * PUT /users/{id} — update profile fields (only the owner or admin should call this)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * PATCH /users/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}

