package com.friends.actionservice.util;

import com.friends.actionservice.appconstant.Channel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MailSenderUtil {

    private static final Logger log = LoggerFactory.getLogger(MailSenderUtil.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:no-reply@example.com}")
    private String from;

    public void send(Channel channel, String toEmailOrPhone, String subject, String templateName, Map<String, Object> variables) {
        if (channel == null) {
            throw new IllegalArgumentException("channel cannot be null");
        }

        switch (channel) {
            case MAIL -> sendMail(toEmailOrPhone, subject, templateName, variables);
            case SMS, WHATSAPP, TELEGRAM ->
                    log.info("[MOCK-CHANNEL:{}] to={} subject={} vars={}", channel, toEmailOrPhone, subject, variables);
        }
    }

    private void sendMail(String to, String subject, String templateName, Map<String, Object> variables) {
        String bodyHtml = render(templateName, variables);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(bodyHtml, true);

            mailSender.send(mimeMessage);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to send mail", ex);
        }
    }

    private String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        return templateEngine.process(templateName, context);
    }
}
