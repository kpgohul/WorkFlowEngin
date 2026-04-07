package com.friends.actionservice.service;

import com.friends.actionservice.userdto.UserContact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    @Value("${clients.userservice.base-url:http://localhost:9097}")
    private String userserviceBaseUrl;

    public UserServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<UserContact> findUsersByRoleAndTeam(Integer roleId, Integer teamId) {
        int count = ThreadLocalRandom.current().nextInt(3, 7);
        List<UserContact> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            long id = ThreadLocalRandom.current().nextLong(1000, 10000);
            users.add(UserContact.builder()
                    .userId(id)
                    .roleId(roleId)
                    .teamId(teamId)
                    .email("user" + id + "@example.com")
                    .phone("+1555" + ThreadLocalRandom.current().nextInt(1000000, 9999999))
                    .build());
        }
        return users;
    }

    public UserContact findUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return webClient.get()
                .uri(userserviceBaseUrl + "/users/" + userId)
                .retrieve()
                .bodyToMono(UserContact.class)
                .blockOptional()
                .orElse(null);
    }
}
