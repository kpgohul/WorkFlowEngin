package com.friends.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    @Size(max = 128, message = "Team name must not exceed 128 characters")
    private String name;

    @Size(max = 512, message = "Description must not exceed 512 characters")
    private String description;
}

