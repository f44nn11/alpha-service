package com.alpha.booking_account.service.sendemail;


import com.alpha.booking_account.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 1/1/2025
 */
//, url = "${email.service.url}"
@FeignClient(name = "SYSPROP-MASTER", url = "${email.service.url}", fallback = EmailServiceFallback.class, configuration = FeignConfiguration.class)
@Primary
public interface EmailServiceClient {
    @PostMapping(value = "/email/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> sendEmail(
            @RequestParam("data") String emailRequest,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments
    );
}

