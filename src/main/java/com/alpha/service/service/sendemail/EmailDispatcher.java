package com.alpha.service.service.sendemail;

import com.alpha.service.model.sendemail.EmailAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailDispatcher {
    private static final Logger log = LoggerFactory.getLogger(EmailDispatcher.class);
    private final EmailService emailService;

    public EmailDispatcher(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async("emailExecutor")
    public void sendEmailAsync(String emailRequestJson, List<EmailAttachment> attachments, String serviceUrl) {
        try {
            emailService.sendEmailWithByteAttachments(emailRequestJson, attachments, serviceUrl);
        } catch (Exception ex) {
            log.error("Failed to dispatch email asynchronously: {}", ex.getMessage(), ex);
        }
    }
}
