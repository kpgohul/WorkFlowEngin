package com.friends.executionservice.clientdto.actionclientdto.actions;

import com.friends.executionservice.clientdto.actionclientdto.ActionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskAction extends ActionRequest {
    private String name;
}
