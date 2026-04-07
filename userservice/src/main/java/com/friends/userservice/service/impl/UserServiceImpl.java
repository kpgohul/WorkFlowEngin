package com.friends.userservice.service.impl;

import com.friends.userservice.dto.common.PagedResponse;
import com.friends.userservice.dto.event.UserRegisteredEvent;
import com.friends.userservice.dto.request.UpdateUserRequest;
import com.friends.userservice.dto.response.UserResponse;
import com.friends.userservice.entity.User;
import com.friends.userservice.exception.ResourceNotFoundException;
import com.friends.userservice.mapper.UserMapper;
import com.friends.userservice.repo.UserRepo;
import com.friends.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Override
//    @Transactional
    public void createUserFromEvent(UserRegisteredEvent event) {
        if (userRepo.existsByAccountId(event.accountId())) {
            log.warn("User with accountId={} already exists, skipping creation.", event.accountId());
            return;
        }
        User user = UserMapper.fromEvent(event);
        userRepo.save(user);
        log.info("User created from Kafka event: accountId={}, username={}", event.accountId(), event.username());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByAccountId(Long accountId) {
        User user = userRepo.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "accountId", accountId.toString()));
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(int page, int size) {
        Page<User> userPage = userRepo.findAll(PageRequest.of(page, size));
        List<UserResponse> content = userPage.getContent().stream()
                .map(UserMapper::toResponse)
                .toList();
        return PagedResponse.<UserResponse>builder()
                .content(content)
                .totalElements(userPage.getTotalElements())
                .page(page + 1)
                .size(size)
                .totalPages(userPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
        User updated = UserMapper.mergeForUpdate(request, user);
        return UserMapper.toResponse(userRepo.save(updated));
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
        user.setIsActive(false);
        userRepo.save(user);
        log.info("User deactivated: id={}", id);
    }
}

