package com.friends.actionservice.actionsdto.actions;

import com.friends.actionservice.actionsdto.ActionRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoApprovalAction extends ActionRequest {
    @NotBlank(message = "Auto Approval name required.")
    private String name;
}
