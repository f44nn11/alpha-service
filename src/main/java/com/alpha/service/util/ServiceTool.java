package com.alpha.service.util;

import com.alpha.service.controller.BookingAccountController;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

@Component
public class ServiceTool {
    private final Environment env;
    private static final Logger logger = LoggerFactory.getLogger(BookingAccountController.class);
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
    public List<MultipartFile> convertUrlsToMultipartFiles(List<String> urls) {
        List<MultipartFile> files = new ArrayList<>();

        if (urls == null || urls.isEmpty()) {
            return files;
        }

        for (String rawLocation : urls) {
            String location = Optional.ofNullable(rawLocation).map(String::trim).orElse("");
            if (location.isEmpty()) {
                continue;
            }

            try {
                byte[] fileBytes;
                String fileName;
                String mimeType = "application/octet-stream";

                // Handle HTTP/HTTPS URLs
                if (location.matches("(?i)^https?://.*")) {
                    URL url = new URL(location);
                    try (InputStream inputStream = url.openStream()) {
                        fileBytes = IOUtils.toByteArray(inputStream);
                    }
                    String pathPart = Optional.ofNullable(url.getPath()).orElse("");
                    fileName = Paths.get(pathPart).getFileName() != null ? Paths.get(pathPart).getFileName().toString() : "file";
                    String ct = url.openConnection().getContentType();
                    if (ct != null && !ct.isBlank()) {
                        mimeType = ct;
                    } else {
                        mimeType = guessMimeTypeFromName(fileName);
                    }
                }
                // Handle file:// URLs
                else if (location.matches("(?i)^file:.*")) {
                    Path path = Paths.get(new URL(location).toURI());
                    if (!Files.exists(path)) {
                        logger.error("File not found (file URL): {}", path.toAbsolutePath());
                        continue;
                    }
                    try (InputStream inputStream = Files.newInputStream(path)) {
                        fileBytes = IOUtils.toByteArray(inputStream);
                    }
                    fileName = path.getFileName().toString();
                    mimeType = getMimeType(path.toString());
                }
                // Handle local filesystem path
                else {
                    String normalized = normalizeLocalPath(location);
                    Path path = Paths.get(normalized);
                    if (!Files.exists(path)) {
                        logger.error("File not found (local path): {}", path.toAbsolutePath());
                        continue;
                    }
                    try (InputStream inputStream = Files.newInputStream(path)) {
                        fileBytes = IOUtils.toByteArray(inputStream);
                    }
                    fileName = path.getFileName().toString();
                    mimeType = getMimeType(path.toString());
                }

                MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, mimeType, fileBytes);
                files.add(multipartFile);
            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.error("Exception occurred while converting file: {}", location, e);
            }
        }

        return files;
    }
    private String getMimeType(String fileUrl) {
        try {
            Path path = Paths.get(fileUrl);
            if (Files.exists(path)) {
                String mimeType = Files.probeContentType(path);
                if (mimeType != null) return mimeType;
            }
        } catch (Exception ignored) {
        }
        // Fallback to guessing by file name/extension
        String byName = guessMimeTypeFromName(Paths.get(fileUrl).getFileName() != null ? Paths.get(fileUrl).getFileName().toString() : fileUrl);
        return byName != null ? byName : "application/octet-stream";
    }

    private String guessMimeTypeFromName(String name) {
        try {
            String guess = java.net.URLConnection.guessContentTypeFromName(name);
            return (guess != null && !guess.isBlank()) ? guess : "application/octet-stream";
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name");
        return os != null && os.toLowerCase().contains("win");
    }

    private String normalizeLocalPath(String path) {
        if (path == null) return null;
        // Convert separators to current OS style to improve cross-platform handling
        return isWindows() ? path.replace("/", "\\") : path.replace("\\", "/");
    }
    public String saveFile(MultipartFile file, String baseFolder, String bookCd, String maxRev, String placingCd, String revDoc, String docFolder, String typeFlag) throws IOException {
        // Siapkan path direktori
        Path savePath = Paths.get(baseFolder, bookCd, "rev" + maxRev, placingCd, docFolder);
        Files.createDirectories(savePath);

        // Ambil nama file
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        String extension = "";

        int extIndex = originalFileName.lastIndexOf('.');
        if (extIndex > 0) {
            extension = originalFileName.substring(extIndex);
        }

        String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        String fileName;
        if ("comparation".equalsIgnoreCase(typeFlag)) {
            fileName = String.format("Comparation_%s_Rev%s_%s%s", bookCd, revDoc, timestamp, extension);
        } else {
            fileName = originalFileName;
        }

        Path filePath = savePath.resolve(fileName);

        // Jika file sudah ada, hapus dulu (replace)
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            logger.info("✅ File already exists, deleted: {}", filePath);
        }

        // Simpan file
        try (InputStream inDoc = file.getInputStream()) {
            Files.copy(inDoc, filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("📁 File saved at: {}", filePath);
        }

        // Return relative path (tanpa base folder)
        return filePath.toString().replace(baseFolder + File.separator, "").replace("\\", "/");
    }

    public String saveFileWithBase(MultipartFile file, String baseFolder, String bookCd, String maxRev, String placingCd, String revDoc, String docFolder, String typeFlag) throws IOException {
        // Siapkan path direktori
        Path savePath = Paths.get(baseFolder, bookCd, "rev" + maxRev, placingCd, docFolder);
        Files.createDirectories(savePath);

        // Ambil nama file
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        String extension = "";

        int extIndex = originalFileName.lastIndexOf('.');
        if (extIndex > 0) {
            extension = originalFileName.substring(extIndex);
        }

        String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        String fileName;
        if ("comparation".equalsIgnoreCase(typeFlag)) {
            fileName = String.format("Comparation_%s_Rev%s_%s%s", bookCd, revDoc, timestamp, extension);
        } else {
            fileName = originalFileName;
        }

        Path filePath = savePath.resolve(fileName);

        // Jika file sudah ada, hapus dulu (replace)
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            logger.info("✅ File already exists, deleted: {}", filePath);
        }

        // Simpan file
        try (InputStream inDoc = file.getInputStream()) {
            Files.copy(inDoc, filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("📁 File saved at: {}", filePath);
        }

        // Return relative path (dengan base folder)
        return filePath.toString().replace("\\", "/");
    }
    public String joinArray(List<String> arrays) {
        if (arrays == null || arrays.isEmpty()) {
            return "";
        }
        if (arrays.size() == 1) {
            return arrays.get(0);
        }
        if (arrays.size() == 2) {
            return arrays.get(0) + " dan " + arrays.get(1);
        }
        String join = String.join(", ", arrays.subList(0, arrays.size() - 1));
        return join + " dan " + arrays.get(arrays.size() - 1);
    }
}
