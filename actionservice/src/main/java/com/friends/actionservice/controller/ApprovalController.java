package com.friends.actionservice.controller;

import com.friends.actionservice.path.ApiRoutes;
import com.friends.actionservice.service.ApprovalService;
import com.friends.actionservice.service.ApprovalService.ApprovalResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping(ApiRoutes.BASE_APPROVAL)
// Resolves to /api/v1/approval
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * Example:
     * /api/approval/respond?token=...&approval=accept&approverId=123
     */
    @GetMapping("/respond")
    public Mono<String> respond(
            @RequestParam String token,
            @RequestParam String approval,
            @RequestParam(required = false) Long approverId,
            Model model
    ) {
        return approvalService.respond(token, approval, approverId)
                .doOnNext((ApprovalResult result) -> {
                    model.addAttribute("title", result.title());
                    model.addAttribute("message", result.message());
                    model.addAttribute("resolved", result.resolved());
                    model.addAttribute("executionId", result.executionId());
                    model.addAttribute("executionStepId", result.executionStepId());
                })
                .thenReturn("web/approval-result");
    }
}
