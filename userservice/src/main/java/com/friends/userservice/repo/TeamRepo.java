package com.friends.userservice.repo;

import com.friends.userservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepo extends JpaRepository<Team, Long> {

    boolean existsByName(String name);

    Optional<Team> findByName(String name);
}

