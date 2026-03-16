package com.friends.notificationservice.repo;

import com.friends.notificationservice.entity.NotificationLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface NotificationRepository extends ReactiveCrudRepository<NotificationLog, UUID> {
}