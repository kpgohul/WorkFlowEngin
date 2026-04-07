package com.friends.userservice.repo;

import com.friends.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    boolean existsByAccountId(Long accountId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByAccountId(Long accountId);

    Optional<User> findByEmail(String email);
}

