package com.friends.authserver.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class AccountDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final Boolean isExpired;
    private final Boolean isLocked;
    private final Boolean isCredentialsExpired;
    private final Boolean isEnabled;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return !Boolean.TRUE.equals(isExpired);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(isLocked);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !Boolean.TRUE.equals(isCredentialsExpired);
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isEnabled);
    }

}