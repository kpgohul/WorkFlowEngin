package com.friends.authserver.service.impl;

import com.friends.authserver.entity.Account;
import com.friends.authserver.entity.PasswordResetToken;
import com.friends.authserver.exception.InvalidTokenException;
import com.friends.authserver.exception.ResourceNotFoundException;
import com.friends.authserver.repo.AccountRepo;
import com.friends.authserver.repo.PasswordResetTokenRepository;
import com.friends.authserver.service.EmailService;
import com.friends.authserver.service.PasswordResetService;
import com.friends.authserver.util.accountutil.AccountStateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final AccountRepo accountRepo;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final AccountStateValidator accountStateValidator;

    @Override
    @Transactional
    public void createPasswordResetToken(String email) {
        Account account = accountRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "Email", email));

        accountStateValidator.validateForAccountFlow(account);

        // Delete any existing tokens for this account
        tokenRepository.deleteByAccountId(account.getId());

        // Generate new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, account);
        tokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(email, token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (resetToken.isUsed()) {
            throw new InvalidTokenException("Token already used");
        }

        if (resetToken.isExpired()) {
            throw new InvalidTokenException("Token expired");
        }

        Account account = resetToken.getAccount();
        accountStateValidator.validateForAccountFlow(account);
        account.setPassword(encoder.encode(newPassword));
        accountRepo.save(account);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    @Override
    public boolean validateToken(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isUsed())
                .orElse(false);
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    @Override
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}