package com.alpha.service.controller;

import com.alpha.service.model.procedure.UspBookingVisitParam;
import com.alpha.service.model.procedure.UspBookingVisitGetParam;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.service.BookingVisitService;
import com.alpha.service.util.DataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping
public class BookingVisitController {

    private static final Logger logger = LoggerFactory.getLogger(BookingVisitController.class);

    @Autowired
    private BookingVisitService service;

    @Autowired
    private DataUtil dataUtil;

    // 1) JSON only (no file upload) - p_pthevidance provided as string
    @PostMapping(path = "/booking-visit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createOrUpdateBookingVisitJson(@RequestBody UspBookingVisitParam req) {
        Map<String, Object> result = service.callUspBookingVisit(req);
        String visitCd = Objects.toString(result.get("p_out_visitCd"), null);
        String message = Objects.toString(result.get("p_message"), null);
        Map<String, Object> body = new HashMap<>();
        body.put("visitCd", visitCd);
        body.put("message", message);
        return ResponseEntity.ok(body);
    }

    @PostMapping(path = "/booking-visit/doVisit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseGlobalModel<Object>> getBookingVisit(@RequestBody UspBookingVisitGetParam req) {
        Map<String, Object> result = service.callUspBookingVisitGet(req);
        Integer resultCode = (Integer) result.get("p_resultCode");
        String message = Objects.toString(result.get("p_message"), null);
        String resultJson = Objects.toString(result.get("p_resultJson"), null);

        ResponseGlobalModel<Object> response = new ResponseGlobalModel<>();
        response.setResultCode(resultCode != null ? resultCode : 500);
        response.setMessage(message);

        try {
            if (resultJson != null && !resultJson.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    List<Map<String, Object>> jsonArray = mapper.readValue(resultJson, List.class);
                    response.setData(jsonArray);
                } catch (Exception e) {
                    Map<String, Object> jsonObject = mapper.readValue(resultJson, Map.class);
                    response.setData(Collections.singletonList(jsonObject));
                }
            }
        } catch (Exception ex) {
            response.setError(Collections.singletonMap("jsonError", ex.getMessage()));
        }

        return ResponseEntity.ok(response);
    }

    // 2) Multipart: fields + files (single or multiple)
    @PostMapping(path = "/booking-visit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createOrUpdateBookingVisitMultipart(
            @RequestPart("p_visitDt") String p_visitDt,
            @RequestPart("p_subject") String p_subject,
            @RequestPart(value = "p_visitCd", required = false) String p_visitCd,
            @RequestPart(value = "p_subjectDtl", required = false) String p_subjectDtl,
            @RequestPart("p_source") String p_source,
            @RequestPart(value = "p_bookCd" , required = false) String p_bookCd,
            @RequestPart("p_user") String p_user,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "p_pthevidance", required = false) String p_pthevidanceText
    ) {
        UspBookingVisitParam param = new UspBookingVisitParam();
        param.setP_visitCd(emptyToNull(p_visitCd));
        param.setP_visitDt(p_visitDt);
        param.setP_subject(p_subject);
        param.setP_subjectDtl(p_subjectDtl);
        param.setP_source(p_source);
        param.setP_bookCd(p_bookCd);
        param.setP_user(p_user);

        // Handle evidence: either text provided or upload files
        List<String> paths = new ArrayList<>();
        if (p_pthevidanceText != null && !p_pthevidanceText.isBlank()) {
            paths.add(p_pthevidanceText.trim());
        }
        if (files != null && files.length > 0) {
            for (MultipartFile f : files) {
                if (f != null && !f.isEmpty()) {
                    String saved = saveEvidenceFile(p_bookCd, p_visitCd, f);
                    if (saved != null) {
                        paths.add(saved);
                    }
                }
            }
        }
        param.setP_pthevidance(String.join(",", paths));

        Map<String, Object> result = service.callUspBookingVisit(param);
        String visitCd = Objects.toString(result.get("p_out_visitCd"), null);
        String message = Objects.toString(result.get("p_message"), null);
        Map<String, Object> body = new HashMap<>();
        body.put("visitCd", visitCd);
        body.put("message", message);
        return ResponseEntity.ok(body);
    }

    private String saveEvidenceFile(String bookCd, String visitCd, MultipartFile file) {
        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]+", "_");
            String baseName;
            String ext = "";
            int dot = fileName.lastIndexOf('.');
            if (dot > 0) {
                baseName = fileName.substring(0, dot);
                ext = fileName.substring(dot);
            } else {
                baseName = fileName;
            }
            String uploadRoot = dataUtil.getPathUpload();
            String subFolder = "booking-visit/" + bookCd + (visitCd != null && !visitCd.isBlank() ? ("/" + visitCd) : "");
            File folder = new File(uploadRoot + File.separator + subFolder);
            if (!folder.exists()) {
                //noinspection ResultOfMethodCallIgnored
                folder.mkdirs();
            }
            String newName = baseName + "_" + System.currentTimeMillis() + ext;
            Path path = Paths.get(folder.getAbsolutePath(), newName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            // return normalized forward-slashed path for DB
            return path.toString().replace(File.separatorChar, '/');
        } catch (Exception e) {
            logger.error("Failed to save evidence file", e);
            return null;
        }
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s;
    }
}
