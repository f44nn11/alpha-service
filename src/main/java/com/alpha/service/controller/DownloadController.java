package com.alpha.service.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 1/9/2025
 */
@RestController
@RequestMapping("/download")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.1.2:3000"}, allowCredentials = "true" ,
        allowedHeaders = {"Content-Type", "Authorization", "X-Requested-With"})
public class DownloadController {
    private static final Logger logger = LoggerFactory.getLogger(BookingAccountController.class);

    @PostMapping("/doc")
    ResponseEntity<Resource> downloadFile(@RequestBody Map<String, String> request) {
        String urlPath = request.get("urlPath");
        try {

            File file = new File(urlPath);
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Create a ByteArrayResource from the file
            Path path = file.toPath();
            ByteArrayResource resource = null;
            try {
                resource = new ByteArrayResource(Files.readAllBytes(path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Determine Content-Type
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Default binary type
            }

            // Build ResponseEntity
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
