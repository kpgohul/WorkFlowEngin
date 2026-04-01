package com.friends.workflowservice.dto.ruleconfig;

import com.friends.workflowservice.appconstant.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRuleConfig implements RuleConfig{
    @NotBlank(message = "Notification name required.")
    private String name;
    @NotNull(message = "Notification channel is required")
    private Channel channel;
    @NotNull(message = "NotifyTo user id required.")
    private Long notifyTo;
    @NotBlank(message = "Notification subject part is required.")
    private String subject;
    @NotBlank(message = "Notification body part is required.")
    private String body;
}
