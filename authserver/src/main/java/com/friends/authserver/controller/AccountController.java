package com.friends.authserver.controller;

import com.friends.authserver.dto.AccountCreateRequest;
import com.friends.authserver.dto.DeleteAccountRequest;
import com.friends.authserver.service.AccountService;
import com.friends.authserver.util.routing.ApiRoutes;
import com.friends.authserver.util.securityutil.AuthenticationHelper;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.ACCOUNTS_BASE)
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        // Redirect already authenticated users away from registration page
        if (AuthenticationHelper.isAuthenticated()) {
            return "redirect:" + ApiRoutes.ACCOUNTS_HOME;
        }
        model.addAttribute("accountCreateRequest", new AccountCreateRequest());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(
            @Valid @ModelAttribute("accountCreateRequest") AccountCreateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // Redirect already authenticated users away from registration
        if (AuthenticationHelper.isAuthenticated()) {
            return "redirect:" + ApiRoutes.ACCOUNTS_HOME;
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            accountService.registerUser(request);
            // Use redirect to preserve OAuth2 authorization request saved in session
            redirectAttributes.addFlashAttribute("message", "Account created successfully. Please sign in.");
            return "redirect:" + ApiRoutes.AUTH_LOGIN;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/home")
    public String showAccountHomePage() {
        return "home";
    }

    @GetMapping("/settings")
    public String showAccountSettingsPage(Model model) {
        model.addAttribute("deleteAccountRequest", new DeleteAccountRequest());
        return "account-settings";
    }

    @PostMapping("/delete")
    public String handleAccountDeletion(
            @Valid @ModelAttribute("deleteAccountRequest") DeleteAccountRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            HttpServletRequest httpRequest
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Password is required.");
            return "redirect:" + ApiRoutes.ACCOUNTS_SETTINGS;
        }

        try {
            Long userId = AuthenticationHelper.getLoggedInAccountID();
            accountService.deleteAccount(userId, request.getPassword());
            SecurityContextHolder.clearContext();

            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            redirectAttributes.addFlashAttribute("message", "Your account has been deleted successfully.");
            return "redirect:" + ApiRoutes.AUTH_LOGIN + "?accountDeleted";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getCause());
            return "redirect:" + ApiRoutes.ACCOUNTS_SETTINGS;
        }
    }
}