package com.friends.executionservice.clientdto.actionclientdto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.friends.executionservice.appconstant.ActionType;
import com.friends.executionservice.clientdto.actionclientdto.actions.ApprovalAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.AutoApprovalAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.DelayAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.NotificationAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.TaskAction;
import com.friends.executionservice.clientdto.actionclientdto.actions.WebHookAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "actionType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApprovalAction.class, name = "APPROVAL"),
        @JsonSubTypes.Type(value = AutoApprovalAction.class, name = "AUTO_APPROVAL"),
        @JsonSubTypes.Type(value = DelayAction.class, name = "DELAY"),
        @JsonSubTypes.Type(value = WebHookAction.class, name = "WEBHOOK"),
        @JsonSubTypes.Type(value = NotificationAction.class, name = "NOTIFICATION"),
        @JsonSubTypes.Type(value = TaskAction.class, name = "TASK")
})
public abstract class ActionRequest {
    @NotNull(message = "Action Type cannot be null")
    private ActionType actionType;
}
