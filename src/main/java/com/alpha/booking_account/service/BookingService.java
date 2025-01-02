package com.alpha.booking_account.service;


import com.alpha.booking_account.service.sendemail.EmailServiceClient;
import com.alpha.booking_account.util.ServiceTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/*
 * Created by: fkusu
 * Date: 1/1/2025
 */
@Service
public class BookingService {
    @Autowired
    private EmailServiceClient emailServiceClient;


    public void sendEmailService(String data, List<String> attachmentPaths) {
        try {
            List<MultipartFile> attachments = attachmentPaths.stream()
                    .map(filePath -> {
                        try {
                            return ServiceTool.convertToMultipartFile(filePath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(Objects::nonNull) // Hanya gunakan file yang berhasil dikonversi
                    .toList();

            ResponseEntity<String> response = emailServiceClient.sendEmail(data, attachments);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Email sent successfully!");
            } else {
                System.err.println("Failed to send email: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}
