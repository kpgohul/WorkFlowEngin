package com.friends.executionservice.clientdto.workflowclientdto.ruleconfig;

import com.friends.executionservice.appconstant.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRuleConfig implements RuleConfig{
    private String name;
    private Channel channel;
    private Long notifyTo;
    private String subject;
    private String body;
}
