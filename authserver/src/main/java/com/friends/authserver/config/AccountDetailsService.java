package com.friends.authserver.config;

import com.friends.authserver.entity.Account;
import com.friends.authserver.entity.AccountDetails;
import com.friends.authserver.repo.AccountRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepo repo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(@NonNull String usernameOrEmail) throws UsernameNotFoundException {
        Account account = repo.findByUsername(usernameOrEmail)
                .or(() -> repo.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Account not found with the given details: " + usernameOrEmail
                ));

        var authorities = account.getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        return AccountDetails.builder()
                .id(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .isExpired(account.getIsExpired())
                .isLocked(account.getIsLocked())
                .isEnabled(account.getIsEnabled())
                .password(account.getPassword())
                .isCredentialsExpired(account.getIsCredentialsExpired())
                .authorities(authorities)
                .build();

    }
}