package com.friends.userservice.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Returns the account_id claim from the current JWT (this is the authserver account PK).
     */
    public static Long getCurrentAccountId() {
        Jwt jwt = getJwt();
        Object claim = jwt.getClaim("account_id");
        if (claim instanceof Long l) return l;
        if (claim instanceof Integer i) return i.longValue();
        if (claim instanceof Number n) return n.longValue();
        throw new IllegalStateException("account_id claim is missing or of unexpected type in JWT");
    }

    /**
     * Returns the email claim from the current JWT.
     */
    public static String getCurrentEmail() {
        return getJwt().getClaim("email");
    }

    /**
     * Returns the raw JWT principal.
     */
    public static Jwt getJwt() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT principal found in SecurityContext");
        }
        return jwt;
    }
}

