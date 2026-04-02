package com.friends.executionservice.clientdto.actionclientdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActionResponse extends ActionRequest {

    private Long executionId;
    private Long executionStepId;
    private Boolean isSuccess;
    private String message;
    private String error;
}
