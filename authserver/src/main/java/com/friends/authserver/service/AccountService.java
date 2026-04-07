package com.friends.authserver.service;

import com.friends.authserver.dto.AccountCreateRequest;

public interface AccountService {

    void registerUser(AccountCreateRequest request);

    void deleteAccount(Long userId, String currentPassword);

    void deleteAccountByAdmin(Long userId);
}

