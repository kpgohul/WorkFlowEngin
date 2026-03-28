package com.friends.workflowservice.dto.common;

import com.friends.workflowservice.dto.rule.UpdateRuleRequest;
import com.friends.workflowservice.dto.step.UpdateStepRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStepRuleRequest {
    private UpdateStepRequest step;
    private UpdateRuleRequest rule;
}
