package com.friends.authserver.config;

import com.friends.authserver.entity.AccountDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private static final Logger log = LoggerFactory.getLogger(TokenCustomizer.class);

    @Override
    public void customize(JwtEncodingContext context) {
        var principal = context.getPrincipal();
        var tokenType = context.getTokenType();
        String tokenTypeValue = tokenType != null ? tokenType.getValue() : null;

        log.info("Customizing token: type={}, value={}, principalClass={}",
                tokenType, tokenTypeValue, principal != null ? principal.getClass().getName() : "null");

        // 1. Access token customization
        if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            addUserClaims(context, principal);
            addRoleClaims(context, principal);
            return;
        }

        // 2. ID token customization (OpenID Connect)
        if (OidcParameterNames.ID_TOKEN.equals(tokenTypeValue)) {
            addUserClaims(context, principal);
        }
    }

    private void addUserClaims(JwtEncodingContext context, Object principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken authToken) {
            Object userObj = authToken.getPrincipal();
            if (userObj instanceof AccountDetails userDetails) {
                context.getClaims().claim("account_id", userDetails.getId());
                context.getClaims().claim("email", userDetails.getEmail());
                log.info("Added account_id={} and email={} to token",
                        userDetails.getId(), userDetails.getEmail());
            } else {
                log.info("Principal inside UsernamePasswordAuthenticationToken is not AccountDetails: {}",
                        userObj != null ? userObj.getClass().getName() : "null");
            }
        } else {
            log.info("Principal is not UsernamePasswordAuthenticationToken: {}",
                    principal != null ? principal.getClass().getName() : "null");
        }
    }

    private void addRoleClaims(JwtEncodingContext context, Object principal) {
        if (principal == null || context.getPrincipal() == null) {
            return;
        }

        Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                .stream()
                .map(auth -> auth.replaceFirst("^ROLE_", ""))
                .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));

        context.getClaims().claim("roles", roles);
        log.info("Added roles={} to token", roles);
    }
}
