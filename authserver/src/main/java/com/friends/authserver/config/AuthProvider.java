package com.friends.authserver.config;

import com.friends.authserver.util.accountutil.AccountStateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider {

    private final AccountDetailsService service;
    private final PasswordEncoder encoder;
    private final AccountStateValidator accountStateValidator;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String usernameOrEmail = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        UserDetails accountDetails = service.loadUserByUsername(usernameOrEmail);
        accountStateValidator.validateForAuthentication(accountDetails);

        if (!encoder.matches(rawPassword, accountDetails.getPassword())) {
            throw new BadCredentialsException("Account details are incorrect.");
        }

        return new UsernamePasswordAuthenticationToken(accountDetails, null, accountDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}