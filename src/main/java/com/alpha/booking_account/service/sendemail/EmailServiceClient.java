package com.alpha.booking_account.service.sendemail;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 1/1/2025
 */
//, url = "${email.service.url}"
@FeignClient(name = "SYSPROP-MASTER")
public interface EmailServiceClient {
    @PostMapping(value = "/email/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> sendEmail(
            @RequestParam("data") String emailRequest,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments
    );
}
