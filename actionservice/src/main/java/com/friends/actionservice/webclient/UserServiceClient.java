package com.friends.actionservice.webclient;

import com.friends.actionservice.userdto.UserRoleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    @Value("${app.gateway-url}")
    private String gatewayBaseUrl;

    public UserServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<UserRoleResponse> findUsersByRoleAndTeam(Integer roleId, Integer teamId) {
        if (roleId == null && teamId == null) {
            return new ArrayList<>();
        }

        try {
            com.friends.actionservice.userdto.TeamUsersResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(gatewayBaseUrl + "/teams/users")
                            .queryParamIfPresent("teamId", java.util.Optional.ofNullable(teamId))
                            .queryParamIfPresent("roleId", java.util.Optional.ofNullable(roleId))
                            .build())
                    .retrieve()
                    .bodyToMono(com.friends.actionservice.userdto.TeamUsersResponse.class)
                    .block();

            if (response != null && response.getUsers() != null) {
                return response.getUsers();
            }
        } catch (Exception e) {
            // Log error or handle exception
            System.err.println("Error fetching users by role and team: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    public UserRoleResponse findUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return webClient.get()
                .uri(gatewayBaseUrl + "/users/" + userId)
                .retrieve()
                .bodyToMono(UserRoleResponse.class)
                .blockOptional()
                .orElse(null);
    }
}
