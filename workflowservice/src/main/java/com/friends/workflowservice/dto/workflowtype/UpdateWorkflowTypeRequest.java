package com.friends.workflowservice.dto.workflowtype;

import com.friends.workflowservice.appconstant.workflowtype.WorkflowTypeStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWorkflowTypeRequest {

    @NotNull(message = "WorkflowType ID is required for update")
    private Long id;
    @NotBlank(message = "WorkflowType code is required")
    private String code;
    @NotBlank(message = "WorkflowType name is required")
    private String name;
    private String description;
    private WorkflowTypeStatus status = WorkflowTypeStatus.ACTIVE;
    private Integer version = 1;
    private @Valid List<WorkflowTypeFieldRequest> fields;
}
