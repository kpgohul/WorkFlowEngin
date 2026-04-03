package com.friends.actionservice.actionsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActionResponse {

    private Long executionId;
    private Long executionStepId;

    private Boolean isSuccess;
    private String message;
    private String error;
}
