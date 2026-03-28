package com.friends.workflowservice.dto.common;

import com.friends.workflowservice.dto.rule.CreateRuleRequest;
import com.friends.workflowservice.dto.step.CreateStepRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStepRuleRequest {
    private CreateStepRequest step;
    private CreateRuleRequest rule;
}
