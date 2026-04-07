package com.friends.userservice.repo;

import com.friends.userservice.appconstant.Role;
import com.friends.userservice.entity.UserAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAssignmentRepo extends JpaRepository<UserAssignment, Long> {

    boolean existsByUserId(Long userId);

    boolean existsByTeamIdAndRole(Long teamId, Role role);

    boolean existsByRole(Role role);

    Optional<UserAssignment> findByUserId(Long userId);

    Page<UserAssignment> findAllByTeamId(Long teamId, Pageable pageable);
}

