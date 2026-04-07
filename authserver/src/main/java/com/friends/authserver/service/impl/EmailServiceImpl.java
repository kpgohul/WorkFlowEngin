package com.friends.authserver.service.impl;

import com.friends.authserver.service.EmailService;
import com.friends.authserver.util.routing.ApiRoutes;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.lang.reflect.InvocationTargetException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(fromAddress);
            helper.setSubject("Password Reset Request");

            Context context = new Context();
            context.setVariable("resetUrl", baseUrl + ApiRoutes.AUTH_RESET_PASSWORD + "?token=" + token);
            context.setVariable("appName", "FlowForge");

            String content = templateEngine.process("email/password-reset", context);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendAccountDeletionConfirmation(String to, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(fromAddress);
            helper.setSubject("Account Deletion Confirmation");

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("appName", "FlowForge");

            String content = templateEngine.process("email/account-deletion", context);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
        catch (Exception e){
            log.info("Unexpected error while sending account deletion confirmation email to {}: {}" , to, e.getMessage());
            throw new RuntimeException("Unexpected error while sending email", e);
        }
    }
}