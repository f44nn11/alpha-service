package com.alpha.service.service.sendemail;


import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.model.sendemail.EmailRequestModel;
import com.alpha.service.model.sendemail.EmailAttachment;
import com.alpha.service.repository.LogEmailAppRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 1/4/2025
 */
@Service
public class EmailService {
    private final WebClient webClient;
    private Logger logger = org.slf4j.LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private LogEmailAppRepository logEmailAppRepository;

    @Autowired
    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Async
    public void sendEmailWithAttachmentsAsync(EmailRequestModel emailRequestModel, List<MultipartFile> attachmentFiles,
                                              String emailServiceUrl, String insCd, Long logId) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("data", new Gson().toJson(emailRequestModel));

        if (attachmentFiles != null && !attachmentFiles.isEmpty()) {
            for (MultipartFile file : attachmentFiles) {
                builder.part("attachments", file.getResource())
                        .header("Content-Disposition", "form-data; name=attachments; filename=" + file.getOriginalFilename());
            }
        }

        // NON-BLOCKING: gunakan subscribe!
        webClient.post()
                .uri(emailServiceUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(result -> {
                    try {
                        ResponseGlobalModel<?> emailResponse = new Gson().fromJson(result, ResponseGlobalModel.class);
                        Map<String, Object> respData = (Map<String, Object>) emailResponse.getData();

                        if (emailResponse.getResultCode() == 200) {

                            // Update status SENT
                            logEmailAppRepository.findById(logId).ifPresent(log -> {
                                log.setStatus("SENT");
                                log.setStatusMessage(emailResponse.getMessage());
                                if (respData != null) {
                                    if (respData.get("recipients") != null)
                                        log.setMailTo(respData.get("recipients").toString());
                                    if (respData.get("subject") != null)
                                        log.setSubject(respData.get("subject").toString());
                                    if (respData.get("serialNumber") != null)
                                        log.setSerialNumber(respData.get("serialNumber").toString()); // jika ada field-nya
                                }
                                log.setSendDt(LocalDateTime.now());
                                logEmailAppRepository.save(log);
                            });
                        } else {
                            // Update status FAILED
                            logEmailAppRepository.findById(logId).ifPresent(log -> {
                                log.setStatus("FAILED");
                                log.setStatusMessage(emailResponse.getMessage());
                                log.setFailedDt(LocalDateTime.now());
                                logEmailAppRepository.save(log);
                            });
                        }
                    } catch (Exception ex) {
                        logEmailAppRepository.findById(logId).ifPresent(log -> {
                            log.setStatus("FAILED");
                            log.setStatusMessage("Async error: " + ex.getMessage());
                            log.setFailedDt(LocalDateTime.now());
                            logEmailAppRepository.save(log);
                        });
                    }
                }, error -> {
                    // Error in HTTP call
                    logEmailAppRepository.findById(logId).ifPresent(log -> {
                        log.setStatus("FAILED");
                        log.setStatusMessage("Async HTTP error: " + error.getMessage());
                        log.setFailedDt(LocalDateTime.now());
                        logEmailAppRepository.save(log);
                    });
                });
    }

    public ResponseGlobalModel<Object> sendEmailWithAttachments(String emailRequest, List<MultipartFile> attachments, String serviceUrl) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();

        try {
            // Build multipart request body
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("data", emailRequest);

            // Add files to the request
            boolean hasAttachment = attachments != null && !attachments.isEmpty();
            if (hasAttachment) {
                for (MultipartFile file : attachments) {
                    builder.part("attachments", file.getResource())
                            .header("Content-Disposition", "form-data; name=attachments; filename=" + file.getOriginalFilename());
                }
            }

            String result = webClient.post()
                    .uri(serviceUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Synchronous call


            ResponseGlobalModel<?> emailResponse = new Gson().fromJson(result, ResponseGlobalModel.class);

            if (emailResponse.getResultCode() == 200) {
                responseGlobalModel = ResponseGlobalModel.<Object>builder()
                        .resultCode(200)
                        .message(hasAttachment
                                ? "Email sent successfully with attachment."
                                : "Email sent successfully without attachment.")
                        .data(emailResponse.getData())
                        .build();
            } else {
                responseGlobalModel = ResponseGlobalModel.<Object>builder()
                        .resultCode(emailResponse.getResultCode())
                        .message(emailResponse.getMessage())
                        .error(emailResponse.getError())
                        .data(emailResponse.getData())
                        .build();
            }
        } catch (Exception e) {
            responseGlobalModel = ResponseGlobalModel.<Object>builder()
                    .resultCode(500)
                    .message("Failed to send email: " + e.getMessage())
                    .error(Map.of("error", e.getMessage()))
                    .build();
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> sendEmailWithByteAttachments(String emailRequest, List<EmailAttachment> attachments, String serviceUrl) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("data", emailRequest);

            boolean hasAttachment = attachments != null && !attachments.isEmpty();
            if (hasAttachment) {
                for (EmailAttachment att : attachments) {
                    final String fname = att.getFileName() == null ? "attachment" : att.getFileName();
                    ByteArrayResource res = new ByteArrayResource(att.getBytes()) {
                        @Override
                        public String getFilename() {
                            return fname;
                        }
                    };
                    MultipartBodyBuilder.PartBuilder partBuilder = builder.part("attachments", res)
                            .header("Content-Disposition", "form-data; name=attachments; filename=" + fname);
                    if (att.getContentType() != null && !att.getContentType().isBlank()) {
                        try {
                            partBuilder.contentType(MediaType.parseMediaType(att.getContentType()));
                        } catch (Exception ignore) {
                            // ignore invalid content type
                        }
                    }
                }
            }

            String result = webClient.post()
                    .uri(serviceUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ResponseGlobalModel<?> emailResponse = new Gson().fromJson(result, ResponseGlobalModel.class);
            if (emailResponse.getResultCode() == 200) {
                responseGlobalModel = ResponseGlobalModel.<Object>builder()
                        .resultCode(200)
                        .message(hasAttachment
                                ? "Email sent successfully with attachment."
                                : "Email sent successfully without attachment.")
                        .data(emailResponse.getData())
                        .build();
            } else {
                responseGlobalModel = ResponseGlobalModel.<Object>builder()
                        .resultCode(emailResponse.getResultCode())
                        .message(emailResponse.getMessage())
                        .error(emailResponse.getError())
                        .data(emailResponse.getData())
                        .build();
            }
        } catch (Exception e) {
            responseGlobalModel = ResponseGlobalModel.<Object>builder()
                    .resultCode(500)
                    .message("Failed to send email: " + e.getMessage())
                    .error(Map.of("error", e.getMessage()))
                    .build();
        }
        return responseGlobalModel;
    }
}
