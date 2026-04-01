package com.friends.workflowservice.dto.workflowstep;

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
public class WorkflowStepRequest {

    private Long workflowId;
    @NotBlank(message = "StepCode is required.")
    private String stepCode;
    @NotBlank(message = "step name is required.")
    private String name;
    @NotNull(message = "Step Line is required.")
    private Integer stepLine;
    @NotNull(message = "isLast is required.")
    private Boolean isLast;
    private Long stepTimeoutInMillis = 0L;

}
