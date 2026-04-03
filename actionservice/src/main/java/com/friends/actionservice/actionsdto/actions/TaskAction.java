package com.friends.actionservice.actionsdto.actions;

import com.friends.actionservice.actionsdto.ActionRequest;
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
