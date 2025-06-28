package com.alpha.service.service;


import com.alpha.service.service.sendemail.EmailServiceClient;
import com.alpha.service.util.ServiceTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
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

    @Autowired
    private ServiceTool serviceTool;


    public void sendEmailService(String data, List<String> attachmentPaths) {
        System.out.printf("Sending email service data: %s\n", data);
    }
}
