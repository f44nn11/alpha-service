package com.alpha.service.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class ServiceTool {
    private final Environment env;

    public Constants.SYSTEM_ENVIRONMENT_TYPE CURRENT_SYSTEM_ENVIRONMENT;

    public ServiceTool(Environment env) {
        this.env = env;
        String activeProfile = env.getProperty("spring.profiles.active");

        if ("production".equalsIgnoreCase(activeProfile)) {
            CURRENT_SYSTEM_ENVIRONMENT = Constants.SYSTEM_ENVIRONMENT_TYPE.PRODUCTION;
        } else {
            CURRENT_SYSTEM_ENVIRONMENT = Constants.SYSTEM_ENVIRONMENT_TYPE.DEVELOPMENT;
        }
    }

    public String getActiveProfile() {
        return env.getProperty("spring.profiles.active");
    }
    public String getProperty(String key) {
        return env.getProperty(key);
    }

    public final String generateTimestamp() {
        //UTC
        Instant instant = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();

        //format ISO 8601
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return formatter.format(instant.atZone(zoneId));
    }
    public static MultipartFile convertToMultipartFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        byte[] content = Files.readAllBytes(path);

        return new MockMultipartFile(fileName, fileName, Files.probeContentType(path), content);
    }
}
