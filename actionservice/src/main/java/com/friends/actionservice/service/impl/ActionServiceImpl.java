package com.friends.actionservice.service.impl;

import com.friends.actionservice.actionsdto.ActionRequest;
import com.friends.actionservice.actionsdto.ActionResponse;
import com.friends.actionservice.actionsdto.actions.*;
import com.friends.actionservice.appconstant.ActionType;
import com.friends.actionservice.appconstant.ApprovalStatus;
import com.friends.actionservice.appconstant.ApprovalType;
import com.friends.actionservice.path.ResourcePath;
import com.friends.actionservice.service.ActionService;
import com.friends.actionservice.service.UserServiceClient;
import com.friends.actionservice.userdto.UserContact;
import com.friends.actionservice.entity.ApprovalRequest;
import com.friends.actionservice.entity.ExecutionAction;
import com.friends.actionservice.kafka.KafkaActionResultProducer;
import com.friends.actionservice.repo.ApprovalRequestRepository;
import com.friends.actionservice.repo.ExecutionActionRepository;
import com.friends.actionservice.util.JsonUtils;
import com.friends.actionservice.util.MailSenderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {

    private final ExecutionActionRepository executionActionRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final KafkaActionResultProducer resultProducer;
    private final MailSenderUtil mailSenderUtil;
    private final UserServiceClient userServiceClient;
    private final WebClient webClient;
    private final JsonUtils jsonUtils;

    @Value("${app.mail.approval-base-url:http://localhost:8080/api/approval/respond}")
    private String approvalBaseUrl;

    @Override
    public Mono<Void> handleActionProcess(Long executionId, Long executionStepId, ActionRequest action) {
        if (executionId == null || executionStepId == null) {
            return Mono.error(new IllegalArgumentException("Missing Kafka headers executionId/executionStepId"));
        }
        if (action == null || action.getActionType() == null) {
            return Mono.error(new IllegalArgumentException("Invalid action payload"));
        }

        return switch (action.getActionType()) {
            case APPROVAL -> handleApprovalAction(executionId, executionStepId, (ApprovalAction) action);
            case AUTO_APPROVAL -> handleAutoApprovalAction(executionId, executionStepId, (AutoApprovalAction) action);
            case NOTIFICATION -> handleNotificationAction(executionId, executionStepId, (NotificationAction) action);
            case WEBHOOK -> handleWebHookAction(executionId, executionStepId, (WebHookAction) action);
            case DELAY -> handleDelayAction(executionId, executionStepId, (DelayAction) action);
            case TASK -> handleTaskAction(executionId, executionStepId, (TaskAction) action);
        };
    }

    @Override
    public Mono<Void> handleActionResult(ActionResponse response) {
        // For now, actionservice is the producer of ActionResponse for executionservice.
        // Keeping this method allows future consumption/handling if you add retries/monitoring.
        return resultProducer.send(response);
    }

    private Mono<Void> handleApprovalAction(Long executionId, Long executionStepId, ApprovalAction approvalAction) {
        Instant now = Instant.now();

        // Per requirement:
        // - ANY: use teamId + roleId only
        // - ANY_ONE (specific): use approverId only
        ApprovalType approvalType = approvalAction.getApprovalType();
        if (approvalType == null) {
            return Mono.error(new IllegalArgumentException("approvalType is required"));
        }

        Map<String, Object> meta = new HashMap<>();
        meta.put("name", approvalAction.getName());
        meta.put("approvalType", approvalType);
        meta.put("channel", approvalAction.getChannel());
        if (approvalType == ApprovalType.ANY) {
            meta.put("approverRoleId", approvalAction.getApproverRoleId());
            meta.put("teamId", approvalAction.getTeamId());
        } else {
            meta.put("approverId", approvalAction.getApproverId());
        }

        ExecutionAction execAction = ExecutionAction.builder()
                .executionId(executionId)
                .executionStepId(executionStepId)
                .actionType(ActionType.APPROVAL)
                .isActive(true)
                .actionMeta(jsonUtils.toJson(meta))
                .initiatedAt(now)
                .completedAt(null)
                .build();

        String token = UUID.randomUUID().toString();

        return executionActionRepository.save(execAction)
                .flatMap(saved -> {
                    ApprovalRequest approvalRequest = ApprovalRequest.builder()
                            .executionActionId(saved.getId())
                            .token(token)
                            .status(ApprovalStatus.PENDING)
                            .isApproved(null)
                            .approverId(approvalType == ApprovalType.ANY_ONE ? approvalAction.getApproverId() : null)
                            .createdAt(now)
                            .respondedAt(null)
                            .build();

                    Mono<ApprovalRequest> createReq = approvalRequestRepository.save(approvalRequest);

                    Mono<Void> sendReq;
                    if (approvalType == ApprovalType.ANY) {
                        List<UserContact> users = userServiceClient.findUsersByRoleAndTeam(
                                approvalAction.getApproverRoleId(),
                                approvalAction.getTeamId()
                        );
                        sendReq = Mono.fromRunnable(() -> users.forEach(u -> sendApprovalMessage(executionId, executionStepId, approvalAction, token, u)));
                    } else {
                        UserContact user = userServiceClient.findUserById(approvalAction.getApproverId());
                        sendReq = Mono.fromRunnable(() -> sendApprovalMessage(executionId, executionStepId, approvalAction, token, user));
                    }

                    return createReq.then(sendReq);
                });
    }

    private void sendApprovalMessage(Long executionId, Long executionStepId, ApprovalAction action, String token, UserContact user) {
        if (user == null) {
            return;
        }
        String approveUrl = approvalBaseUrl + "?token=" + token + "&approval=accept" + "&approverId=" + user.getUserId();
        String rejectUrl = approvalBaseUrl + "?token=" + token + "&approval=reject" + "&approverId=" + user.getUserId();

        String subject = (action.getSubject() == null || action.getSubject().isBlank())
                ? "Approval required: " + safe(action.getName())
                : action.getSubject();

        String msg = (action.getBody() == null || action.getBody().isBlank())
                ? defaultApprovalBody(action)
                : action.getBody();

        Map<String, Object> vars = new HashMap<>();
        vars.put("actionName", safe(action.getName()));
        vars.put("approvalType", action.getApprovalType() != null ? action.getApprovalType().name() : "");
        vars.put("executionId", executionId);
        vars.put("executionStepId", executionStepId);
        vars.put("message", msg);
        vars.put("approveUrl", approveUrl);
        vars.put("rejectUrl", rejectUrl);

        String to = action.getChannel() == null || action.getChannel().name().equals("MAIL") ? user.getEmail() : user.getPhone();
        mailSenderUtil.send(action.getChannel(), to, subject, ResourcePath.MAIL_APPROVAL_REQUEST, vars);
    }

    private String defaultApprovalBody(ApprovalAction action) {
        String type = action.getApprovalType() == null ? "" : action.getApprovalType().name();
        return "Please review and respond. Approval type: " + type + ".";
    }

    private Mono<Void> handleAutoApprovalAction(Long executionId, Long executionStepId, AutoApprovalAction autoApprovalAction) {
        Instant now = Instant.now();
        ExecutionAction execAction = ExecutionAction.builder()
                .executionId(executionId)
                .executionStepId(executionStepId)
                .actionType(ActionType.AUTO_APPROVAL)
                .isActive(false)
                .actionMeta(jsonUtils.toJson(Map.of("name", autoApprovalAction.getName())))
                .initiatedAt(now)
                .completedAt(now)
                .build();

        ActionResponse response = ActionResponse.builder()
                .executionId(executionId)
                .executionStepId(executionStepId)
                .isSuccess(true)
                .message("Auto-approved")
                .error(null)
                .build();

        return executionActionRepository.save(execAction)
                .then(resultProducer.send(response));
    }

    private Mono<Void> handleNotificationAction(Long executionId, Long executionStepId, NotificationAction notificationAction) {
        Instant now = Instant.now();
        ExecutionAction execAction = ExecutionAction.builder()
                .executionId(executionId)
                .executionStepId(executionStepId)
                .actionType(ActionType.NOTIFICATION)
                .isActive(true)
                .actionMeta(jsonUtils.toJson(Map.of(
                        "name", notificationAction.getName(),
                        "channel", notificationAction.getChannel(),
                        "notifyTo", notificationAction.getNotifyTo()
                )))
                .initiatedAt(now)
                .completedAt(null)
                .build();

        return executionActionRepository.save(execAction)
                .flatMap(saved -> {
                    UserContact user = userServiceClient.findUserById(notificationAction.getNotifyTo());
                    String subject = (notificationAction.getSubject() == null || notificationAction.getSubject().isBlank())
                            ? "Notification: " + safe(notificationAction.getName())
                            : notificationAction.getSubject();
                    String message = (notificationAction.getBody() == null || notificationAction.getBody().isBlank())
                            ? "You have a new notification."
                            : notificationAction.getBody();

                    Map<String, Object> vars = new HashMap<>();
                    vars.put("title", safe(notificationAction.getName()));
                    vars.put("executionId", executionId);
                    vars.put("executionStepId", executionStepId);
                    vars.put("message", message);

                    String to = notificationAction.getChannel() == null || notificationAction.getChannel().name().equals("MAIL") ? user.getEmail() : user.getPhone();
                    return Mono.fromRunnable(() -> mailSenderUtil.send(notificationAction.getChannel(), to, subject, ResourcePath.MAIL_NOTIFICATION, vars))
                            .then(markCompletedAndSend(saved, true, "Notification sent", null));
                });
    }

    private Mono<Void> handleTaskAction(Long executionId, Long executionStepId, TaskAction taskAction) {
        Instant now = Instant.now();
        ExecutionAction execAction = ExecutionAction.builder()
                .executionId(executionId)
                .executionStepId(executionStepId)
                .actionType(ActionType.TASK)
                .isActive(false)
                .actionMeta(jsonUtils.toJson(Map.of("name", taskAction.getName())))
                .initiatedAt(now)
                .completedAt(now)
                .build();

        ActionResponse response = ActionResponse.builder()
                .executionId(executionId)
                .executionStepId(executionStepId)
                .isSuccess(true)
                .message("Task recorded")
                .error(null)
                .build();

        return executionActionRepository.save(execAction)
                .then(resultProducer.send(response));
    }

    private Mono<Void> handleWebHookAction(Long executionId, Long executionStepId, WebHookAction webHookAction) {
        Instant now = Instant.now();
        Map<String, Object> meta = new HashMap<>();
        meta.put("name", webHookAction.getName());
        meta.put("uri", webHookAction.getUri() != null ? webHookAction.getUri().toString() : "");
        meta.put("method", webHookAction.getMethod());

        ExecutionAction execAction = ExecutionAction.builder()
                .executionId(executionId)
                .executionStepId(executionStepId)
                .actionType(ActionType.WEBHOOK)
                .isActive(true)
                .actionMeta(jsonUtils.toJson(meta))
                .initiatedAt(now)
                .completedAt(null)
                .build();

        return executionActionRepository.save(execAction)
                .flatMap(saved -> {
                    HttpMethod method = webHookAction.getMethod() == null ? HttpMethod.POST : webHookAction.getMethod();
                    WebClient.RequestBodySpec spec = webClient.method(method).uri(webHookAction.getUri());

                    if (webHookAction.getHeader() != null) {
                        webHookAction.getHeader().forEach((k, v) -> spec.header(k, String.valueOf(v)));
                    }

                    Mono<String> call = spec.bodyValue(webHookAction.getBody() == null ? "" : webHookAction.getBody())
                            .retrieve()
                            .bodyToMono(String.class);

                    return call
                            .flatMap(body -> markCompletedAndSend(saved, true, "Webhook triggered", null))
                            .onErrorResume(ex -> markCompletedAndSend(saved, false, null, ex.getMessage()));
                });
    }

    private Mono<Void> handleDelayAction(Long executionId, Long executionStepId, DelayAction delayAction) {
        Instant now = Instant.now();
        long ms = delayAction.getDelayDurationInMillis() == null ? 0L : delayAction.getDelayDurationInMillis();

        ExecutionAction execAction = ExecutionAction.builder()
                .executionId(executionId)
                .executionStepId(executionStepId)
                .actionType(ActionType.DELAY)
                .isActive(true)
                .actionMeta(jsonUtils.toJson(Map.of(
                        "name", delayAction.getName(),
                        "delayDurationInMillis", ms
                )))
                .initiatedAt(now)
                .completedAt(null)
                .build();

        return executionActionRepository.save(execAction)
                .flatMap(saved -> Mono.delay(Duration.ofMillis(Math.max(0L, ms)))
                        .then(markCompletedAndSend(saved, true, "Delay completed", null))
                );
    }

    private Mono<Void> markCompletedAndSend(ExecutionAction action, boolean success, String message, String error) {
        Instant now = Instant.now();
        action.setIsActive(false);
        action.setCompletedAt(now);

        ActionResponse response = ActionResponse.builder()
                .executionId(action.getExecutionId())
                .executionStepId(action.getExecutionStepId())
                .isSuccess(success)
                .message(message)
                .error(error)
                .build();

        return executionActionRepository.save(action)
                .then(resultProducer.send(response));
    }

    private String safe(String val) {
        return val == null ? "" : val;
    }
}
