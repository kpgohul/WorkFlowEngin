package com.friends.authserver.repo;

import com.friends.authserver.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.authorities WHERE a.username = :username")
    Optional<Account> findByUsername(@Param("username") String username);

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.authorities WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}