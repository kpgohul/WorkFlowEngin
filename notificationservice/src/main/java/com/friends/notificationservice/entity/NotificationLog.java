package com.friends.notificationservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("notification_logs")
public class NotificationLog {

    @Id
    private UUID id;
    private UUID executionId;
    private String eventType;
    private String recipient;
    private String message;
    private String status;
    private Instant createdAt;
}