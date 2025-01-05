package com.alpha.booking_account.service.sendemail;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class EmailServiceFallback implements EmailServiceClient {
    @Override
    public ResponseEntity<String> sendEmail(String emailRequest, List<MultipartFile> attachments) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Email service is currently unavailable.");
    }
}
