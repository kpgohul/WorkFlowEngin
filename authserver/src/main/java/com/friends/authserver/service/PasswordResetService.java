package com.friends.authserver.service;

public interface PasswordResetService {

    public void createPasswordResetToken(String email);
    public void resetPassword(String token, String newPassword);
    public boolean validateToken(String token);
    public void cleanupExpiredTokens();
}