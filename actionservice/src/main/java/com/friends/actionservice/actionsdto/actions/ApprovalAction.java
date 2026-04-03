package com.friends.actionservice.actionsdto.actions;


import com.friends.actionservice.actionsdto.ActionRequest;
import com.friends.actionservice.appconstant.ApprovalType;
import com.friends.actionservice.appconstant.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalAction extends ActionRequest {
    private String name;
    private Channel channel;
    private ApprovalType approvalType;
    private Long approverId;
    private Integer approverRoleId;
    private Integer teamId;
    private String subject;
    private String body;
}
