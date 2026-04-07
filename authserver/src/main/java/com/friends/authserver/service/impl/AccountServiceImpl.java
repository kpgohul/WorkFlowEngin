package com.friends.authserver.service.impl;

import com.friends.authserver.dto.AccountCreateRequest;
import com.friends.authserver.dto.UserRegisteredEvent;
import com.friends.authserver.entity.Account;
import com.friends.authserver.entity.Role;
import com.friends.authserver.exception.ResourceNotFoundException;
import com.friends.authserver.kafka.UserRegistrationProducer;
import com.friends.authserver.repo.AccountRepo;
import com.friends.authserver.repo.PasswordResetTokenRepository;
import com.friends.authserver.repo.RoleRepo;
import com.friends.authserver.service.AccountService;
import com.friends.authserver.service.EmailService;
import com.friends.authserver.util.accountutil.AccountStateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepository;
    private final RoleRepo roleRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AccountStateValidator accountStateValidator;
    private final UserRegistrationProducer userRegistrationProducer;

    @Override
    @Transactional
    public void registerUser(AccountCreateRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Default role USER not found."));

        Account newAccount = Account.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isExpired(false)
                .isLocked(false)
                .isCredentialsExpired(false)
                .isEnabled(true)
                .authorities(Collections.singleton(defaultRole))
                .build();

        Account saved = accountRepository.save(newAccount);

        // Publish Kafka event AFTER transaction commits to avoid publishing on rollback
        UserRegisteredEvent event = new UserRegisteredEvent(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                Instant.now()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                userRegistrationProducer.publish(event);
            }
        });
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId, String currentPassword) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "AccountID", userId.toString()));

        accountStateValidator.validateForAccountFlow(account);

        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new IllegalArgumentException("Invalid password.");
        }

        deleteUserRelatedData(account);
    }

    @Override
    @Transactional
    public void deleteAccountByAdmin(Long userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "AccountID", userId.toString()));

        deleteUserRelatedData(account);
    }

    @Transactional
    private void deleteUserRelatedData(Account account) {
        tokenRepository.deleteByAccountId(account.getId());
        accountRepository.deleteById(account.getId());
        emailService.sendAccountDeletionConfirmation(account.getEmail(), account.getUsername());
    }
}

