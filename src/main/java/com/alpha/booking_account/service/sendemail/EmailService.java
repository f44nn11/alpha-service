package com.alpha.booking_account.service.sendemail;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 1/4/2025
 */
@Service
public class EmailService {
    private final WebClient webClient;

    @Autowired
    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String sendEmailWithAttachments(String emailRequest, List<MultipartFile> attachments, String serviceUrl) {
        try {
            // Build multipart request body
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("data", emailRequest);

            // Add files to the request
            if (attachments != null && !attachments.isEmpty()) {
                for (MultipartFile file : attachments) {
                    builder.part("attachments", file.getResource())
                            .header("Content-Disposition", "form-data; name=attachments; filename=" + file.getOriginalFilename());
                }
            }

            // Send request
            return webClient.post()
                    .uri(serviceUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Synchronous call
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
