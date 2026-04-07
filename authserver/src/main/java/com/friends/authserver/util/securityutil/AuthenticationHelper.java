package com.friends.authserver.util.securityutil;

import com.friends.authserver.entity.AccountDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for authentication-related operations.
 * Provides helper methods to check authentication status and resolve the current user.
 */
public final class AuthenticationHelper {

    private AuthenticationHelper() {
        // Utility class - prevent instantiation
    }

    /**
     * Checks if the current user is authenticated (not anonymous).
     * @return true if user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !isAnonymous(auth);
    }

    /**
     * Checks if the current authentication represents an anonymous user.
     * @param auth the authentication object
     * @return true if the user is anonymous, false otherwise
     */
    private static boolean isAnonymous(Authentication auth) {
        return "anonymousUser".equals(auth.getName()) ||
               auth.getAuthorities().stream()
                   .anyMatch(authority -> "ROLE_ANONYMOUS".equals(authority.getAuthority()));
    }

    /**
     * Gets the current authenticated username, or null if not authenticated.
     * @return the username or null
     */
    public static String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || isAnonymous(auth)) {
            return null;
        }
        return auth.getName();
    }

    /**
     * Gets the currently logged-in account id.
     * @return the account id for the authenticated user
     * @throws IllegalStateException if the security context does not contain an authenticated AccountDetails principal
     */
    public static Long getLoggedInAccountID() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AccountDetails userDetails)) {
            throw new IllegalStateException("Security context is not set up correctly for the current account.");
        }
        return userDetails.getId();
    }
}
