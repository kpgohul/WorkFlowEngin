package service;

import com.workflow.notification.dto.WorkflowEventDTO;
import com.workflow.notification.entity.NotificationLog;
import com.workflow.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    private final EmailService emailService;

    private final SlackService slackService;

    public void handleNotification(WorkflowEventDTO event) {

        String message = "Workflow Step " + event.getStep() + " status: " + event.getStatus();

        emailService.sendEmail("manager@example.com", message);

        slackService.sendSlackMessage(message);

        NotificationLog log = NotificationLog.builder()
                .id(UUID.randomUUID())
                .executionId(event.getExecutionId())
                .eventType(event.getStatus())
                .recipient("manager@example.com")
                .message(message)
                .status("SENT")
                .createdAt(Instant.now())
                .build();

        repository.save(log).subscribe();
    }
}