package com.friends.authserver.util.accountutil;

import com.friends.authserver.entity.Account;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AccountStateValidator {

    public void validateForAuthentication(UserDetails userDetails) {
        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("Account has expired.");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("Account is locked.");
        }
        if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Account credentials have expired.");
        }
        if (!userDetails.isEnabled()) {
            throw new DisabledException("Account is disabled.");
        }
    }

    public void validateForAccountFlow(Account account) {
        if (Boolean.TRUE.equals(account.getIsExpired())) {
            throw new IllegalStateException("Account is expired.");
        }
        if (Boolean.TRUE.equals(account.getIsLocked())) {
            throw new IllegalStateException("Account is locked.");
        }
        if (Boolean.TRUE.equals(account.getIsCredentialsExpired())) {
            throw new IllegalStateException("Account credentials are expired.");
        }
        if (!Boolean.TRUE.equals(account.getIsEnabled())) {
            throw new IllegalStateException("Account is disabled.");
        }
    }
}

