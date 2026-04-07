package com.friends.authserver.service;

public interface EmailService {

    public void sendPasswordResetEmail(String to, String token);
    public void sendAccountDeletionConfirmation(String to, String username);

}