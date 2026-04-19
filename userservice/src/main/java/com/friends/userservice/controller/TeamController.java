package com.friends.userservice.controller;

import com.friends.userservice.dto.common.PagedResponse;
import com.friends.userservice.dto.request.AssignUserRequest;
import com.friends.userservice.dto.request.CreateTeamRequest;
import com.friends.userservice.dto.response.TeamResponse;
import com.friends.userservice.dto.response.UserAssignmentResponse;
import com.friends.userservice.path.ApiRoutes;
import com.friends.userservice.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiRoutes.BASE_TEAMS)
// Resolves to /api/v1/teams
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
//    private final UserService userService;

    /**
     * POST /teams — create a new team (creator becomes the owner)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
//        Long accountId = SecurityUtils.getCurrentAccountId();
//        Long creatorUserId = userService.getUserByAccountId(accountId).getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.createTeam(request));
    }

    /**
     * GET /teams/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    /**
     * GET /teams?page=1&size=10
     */
    @GetMapping
    public ResponseEntity<PagedResponse<TeamResponse>> getAllTeams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page <= 0 || size <= 0) throw new IllegalArgumentException("Page must be >= 1 and size must be > 0");
        return ResponseEntity.ok(teamService.getAllTeams(page - 1, size));
    }

    /**
     * POST /teams/assign — assign a user to a role (optionally a team)
     */
    @PostMapping("/assign")
    public ResponseEntity<UserAssignmentResponse> assignUser(@Valid @RequestBody AssignUserRequest request) {
//        Long accountId = SecurityUtils.getCurrentAccountId();
        return ResponseEntity.ok(teamService.assignUser(request));
    }

    /**
     * DELETE /teams/remove/{userId} — remove a user's role assignment
     */
    @DeleteMapping("/remove/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeUserFromTeam(@PathVariable Long accountId) {
        teamService.removeUserFromTeam(accountId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /teams/{teamId}/members?page=1&size=10
     */
    @GetMapping("/{teamId}/members")
    public ResponseEntity<PagedResponse<UserAssignmentResponse>> getUsersInTeam(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page <= 0 || size <= 0) throw new IllegalArgumentException("Page must be >= 1 and size must be > 0");
        return ResponseEntity.ok(teamService.getUsersInTeam(teamId, page - 1, size));
    }

    /**
     * GET /teams/assignment/{userId} — get a specific user's assignment
     */
    @GetMapping("/assignment/{accountId}")
    public ResponseEntity<UserAssignmentResponse> getAssignmentByUserId(@PathVariable Long accountId) {
        return ResponseEntity.ok(teamService.getAssignmentByAccountId(accountId));
    }

    /**
     * GET /teams/users?teamId=1&roleId=2
     * Get a list of users based on teamId and roleId.
     * At least one parameter must be provided.
     */
    @GetMapping("/users")
    public ResponseEntity<com.friends.userservice.dto.response.TeamUsersResponse> getUsersByTeamAndRole(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Integer roleId) {
        if (teamId == null && roleId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(teamService.getTeamUsers(teamId, roleId));
    }
}

