package com.friends.executionservice.clientdto.actionclientdto.actions;


import com.friends.executionservice.appconstant.Channel;
import com.friends.executionservice.clientdto.actionclientdto.ActionRequest;
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
