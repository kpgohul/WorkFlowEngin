package com.friends.userservice.service.impl;

import com.friends.userservice.appconstant.Role;
import com.friends.userservice.dto.common.PagedResponse;
import com.friends.userservice.dto.request.AssignUserRequest;
import com.friends.userservice.dto.request.CreateTeamRequest;
import com.friends.userservice.dto.response.TeamResponse;
import com.friends.userservice.dto.response.UserAssignmentResponse;
import com.friends.userservice.entity.Team;
import com.friends.userservice.entity.User;
import com.friends.userservice.entity.UserAssignment;
import com.friends.userservice.exception.ResourceAlreadyExistException;
import com.friends.userservice.exception.ResourceNotFoundException;
import com.friends.userservice.mapper.TeamMapper;
import com.friends.userservice.mapper.UserAssignmentMapper;
import com.friends.userservice.repo.TeamRepo;
import com.friends.userservice.repo.UserAssignmentRepo;
import com.friends.userservice.repo.UserRepo;
import com.friends.userservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

    private final TeamRepo teamRepo;
    private final UserRepo userRepo;
    private final UserAssignmentRepo userAssignmentRepo;

    // ─── TEAM CRUD ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TeamResponse createTeam(CreateTeamRequest request, Long creatorUserId) {
        if (teamRepo.existsByName(request.getName())) {
            throw new ResourceAlreadyExistException("Team", "name", request.getName());
        }
        Team team = TeamMapper.toEntity(request, creatorUserId);
        return TeamMapper.toResponse(teamRepo.save(team));
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long id) {
        return TeamMapper.toResponse(findTeamById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TeamResponse> getAllTeams(int page, int size) {
        Page<Team> teamPage = teamRepo.findAll(PageRequest.of(page, size));
        List<TeamResponse> content = teamPage.getContent().stream()
                .map(TeamMapper::toResponse)
                .toList();
        return PagedResponse.<TeamResponse>builder()
                .content(content)
                .totalElements(teamPage.getTotalElements())
                .page(page + 1)
                .size(size)
                .totalPages(teamPage.getTotalPages())
                .build();
    }

    // ─── ASSIGNMENT ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public UserAssignmentResponse assignUser(AssignUserRequest request, Long assignedByUserId) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId().toString()));

        // Rule 1: Each user can only have one assignment
        if (userAssignmentRepo.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("User already has a role assignment. Remove existing assignment first.");
        }

        Role role = request.getRole();
        Team team = null;

        // Rule 3 & 4: Team-based vs Global roles
        boolean requiresTeam = (role == Role.FRESHER || role == Role.EMPLOYEE || role == Role.MANAGER);
        boolean mustBeGlobal  = (role == Role.HEAD    || role == Role.SUPER_HEAD);

        if (requiresTeam) {
            if (request.getTeamId() == null) {
                throw new IllegalArgumentException("Role " + role + " requires a team assignment.");
            }
            team = findTeamById(request.getTeamId());
        }

        if (mustBeGlobal && request.getTeamId() != null) {
            throw new IllegalArgumentException("Role " + role + " must NOT be assigned to a team.");
        }

        // Rule 6: Only one MANAGER per team
        if (role == Role.MANAGER && userAssignmentRepo.existsByTeamIdAndRole(request.getTeamId(), Role.MANAGER)) {
            throw new IllegalArgumentException("Team already has a MANAGER. Only one MANAGER per team is allowed.");
        }

        // Rule 8: Only one SUPER_HEAD in system
        if (role == Role.SUPER_HEAD && userAssignmentRepo.existsByRole(Role.SUPER_HEAD)) {
            throw new IllegalArgumentException("A SUPER_HEAD already exists in the system. Only one SUPER_HEAD is allowed.");
        }

        UserAssignment assignment = UserAssignment.builder()
                .user(user)
                .team(team)
                .role(role)
                .assignedBy(assignedByUserId)
                .build();

        return UserAssignmentMapper.toResponse(userAssignmentRepo.save(assignment));
    }

    @Override
    @Transactional
    public void removeUserFromTeam(Long userId) {
        UserAssignment assignment = userAssignmentRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAssignment", "userId", userId.toString()));
        userAssignmentRepo.delete(assignment);
        log.info("Removed assignment for userId={}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserAssignmentResponse> getUsersInTeam(Long teamId, int page, int size) {
        findTeamById(teamId); // validate team exists
        Page<UserAssignment> assignmentPage = userAssignmentRepo.findAllByTeamId(teamId, PageRequest.of(page, size));
        List<UserAssignmentResponse> content = assignmentPage.getContent().stream()
                .map(UserAssignmentMapper::toResponse)
                .toList();
        return PagedResponse.<UserAssignmentResponse>builder()
                .content(content)
                .totalElements(assignmentPage.getTotalElements())
                .page(page + 1)
                .size(size)
                .totalPages(assignmentPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserAssignmentResponse getAssignmentByUserId(Long userId) {
        UserAssignment assignment = userAssignmentRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAssignment", "userId", userId.toString()));
        return UserAssignmentMapper.toResponse(assignment);
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private Team findTeamById(Long id) {
        return teamRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id.toString()));
    }
}

