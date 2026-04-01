package com.friends.workflowservice.dto.workflowstep;

import com.friends.workflowservice.appconstant.RuleType;
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
public class WorkflowStepResponse {

    private Long id;
    private Long workflowId;
    private String stepCode;
    private String name;
    private Integer stepLine;
    private Boolean isLast;
    private Long stepTimeoutInMillis;

}
