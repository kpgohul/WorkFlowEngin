package com.friends.workflowservice.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseEntity {

    private Instant createdAt;
    private Instant updatedAt;

}