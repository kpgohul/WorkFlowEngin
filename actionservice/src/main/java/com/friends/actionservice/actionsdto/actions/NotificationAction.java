package com.friends.actionservice.actionsdto.actions;

import com.friends.actionservice.actionsdto.ActionRequest;
import com.friends.actionservice.appconstant.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationAction extends ActionRequest {
    private String name;
    private Channel channel;
    private Long notifyTo;
    private String subject;
    private String body;
}
