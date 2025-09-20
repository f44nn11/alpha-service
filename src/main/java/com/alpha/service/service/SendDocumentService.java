package com.alpha.service.service;

import com.alpha.service.entity.LogEmailApp;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.model.senddocument.PlacingRequestModel;
import com.alpha.service.model.sendemail.EmailRequestModel;
import com.alpha.service.service.sendemail.EmailService;
import com.alpha.service.service.sendemail.LogEmailAppService;
import com.alpha.service.util.ServiceTool;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SendDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(SendDocumentService.class);

    @Autowired
    private DataSource dataSource;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ServiceTool serviceTool;
    @Autowired
    private LogEmailAppService logEmailAppService;

    private final Gson gson = new Gson();

    public ResponseGlobalModel<Object> processSendDocument(String rawJson) {
        // 1) Minimal validation on raw JSON
        ValidationResult validation = validateRawJson(rawJson);
        if (!validation.valid) {
            return ResponseGlobalModel.<Object>builder()
                    .resultCode(422)
                    .message(validation.message)
                    .error(Map.of("error", validation.message))
                    .build();
        }

        // 2) Call stored procedure
        Map<String, Object> spResult;
        try {
            spResult = callStoredProcedure(rawJson);
        } catch (Exception ex) {
            logger.error("DB error on USP_SEND_DOCUMENT", ex);
            return ResponseGlobalModel.<Object>builder()
                    .resultCode(500)
                    .message("DB error: " + ex.getMessage())
                    .error(Map.of("error", ex.getMessage()))
                    .build();
        }

        int resultCode = (int) spResult.getOrDefault("resultCode", 500);
        String message = Objects.toString(spResult.get("message"), "");
        String placingCd = Objects.toString(spResult.get("placingCd"), null);
        String bookCd = Objects.toString(spResult.get("bookCd"), null);

        // 3) Map to HTTP and when success prepare emails
        if (resultCode == 1000) {
            // build email tasks
            List<Map<String, Object>> emailResults = new ArrayList<>();
            try {
                JsonObject req = JsonParser.parseString(rawJson).getAsJsonObject();
                String createdBy = null;
                if (req.has("createBy") && !req.get("createBy").isJsonNull()) {
                    JsonElement cb = req.get("createBy");
                    createdBy = cb.isJsonPrimitive() ? cb.getAsJsonPrimitive().getAsString() : cb.toString();
                }
                if (createdBy == null || createdBy.isBlank()) createdBy = "System";

                JsonArray insArr = req.getAsJsonArray("insurances");
                for (JsonElement insEl : insArr) {
                    JsonObject ins = insEl.getAsJsonObject();
                    String insCd = Objects.toString(ins.get("insCd").getAsLong());
                    int rev = ins.has("rev") && !ins.get("rev").isJsonNull() ? ins.get("rev").getAsInt() : 0;
                    String mailType = rev == 0 ? "SDCNEW" : "SDCREV";

                    // attachments
                    List<String> attachmentUrls = new ArrayList<>();
                    List<String> attachmentNames = new ArrayList<>();
                    if (ins.has("docTypes") && ins.get("docTypes").isJsonArray()) {
                        for (JsonElement dtEl : ins.getAsJsonArray("docTypes")) {
                            JsonObject dt = dtEl.getAsJsonObject();
                            if (dt.has("urlPath") && !dt.get("urlPath").isJsonNull()) {
                                String url = Objects.toString(dt.get("urlPath").getAsString(), "");
                                if (!url.isBlank()) {
                                    attachmentUrls.add(url);
                                    // collect display name when available
                                    String name = null;
                                    if (dt.has("descp") && !dt.get("descp").isJsonNull()) {
                                        try { name = dt.get("descp").getAsString(); } catch (Exception ignore) { name = dt.get("descp").toString(); }
                                    }
                                    if (name != null && !name.isBlank()) {
                                        name = name.replace("<", "&lt;").replace(">", "&gt;").trim();
                                        attachmentNames.add(name);
                                    }
                                }
                            }
                        }
                    }

                    // Build HTML list for attachments to be used in the email template
                    String attachmentListHtml = attachmentNames.isEmpty()
                            ? "<li>Tidak ada dokumen</li>"
                            : attachmentNames.stream().map(n -> "<li>" + n + "</li>")
                            .collect(java.util.stream.Collectors.joining());

                    EmailRequestModel emailReq = new EmailRequestModel();
                    emailReq.setMailType(mailType);
                    emailReq.setCode(placingCd);
                    emailReq.setBookCd(bookCd);
                    emailReq.setActionType("2");
                    emailReq.setRevDoc(String.valueOf(rev));
                    emailReq.setCreatedBy(createdBy);

                    Map<String, Object> template = new HashMap<>();
                    template.put("mailType", mailType);
                    template.put("actionType", "2");
                    template.put("placingCd", placingCd);
                    template.put("bookCd", bookCd);
                    template.put("code", placingCd);
                    template.put("insCd", insCd);
                    template.put("ATTACHMENT_LIST", attachmentListHtml);
                    emailReq.setParamTemplate(template);

                    // Log email as PROCESSING
                    LogEmailApp log = new LogEmailApp();
                    log.setRefType("SENDDOC");
                    log.setRefId(placingCd);
                    log.setRefSubId(insCd);
                    log.setMailType(mailType);
                    log.setSubject(emailReq.getSubject());
                    log.setMailTo("");
                    log.setAttachmentInfo(gson.toJson(attachmentUrls));
                    log.setStatus("PROCESSING");
                    log.setRequestDt(LocalDateTime.now());
                    log.setCreateBy(createdBy);
                    LogEmailApp saved = logEmailAppService.createLog(log);

                    // async
                    List<MultipartFile> files = serviceTool.convertUrlsToMultipartFiles(attachmentUrls);
                    String emailServiceUrl = serviceTool.getProperty("email.service.url");
                    if (emailServiceUrl == null) emailServiceUrl = ""; // TODO property should be set
                    emailServiceUrl = emailServiceUrl + "/email/send";
                    emailService.sendEmailWithAttachmentsAsync(emailReq, files, emailServiceUrl, insCd, saved.getId());

                    Map<String, Object> emailRes = new HashMap<>();
                    emailRes.put("insCd", insCd);
                    emailRes.put("mailType", mailType);
                    emailRes.put("resultCode", 200);
                    emailRes.put("message", "PROCESSING");
                    emailResults.add(emailRes);
                }
            } catch (Exception ex) {
                logger.error("Email preparation error", ex);
                return ResponseGlobalModel.<Object>builder()
                        .resultCode(500)
                        .message("Failed preparing emails: " + ex.getMessage())
                        .error(Map.of("error", ex.getMessage()))
                        .build();
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("resultCode", 200);
            body.put("message", "OK");
            if (placingCd != null) body.put("placingCd", placingCd);
            if (bookCd != null) body.put("bookCd", bookCd);
            body.put("emailResults", emailResults);

            return ResponseGlobalModel.<Object>builder()
                    .resultCode(200)
                    .message("OK")
                    .data(body)
                    .build();
        } else if (resultCode == 422) {
            return ResponseGlobalModel.<Object>builder()
                    .resultCode(422)
                    .message(message)
                    .error(Map.of("error", message))
                    .build();
        } else {
            return ResponseGlobalModel.<Object>builder()
                    .resultCode(500)
                    .message(message != null && !message.isBlank() ? message : "Internal Error")
                    .error(Map.of("error", message))
                    .build();
        }
    }

    private ValidationResult validateRawJson(String rawJson) {
        try {
            JsonObject obj = JsonParser.parseString(rawJson).getAsJsonObject();
            if (!obj.has("placingCd") || obj.get("placingCd").isJsonNull() || obj.get("placingCd").getAsString().isBlank())
                return ValidationResult.invalid("placingCd is required");

            if (!obj.has("insurances") || !obj.get("insurances").isJsonArray() || obj.getAsJsonArray("insurances").size() == 0)
                return ValidationResult.invalid("insurances[] must be non-empty");

            JsonArray insArr = obj.getAsJsonArray("insurances");
            for (JsonElement el : insArr) {
                if (!el.isJsonObject()) return ValidationResult.invalid("insurances[] invalid");
                JsonObject ins = el.getAsJsonObject();
                if (!ins.has("insCd") || ins.get("insCd").isJsonNull()) return ValidationResult.invalid("insCd is required");
                if (!ins.has("rev") || ins.get("rev").isJsonNull()) return ValidationResult.invalid("rev is required");
                int rev = ins.get("rev").getAsInt();
                if (rev < 0) return ValidationResult.invalid("rev must be >= 0");
                if (!ins.has("docTypes") || !ins.get("docTypes").isJsonArray()) return ValidationResult.invalid("docTypes[] required");
                for (JsonElement dtEl : ins.getAsJsonArray("docTypes")) {
                    JsonObject dt = dtEl.getAsJsonObject();
                    if (!dt.has("code") || dt.get("code").isJsonNull()) return ValidationResult.invalid("docTypes[*].code is required");
                    String codeStr = dt.get("code").getAsString();
                    try { Integer.parseInt(codeStr); } catch (Exception ex) { return ValidationResult.invalid("docTypes[*].code must be int"); }
                }
            }
            return ValidationResult.valid();
        } catch (Exception ex) {
            return ValidationResult.invalid("Invalid JSON: " + ex.getMessage());
        }
    }

    private Map<String, Object> callStoredProcedure(String jsonParam) throws SQLException {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        CallableStatement cs = null;
        ResultSet rs = null;
        try {
            cs = conn.prepareCall("{ CALL USP_SEND_DOCUMENT(?) }");
            cs.setString(1, jsonParam);
            boolean hasResultSet = cs.execute();
            Map<String, Object> map = new HashMap<>();
            if (hasResultSet) {
                rs = cs.getResultSet();
                if (rs.next()) {
                    map.put("resultCode", rs.getInt("resultCode"));
                    map.put("message", rs.getString("message"));
                    // some DBs may return placingCd or bookCd
                    try { map.put("placingCd", rs.getString("placingCd")); } catch (SQLException ignored) {}
                    try { map.put("bookCd", rs.getString("bookCd")); } catch (SQLException ignored) {}
                }
            }
            return map;
        } finally {
            if (rs != null) try { rs.close(); } catch (Exception ignored) {}
            if (cs != null) try { cs.close(); } catch (Exception ignored) {}
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    private static class ValidationResult {
        final boolean valid;
        final String message;
        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        static ValidationResult valid() { return new ValidationResult(true, null); }
        static ValidationResult invalid(String msg) { return new ValidationResult(false, msg); }
    }
}
