package com.friends.actionservice.service;

import com.friends.actionservice.userdto.MockUserContact;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MockUserDirectory {

    public List<MockUserContact> findUsersByRoleAndTeam(Integer roleId, Integer teamId) {
        int count = ThreadLocalRandom.current().nextInt(3, 7);
        List<MockUserContact> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            long id = ThreadLocalRandom.current().nextLong(1000, 10000);
            users.add(MockUserContact.builder()
                    .userId(id)
                    .roleId(roleId)
                    .teamId(teamId)
                    .email("user" + id + "@example.com")
                    .phone("+1555" + ThreadLocalRandom.current().nextInt(1000000, 9999999))
                    .build());
        }
        return users;
    }

    public MockUserContact findUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return MockUserContact.builder()
                .userId(userId)
                .email("user" + userId + "@example.com")
                .phone("+1555" + ThreadLocalRandom.current().nextInt(1000000, 9999999))
                .build();
    }
}

