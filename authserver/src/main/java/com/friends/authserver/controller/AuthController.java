package com.friends.authserver.controller;

import com.friends.authserver.dto.ForgotPasswordRequest;
import com.friends.authserver.dto.ResetPasswordRequest;
import com.friends.authserver.exception.InvalidTokenException;
import com.friends.authserver.exception.ResourceNotFoundException;
import com.friends.authserver.service.PasswordResetService;
import com.friends.authserver.util.routing.ApiRoutes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.friends.authserver.util.securityutil.AuthenticationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.AUTH_BASE)
public class AuthController {

    private static final String LOGIN_NOTICE_LOGOUT = "LOGIN_NOTICE_LOGOUT";
    private static final String LOGIN_NOTICE_SESSION_EXPIRED = "LOGIN_NOTICE_SESSION_EXPIRED";

    private final PasswordResetService passwordResetService;

    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest request, Model model) {
        // Redirect already authenticated users away from login page
        if (AuthenticationHelper.isAuthenticated()) {
            return "redirect:" + ApiRoutes.ACCOUNTS_HOME;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            if (Boolean.TRUE.equals(session.getAttribute(LOGIN_NOTICE_LOGOUT))) {
                model.addAttribute("logoutNotice", true);
                session.removeAttribute(LOGIN_NOTICE_LOGOUT);
            }
            if (Boolean.TRUE.equals(session.getAttribute(LOGIN_NOTICE_SESSION_EXPIRED))) {
                model.addAttribute("sessionExpiredNotice", true);
                session.removeAttribute(LOGIN_NOTICE_SESSION_EXPIRED);
            }
        }

        return "login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        // Redirect already authenticated users away from forgot password page
        if (AuthenticationHelper.isAuthenticated()) {
            return "redirect:" + ApiRoutes.ACCOUNTS_HOME;
        }

        if (!model.containsAttribute("forgotPasswordRequest")) {
            model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        }
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // Redirect already authenticated users away from forgot password functionality
        if (AuthenticationHelper.isAuthenticated()) {
            return "redirect:" + ApiRoutes.ACCOUNTS_HOME;
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please provide a valid email address.");
            return "redirect:" + ApiRoutes.AUTH_FORGOT_PASSWORD;
        }

        try {
            passwordResetService.createPasswordResetToken(request.getEmail());
        } catch (ResourceNotFoundException | IllegalStateException ignored) {
            // Keep response consistent to avoid account enumeration.
        }

        redirectAttributes.addFlashAttribute("message", "If the email exists, a reset link has been sent.");
        return "redirect:" + ApiRoutes.AUTH_FORGOT_PASSWORD + "?success";
    }

    @GetMapping("/logout")
    public String showLogoutPage() {
        return "logout";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        // Redirect already authenticated users away from password reset
        if (AuthenticationHelper.isAuthenticated()) {
            return "redirect:" + ApiRoutes.ACCOUNTS_HOME;
        }

        if (!passwordResetService.validateToken(token)) {
            model.addAttribute("error", "Invalid or expired password reset link.");
            return "error";
        }

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        model.addAttribute("resetPasswordRequest", request);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(
            @Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // Redirect already authenticated users away from password reset functionality
        if (AuthenticationHelper.isAuthenticated()) {
            return "redirect:" + ApiRoutes.ACCOUNTS_HOME;
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please provide valid password details.");
            return "redirect:" + ApiRoutes.AUTH_RESET_PASSWORD + "?token=" + request.getToken();
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:" + ApiRoutes.AUTH_RESET_PASSWORD + "?token=" + request.getToken();
        }

        try {
            passwordResetService.resetPassword(request.getToken(), request.getPassword());
            redirectAttributes.addFlashAttribute("message", "Password reset successful. Please sign in.");
            return "redirect:" + ApiRoutes.AUTH_LOGIN + "?resetSuccess";
        } catch (InvalidTokenException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:" + ApiRoutes.AUTH_RESET_PASSWORD + "?token=" + request.getToken();
        }
    }

    /**
     * Session extension endpoint for AJAX requests
     * Allows authenticated users to extend their session without full page reload
     */
    @PostMapping("/session-extend")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> extendSession(HttpServletRequest request) {
        // Check if user is authenticated
        if (!AuthenticationHelper.isAuthenticated()) {
            return ResponseEntity.status(401)
                .body(Map.of(
                    "success", false,
                    "message", "User not authenticated"
                ));
        }

        try {
            // Get current session and update last accessed time
            HttpSession session = request.getSession(false);
            if (session != null) {
                // Accessing the session automatically updates the last accessed time
                session.setAttribute("lastActivity", System.currentTimeMillis());

                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Session extended successfully",
                    "sessionId", session.getId(),
                    "maxInactiveInterval", session.getMaxInactiveInterval()
                ));
            } else {
                return ResponseEntity.status(401)
                    .body(Map.of(
                        "success", false,
                        "message", "No active session found"
                    ));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to extend session: " + ex.getMessage()
                ));
        }
    }
}