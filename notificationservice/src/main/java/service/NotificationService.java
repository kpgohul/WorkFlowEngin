//package service;
//
//import com.friends.notificationservice.dto.WorkflowEventDTO;
//import com.friends.notificationservice.entity.NotificationLog;
//import com.friends.notificationservice.repo.NotificationRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import java.time.Instant;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationService {
//
//    private final NotificationRepository repository;
//    private final EmailService emailService;
//    private final SlackService slackService;
//    private final R2dbcEntityTemplate entityTemplate;
//
//    public Mono<Void> handleNotification(WorkflowEventDTO event) {
//
//        String message = "Workflow Step " + event.getStep() + " status: " + event.getStatus();
//
//        emailService.sendEmail("manager@example.com", message);
//        slackService.sendSlackMessage(message);
//
//        NotificationLog notificationLog = NotificationLog.builder()
//                .id(UUID.randomUUID())
//                .executionId(event.getExecutionId())
//                .eventType(event.getStatus())
//                .recipient("manager@example.com")
//                .message(message)
//                .status("SENT")
//                .createdAt(Instant.now())
//                .build();
//
//        return entityTemplate.insert(NotificationLog.class).using(notificationLog)
//                .doOnNext(saved -> log.info("NotificationLog inserted: {}", saved.getId()))
//                .doOnError(err -> log.error("Error saving notification log for execution {}", event.getExecutionId(), err))
//                .then();
//    }
//}
//
//        String message = "Workflow Step " + event.getStep() + " status: " + event.getStatus();
//
//        emailService.sendEmail("manager@example.com", message);
//        slackService.sendSlackMessage(message);
//
//        NotificationLog notificationLog = NotificationLog.builder()
//                .id(UUID.randomUUID())
//                .executionId(event.getExecutionId())
//                .eventType(event.getStatus())
//                .recipient("manager@example.com")
//                .message(message)
//                .status("SENT")
//                .createdAt(Instant.now())
//                .build();
//
//        return entityTemplate.insert(NotificationLog.class).using(notificationLog)
//                .doOnNext(saved -> log.info("NotificationLog inserted: {}", saved.getId()))
//                .doOnError(err -> log.error("Error saving notification log for execution {}", event.getExecutionId(), err))
//                .then();
//    }
//}
