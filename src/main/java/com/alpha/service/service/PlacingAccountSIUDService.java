package com.alpha.service.service;


import com.alpha.service.entity.LogEmailApp;
import com.alpha.service.mapper.PlacingAccountMapper;
import com.alpha.service.model.EmailCheckModel;
import com.alpha.service.model.EmailCheckResult;
import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.model.comparation.ComparationRequestModel;
import com.alpha.service.model.placing.PlacingRequestModel;
import com.alpha.service.model.procedure.*;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.model.sendemail.EmailRequestModel;
import com.alpha.service.repository.PlacingAccountRepository;
import com.alpha.service.service.sendemail.EmailService;
import com.alpha.service.service.sendemail.LogEmailAppService;
import com.alpha.service.util.DataUtil;
import com.alpha.service.util.ServiceTool;
import com.alpha.service.service.FileStorageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.persistence.ParameterMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Created by: fkusu
 * Date: 2/3/2025
 */
@Service
public class PlacingAccountSIUDService {
    private final Logger logger = LoggerFactory.getLogger(PlacingAccountSIUDService.class);

    @Autowired
    private PlacingAccountRepository repository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ServiceTool serviceTool;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DataUtil dataUtil;
    @Autowired
    private LogEmailAppService logEmailAppService;

    public ResponseGlobalModel<Object> doProcessPlacing(UspPlacingParam upp) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_PLACING_GET";
            params = buildParamProcedure(upp);

            Map<String, Object> procedureResult = repository.callPlacingProcedure(procedureName, params);

            int resultCode = (Integer) procedureResult.get("p_resultCode");
            String message = (String) procedureResult.get("p_message");
            String resultJson = (String) procedureResult.get("p_resultJson");

            responseGlobalModel.setResultCode(resultCode);
            responseGlobalModel.setMessage(message);

            if (resultJson == null || resultJson.trim().isEmpty()) {
                resultJson = "[]";
            }
            responseGlobalModel.setData(new Gson().fromJson(resultJson, List.class));
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessPlacingConfirm(UspPlacingParam upp) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_PLACING_CONFIRM_GET";
            params = buildParamProcedure(upp);

            Map<String, Object> procedureResult = repository.callPlacingProcedure(procedureName, params);

            int resultCode = (Integer) procedureResult.get("p_resultCode");
            String message = (String) procedureResult.get("p_message");
            System.out.println("Received JSON: " + procedureResult.get("p_resultJson"));
            String resultJson = (String) procedureResult.get("p_resultJson");

            responseGlobalModel.setResultCode(resultCode);
            responseGlobalModel.setMessage(message);

            if (resultJson == null || resultJson.trim().isEmpty()) {
                resultJson = "[]";
            }
            responseGlobalModel.setData(new Gson().fromJson(resultJson, List.class));
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessProposal(UspProposalParam upp) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_PROPOSAL_GET";
            params = buildParamProcedure(upp);

            Map<String, Object> procedureResult = repository.callPlacingProcedure(procedureName, params);

            int resultCode = (Integer) procedureResult.get("p_resultCode");
            String message = (String) procedureResult.get("p_message");
            String resultJson = (String) procedureResult.get("p_resultJson");

            responseGlobalModel.setResultCode(resultCode);
            responseGlobalModel.setMessage(message);

            if (resultJson == null || resultJson.trim().isEmpty()) {
                resultJson = "[]";
            }
            responseGlobalModel.setData(new Gson().fromJson(resultJson, List.class));
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessProposalRequest(UspProposalParam upp) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_PROPOSAL_REQUEST_GET";
            params = buildParamProcedure(upp);

            Map<String, Object> procedureResult = repository.callPlacingProcedure(procedureName, params);

            int resultCode = (Integer) procedureResult.get("p_resultCode");
            String message = (String) procedureResult.get("p_message");
            String resultJson = (String) procedureResult.get("p_resultJson");
            logger.info("resultCode===>", resultCode );
            logger.info("resultJson===>", resultJson );
            responseGlobalModel.setResultCode(resultCode);
            responseGlobalModel.setMessage(message);

            if (resultJson == null || resultJson.trim().isEmpty()) {
                resultJson = "[]";
            }
            responseGlobalModel.setData(new Gson().fromJson(resultJson, List.class));
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessComparation(UspComparationParam upp) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_COMPARATION_GET";
            params = buildParamProcedure(upp);

            Map<String, Object> procedureResult = repository.callPlacingProcedure(procedureName, params);
            logger.info("procedureResult==>{}", procedureResult);
            int resultCode = (Integer) procedureResult.get("p_resultCode");
            String message = (String) procedureResult.get("p_message");
            String resultJson = (String) procedureResult.get("p_resultJson");

            responseGlobalModel.setResultCode(resultCode);
            responseGlobalModel.setMessage(message);

            if (resultJson == null || resultJson.trim().isEmpty()) {
                resultJson = "[]";
            }
            responseGlobalModel.setData(new Gson().fromJson(resultJson, List.class));
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    //    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessPlacingIUD(PlacingRequestModel placingAccountModel, String actionType) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        String procedureName = "USP_PLACING";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<Map<String, Object>> emailResults = new ArrayList<>();
        try {


            if (actionType.equalsIgnoreCase("1")) {
                if (placingAccountModel.getActionType().equalsIgnoreCase("1")) {
                    if (repository.isBookCdPlaced(placingAccountModel.getBookCd())) {
                        responseGlobalModel.setResultCode(400);
                        responseGlobalModel.setMessage("BookCd is already placed.");
                        return responseGlobalModel;
                    }
                }
                logger.info("placingAccountModel===>before" + new Gson().toJson(placingAccountModel));
                UspPlacingAccountParam uspParam = PlacingAccountMapper.INSTANCE.toUspPlacingAccountParam(placingAccountModel);
                logger.info("uspParam===>" + new Gson().toJson(uspParam));

                String insPlacingJson = new Gson().toJson(placingAccountModel.getInsurances());
                uspParam.setInsPlacing(insPlacingJson);

                List<ProcedureParamModel> procedureParams = buildParamProcedure(uspParam);
                logger.info("procedureParams===>" + procedureParams);
                Map<String, Object> procedureResult = repository.callPlacingProcedure(procedureName, procedureParams);

                responseGlobalModel.setResultCode((Integer) procedureResult.get("p_resultCode"));
                responseGlobalModel.setMessage((String) procedureResult.get("p_message"));

                if (responseGlobalModel.getResultCode() == 200) {
                    responseGlobalModel.setData(new Gson().fromJson((String) procedureResult.get("p_resultJson"), Map.class));
                    Map<String, Object> responseData = (Map<String, Object>) responseGlobalModel.getData();
                    String placingCd = (String) responseData.get("placingCd") == null ? placingAccountModel.getPlacingCd() : (String) responseData.get("placingCd");
                    String bookCd = (String) responseData.get("bookCd") == null ? placingAccountModel.getBookCd() : (String) responseData.get("bookCd");

                    //diremark flow baru placing tidak kirim document //20250817
                    String mailType = "";
                    if (placingAccountModel.getActionType().equalsIgnoreCase("1")) {
                        mailType = "PLNEW";
                    } else if (placingAccountModel.getActionType().equalsIgnoreCase("2")) {
                        mailType = "PLREV";
                    }

                    if (!placingAccountModel.getInsurances().isEmpty()) {
                        for (PlacingRequestModel.Insurance insurance : placingAccountModel.getInsurances()) {

                            String insCd = String.valueOf(insurance.getInsCd());
                            List<String> attachmentUrls = new ArrayList<>();
                            List<String> attachmentNames = new ArrayList<>();
                            for (PlacingRequestModel.DocType docType : insurance.getDocTypes()) {
                                String urlPath = docType.getUrlPath();
                                if (urlPath != null && !urlPath.isEmpty()) {
                                    attachmentUrls.add(urlPath);
                                    String name = docType.getDescp();
                                    if (name != null && !name.isBlank()) {
                                        name = name.replace("<", "&lt;").replace(">", "&gt;");
                                        attachmentNames.add(name.trim());
                                    }
                                }
                            }

                            String attachmentListHtml =
                                    attachmentNames.isEmpty()
                                            ? "<li>Tidak ada dokumen</li>"
                                            : attachmentNames.stream()
                                            .map(n -> "<li>" + n + "</li>")
                                            .collect(java.util.stream.Collectors.joining());

                            EmailRequestModel emailRequestModel = new EmailRequestModel();
                            emailRequestModel.setMailType(mailType);
                            emailRequestModel.setCode(placingCd);
                            emailRequestModel.setBookCd(bookCd);
                            emailRequestModel.setActionType("2");
                            emailRequestModel.setCreatedBy(placingAccountModel.getCreatedBy() == null ? "System" : placingAccountModel.getCreatedBy());

                            Map<String, Object> templateData = new HashMap<>();
                            templateData.put("mailType", mailType);
                            templateData.put("actionType", "2");
                            templateData.put("placingCd", placingCd);
                            templateData.put("bookCd", bookCd);
                            templateData.put("code", placingCd);
                            templateData.put("insCd", insCd);
                            templateData.put("ATTACHMENT_LIST", attachmentListHtml);
                            emailRequestModel.setParamTemplate(templateData);
                            System.out.println("emailRequestModel==0>" + new Gson().toJson(emailRequestModel));
//                        ResponseGlobalModel<Object> emailResult = emailService.sendEmailWithAttachments(
//                                new Gson().toJson(emailRequestModel),
//                                serviceTool.convertUrlsToMultipartFiles(attachmentUrls),
//                                serviceTool.getProperty("email.service.url") + "/email/send"
//                        );

                            //insert Log Email
                            LogEmailApp log = new LogEmailApp();
                            log.setRefType("PLACING");
                            log.setRefId(placingCd);
                            log.setRefSubId(insCd);
                            log.setMailType(mailType);
                            log.setSubject(emailRequestModel.getSubject());
                            log.setMailTo("");
                            log.setAttachmentInfo(new Gson().toJson(attachmentUrls));
                            log.setStatus("PROCESSING");
                            log.setRequestDt(LocalDateTime.now());
                            log.setCreateBy(emailRequestModel.getCreatedBy());
                            LogEmailApp savedLog = logEmailAppService.createLog(log);
                            Long logId = savedLog.getId();

                            emailService.sendEmailWithAttachmentsAsync(
                                    emailRequestModel,
//                                    serviceTool.convertUrlsToMultipartFiles(attachmentUrls),
                                    null,
                                    serviceTool.getProperty("email.service.url") + "/email/send",
                                    insCd,
                                    logId
                            );

                            Map<String, Object> emailResultMap = new HashMap<>();
                            emailResultMap.put("insCd", insCd);
                            emailResultMap.put("resultCode", 200);
                            emailResultMap.put("message", "PROCESSING");
                            emailResults.add(emailResultMap);
                        }
                    }

                    ((Map<String, Object>) responseGlobalModel.getData()).put("emailResults", emailResults);

                    logger.info("Placing IUD successful: placingCd={}, bookCd={}, insurances={}, emailResults={}",
                            placingCd, bookCd, placingAccountModel.getInsurances(), emailResults);

                } else {
                    responseGlobalModel.setError(Map.of("error", String.valueOf(procedureResult.get("p_resultJson"))));
                }
            }
        } catch (Exception e) {
            logger.error("Error processing placing: ", e);
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage(e.getMessage());
            responseGlobalModel = ResponseGlobalModel.<Object>builder()
                    .resultCode(500)
                    .message("Failed process placing " + e.getMessage())
                    .error(Map.of("error", e.getMessage()))
                    .build();
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessPlacingResend(PlacingRequestModel placingAccountModel, String actionType) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        String procedureName = "USP_PLACING";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<Map<String, Object>> emailResults = new ArrayList<>();
        try {


            if (actionType.equalsIgnoreCase("2")) {

                logger.info("placingAccountModel===>before" + new Gson().toJson(placingAccountModel));
                UspPlacingAccountParam uspParam = PlacingAccountMapper.INSTANCE.toUspPlacingAccountParam(placingAccountModel);
                logger.info("uspParam===>" + new Gson().toJson(uspParam));

                String insPlacingJson = new Gson().toJson(placingAccountModel.getInsurances());
                uspParam.setInsPlacing(insPlacingJson);

                List<ProcedureParamModel> procedureParams = buildParamProcedure(uspParam);
                logger.info("procedureParams===>" + procedureParams);
                Map<String, Object> procedureResult = repository.callPlacingProcedure(procedureName, procedureParams);

                responseGlobalModel.setResultCode((Integer) procedureResult.get("p_resultCode"));
                responseGlobalModel.setMessage((String) procedureResult.get("p_message"));

                if (responseGlobalModel.getResultCode() == 200) {
                    responseGlobalModel.setData(new Gson().fromJson((String) procedureResult.get("p_resultJson"), Map.class));
                    Map<String, Object> responseData = (Map<String, Object>) responseGlobalModel.getData();
                    String placingCd = (String) responseData.get("placingCd") != null ? (String) responseData.get("placingCd") : placingAccountModel.getPlacingCd();
                    String bookCd = (String) responseData.get("bookCd") != null ? (String) responseData.get("bookCd") : placingAccountModel.getBookCd();

                    String mailType = "";
                    if (placingAccountModel.getActionType().equalsIgnoreCase("1")) {
                        mailType = "PLNEW";
                    } else if (placingAccountModel.getActionType().equalsIgnoreCase("2")) {
                        mailType = "PLNEW";
                    }
                    for (PlacingRequestModel.Insurance insurance : placingAccountModel.getInsurances()) {

                        String insCd = String.valueOf(insurance.getInsCd());
                        List<String> attachmentUrls = new ArrayList<>();
                        for (PlacingRequestModel.DocType docType : insurance.getDocTypes()) {
                            String urlPath = docType.getUrlPath();
                            if (urlPath != null && !urlPath.isEmpty()) {
                                attachmentUrls.add(urlPath);
                            }
                        }

                        EmailRequestModel emailRequestModel = new EmailRequestModel();
                        emailRequestModel.setMailType(mailType);
                        emailRequestModel.setCode(placingCd);
                        emailRequestModel.setBookCd(bookCd);
                        emailRequestModel.setActionType("2");
                        emailRequestModel.setCreatedBy(placingAccountModel.getCreatedBy() == null ? "System" : placingAccountModel.getCreatedBy());

                        Map<String, Object> templateData = new HashMap<>();
                        templateData.put("mailType", mailType);
                        templateData.put("actionType", "2");
                        templateData.put("placingCd", placingCd);
                        templateData.put("bookCd", bookCd);
                        templateData.put("code", placingCd);
                        templateData.put("insCd", insCd);
                        emailRequestModel.setParamTemplate(templateData);
                        System.out.println("emailRequestModel==0>" + new Gson().toJson(emailRequestModel));
//                        ResponseGlobalModel<Object> emailResult = emailService.sendEmailWithAttachments(
//                                new Gson().toJson(emailRequestModel),
//                                serviceTool.convertUrlsToMultipartFiles(attachmentUrls),
//                                serviceTool.getProperty("email.service.url") + "/email/send"
//                        );

                        //insert Log Email
                        LogEmailApp log = new LogEmailApp();
                        log.setRefType("PLACING");
                        log.setRefId(placingCd);
                        log.setRefSubId(insCd);
                        log.setMailType(mailType);
                        log.setSubject(emailRequestModel.getSubject());
                        log.setMailTo("");
                        log.setAttachmentInfo(new Gson().toJson(attachmentUrls));
                        log.setStatus("PROCESSING");
                        log.setRequestDt(LocalDateTime.now());
                        log.setCreateBy(emailRequestModel.getCreatedBy());
                        LogEmailApp savedLog = logEmailAppService.createLog(log);
                        Long logId = savedLog.getId();
                        logger.info("Log Email created with ID: {}", log);
                        emailService.sendEmailWithAttachmentsAsync(
                                emailRequestModel,
                                serviceTool.convertUrlsToMultipartFiles(attachmentUrls),
                                serviceTool.getProperty("email.service.url") + "/email/send",
                                insCd,
                                logId
                        );

                        Map<String, Object> emailResultMap = new HashMap<>();
                        emailResultMap.put("insCd", insCd);
                        emailResultMap.put("resultCode", 200);
                        emailResultMap.put("message", "PROCESSING");
                        emailResults.add(emailResultMap);
                    }
                    ((Map<String, Object>) responseGlobalModel.getData()).put("emailResults", emailResults);

                    logger.info("Placing IUD successful: placingCd={}, bookCd={}, insurances={}, emailResults={}",
                            placingCd, bookCd, placingAccountModel.getInsurances(), emailResults);

                } else {
                    responseGlobalModel.setError(Map.of("error", String.valueOf(procedureResult.get("p_resultJson"))));
                }
            }
        } catch (Exception e) {
            logger.error("Error processing placing: ", e);
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage(e.getMessage());
            responseGlobalModel = ResponseGlobalModel.<Object>builder()
                    .resultCode(500)
                    .message("Failed process placing " + e.getMessage())
                    .error(Map.of("error", e.getMessage()))
                    .build();
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doCheckEmail(EmailCheckModel emailCheckModel) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        try {
            EmailCheckResult emailCheckResult = repository.checkEmailAlready(
                    emailCheckModel.getCode(),
                    emailCheckModel.getParam1(),
                    emailCheckModel.getParam2(),
                    emailCheckModel.getParam3(),
                    emailCheckModel.getActionType()
            );

            responseGlobalModel.setResultCode(emailCheckResult.getResultCode());
            responseGlobalModel.setMessage(emailCheckResult.getMessage());
            responseGlobalModel.setData(emailCheckResult);
        } catch (Exception e) {
            logger.error("Error checking email: ", e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error: " + e.getMessage());
        }
        return responseGlobalModel;

    }

    private <T> List<ProcedureParamModel> buildParamProcedure(T model) {
        List<ProcedureParamModel> params = new ArrayList<>();
        Field[] fields = model.getClass().getDeclaredFields();
        Gson gson = new Gson();

        try {


            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(model);

                String paramName = "p_" + toSnakeCase(field.getName());

                if ("files".equals(field.getName())) continue;

                // Handle List docTypes
                if (field.getName().equalsIgnoreCase("docTypes") && value instanceof List) {
                    String jsonString = gson.toJson(value);
                    params.add(new ProcedureParamModel("p_docListJson", jsonString, String.class, ParameterMode.IN));
                } else if (value instanceof Map) {
                    continue;
                }

                // Handle normal parameter
                else {
                    params.add(new ProcedureParamModel(
                            paramName,
                            value != null ? value : "",
                            field.getType(),
                            ParameterMode.IN
                    ));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error building procedure parameters", e);
        }

        // Tambahkan parameter OUT
        params.add(new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT));

        return params;
    }

    // Utility to convert camelCase to snake_case
    private String toSnakeCase(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessPlacingConfirm(String data) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        List<Map<String, Object>> insuranceResults = new ArrayList<>();

        try {
            PlacingConfirmRequestParam uspParam = gson.fromJson(data, PlacingConfirmRequestParam.class);
            String placingCd = uspParam.getPlacingCd();
            String bookCd = uspParam.getBookCd();


            for (PlacingConfirmRequestParam.InsuranceItem insurance : uspParam.getInsurances()) {
                // Set file path & update doc url
                String insCd = String.valueOf(insurance.getInsCd());
                String actionType = uspParam.getActionType();

                List<ProcedureParamModel> procParams = List.of(
                        new ProcedureParamModel("p_placingCd", placingCd, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_bookCd", bookCd, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_actionType", actionType, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_confirmDt", insurance.getConfirmDate(), Date.class, ParameterMode.IN),
                        new ProcedureParamModel("p_user", uspParam.getCreateBy(), String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_insCd", insurance.getInsCd(), Long.class, ParameterMode.IN),
                        new ProcedureParamModel("p_description", insurance.getDescription(), String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_proposalDt", insurance.getProposalDate(), Date.class, ParameterMode.IN),
                        new ProcedureParamModel("p_premiumTot", insurance.getPremium(), BigDecimal.class, ParameterMode.IN),
                        new ProcedureParamModel("p_compct", insurance.getCommission(), BigDecimal.class, ParameterMode.IN),
                        new ProcedureParamModel("p_docListJson", "", String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT),
                        new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT),
                        new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT)
                );

                logger.info("procParams====> {}", procParams);

                Map<String, Object> result = repository.callPlacingProcedure("USP_PLACING_CONFIRM", procParams);
                int resultCode = (Integer) result.get("p_resultCode");

                if (resultCode != 200) {
                    responseGlobalModel.setResultCode(resultCode);
                    responseGlobalModel.setMessage((String) result.get("p_message"));
                    responseGlobalModel.setError(Collections.singletonMap("error", String.valueOf(result.get("p_resultJson"))));
                    logger.error("❌ Failed for insCd={}, msg={} , responseGlobalModel={}", insCd, (String) result.get("p_message"), responseGlobalModel);
                    return responseGlobalModel;
                }

                Map<String, Object> responseData = gson.fromJson((String) result.get("p_resultJson"), Map.class);

                // Simpan hasil per insurance
                insuranceResults.add(responseData);
                logger.info("✅ Success Placing Confirm: placingCd={}, insCd={}, revDoc={} , responseData={}", placingCd, insCd, "", responseData);
            }

            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage("Placing confirm berhasil");
            responseGlobalModel.setData(insuranceResults);

        } catch (Exception e) {
            logger.error("❌ Error processing placing confirm: ", e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error: " + e.getMessage());
        }

        return responseGlobalModel;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessInsProposal(Map<String, MultipartFile> files, String data) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        List<Map<String, Object>> insuranceResults = new ArrayList<>();

        try {
            PlacingConfirmRequestParam uspParam = gson.fromJson(data, PlacingConfirmRequestParam.class);
            String placingCd = uspParam.getPlacingCd();
            String bookCd = uspParam.getBookCd();

            // Get max rev from documen proposal for folder path
            UspBookingAccountGetParam revParam = new UspBookingAccountGetParam();
            revParam.setBookCd(bookCd);
            revParam.setActionType("3");

            List<ProcedureParamModel> revParams = buildParamProcedure(revParam);
            Map<String, Object> revResult = repository.callPlacingProcedure("USP_BOOKING_ACCOUNT_GET", revParams);
            int revCode = (Integer) revResult.get("p_resultCode");
            String revJson = (String) revResult.get("p_resultJson");

            if (revCode != 200 || revJson == null) {
                responseGlobalModel.setResultCode(revCode);
                responseGlobalModel.setError(Map.of("error", "Gagal ambil max rev dari booking"));
                return responseGlobalModel;
            }

            Map<String, Object> revMap = new Gson().fromJson(revJson, Map.class);
            String maxRev = String.valueOf(revMap.get("maxRev"));
            String baseFolder = dataUtil.getPathUpload();

            System.out.println("baseFolder===>" + baseFolder);

            for (PlacingConfirmRequestParam.InsuranceItem insurance : uspParam.getInsurances()) {
                // Set file path & update doc url
                String insCd = String.valueOf(insurance.getInsCd());
                String actionType = uspParam.getActionType();
                String revDoc = String.valueOf(insurance.getDocTypes().get(0).getRevDoc());

                int nextRevDoc = repository.getNextProposalRev(placingCd, Long.valueOf(insCd));

                String targetPath = baseFolder + "/" + bookCd + "/rev" + maxRev + "/" + placingCd;

                if (insurance.getDocTypes() != null) {
                    for (PlacingRequestModel.DocType doc : insurance.getDocTypes()) {
                        String fileKey = insurance.getInsCd() + "-" + doc.getCode();
                        logger.info("🔍Checking fileKey: {}", fileKey);
                        if (files != null && !files.isEmpty()) {
                            MultipartFile file = files.get(fileKey);
                            if (file != null && !file.isEmpty()) {
                                // Save using new storage service per module=proposal and fixed subfolder Proposal
                                java.nio.file.Path savedPath = fileStorageService.saveWithOverwriteAndBackup(
                                        "proposal", bookCd, Integer.valueOf(maxRev), placingCd, "Proposal", file, uspParam.getCreateBy()
                                );
                                String urlPath = savedPath.toString().replace('\\', '/');
                                // Update urlPath
                                doc.setUrlPath(urlPath);
                            }
                        }

                    }
                }

                String docJson = "";
                if (actionType.equalsIgnoreCase("1")) {
                    docJson = gson.toJson(insurance.getDocTypes());
                } else {
                    if (files != null && !files.isEmpty()) {
                        docJson = gson.toJson(insurance.getDocTypes());
                    }
                }


                logger.info("dataJson====> {}", docJson);
                logger.info("Length dataJson====> {}", docJson.length());

                List<ProcedureParamModel> procParams = List.of(
                        new ProcedureParamModel("p_placingCd", placingCd, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_bookCd", bookCd, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_actionType", actionType, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_confirmDt", insurance.getConfirmDate(), Date.class, ParameterMode.IN),
                        new ProcedureParamModel("p_user", uspParam.getCreateBy(), String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_insCd", insurance.getInsCd(), Long.class, ParameterMode.IN),
                        new ProcedureParamModel("p_description", insurance.getDescription(), String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_proposalDt", insurance.getProposalDate(), Date.class, ParameterMode.IN),
                        new ProcedureParamModel("p_premiumTot", insurance.getPremium(), BigDecimal.class, ParameterMode.IN),
                        new ProcedureParamModel("p_compct", insurance.getCommission(), BigDecimal.class, ParameterMode.IN),
                        new ProcedureParamModel("p_docListJson", docJson, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_rev", actionType.equalsIgnoreCase("1") ? maxRev : revDoc, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT),
                        new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT),
                        new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT)
                );

                logger.info("procParams====> {}", procParams);

                Map<String, Object> result = repository.callPlacingProcedure("USP_PROPOSAL", procParams);
                int resultCode = (Integer) result.get("p_resultCode");

                if (resultCode != 200) {
                    responseGlobalModel.setResultCode(resultCode);
                    responseGlobalModel.setMessage((String) result.get("p_message"));
                    responseGlobalModel.setError(Collections.singletonMap("error", String.valueOf(result.get("p_resultJson"))));
                    logger.error("❌ Failed for insCd={}, msg={} , responseGlobalModel={}", insCd, (String) result.get("p_message"), responseGlobalModel);
                    return responseGlobalModel;
                }

                Map<String, Object> responseData = gson.fromJson((String) result.get("p_resultJson"), Map.class);

                // Simpan hasil per insurance
                insuranceResults.add(responseData);
                logger.info("✅ Success Proposal Doc: placingCd={}, insCd={}, revDoc={} , responseData={}", placingCd, insCd, nextRevDoc, responseData);
            }

            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage("Placing confirm berhasil");
            responseGlobalModel.setData(insuranceResults);

        } catch (Exception e) {
            logger.error("❌ Error processing placing confirm: ", e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error: " + e.getMessage());
        }

        return responseGlobalModel;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessInsComparation(Map<String, MultipartFile> files, String data) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        try {
            ComparationRequestModel uspParam = gson.fromJson(data, ComparationRequestModel.class);
            logger.info("uspParam===> {}", new Gson().toJson(uspParam));
            String comparationCd = uspParam.getComparationCd();
            String revDoc = uspParam.getRevDoc();
            List<ComparationRequestModel.ComparationModel> revisions = uspParam.getComparations();
            if (comparationCd == null || comparationCd.isBlank()) {
                if (revisions != null && !revisions.isEmpty()) {
                    ComparationRequestModel.ComparationModel lastRevision = revisions.get(revisions.size() - 1);
                    if (lastRevision.getComparationCd() != null && !lastRevision.getComparationCd().isBlank()) {
                        comparationCd = lastRevision.getComparationCd();
                    }

                }
            }
            if (revDoc == null || revDoc.isBlank()) {
                if (revisions != null && !revisions.isEmpty()) {
                    ComparationRequestModel.ComparationModel lastRevision = revisions.get(revisions.size() - 1);
                    if (lastRevision.getRev() != null && !lastRevision.getRev().isBlank()) {
                        revDoc = lastRevision.getRev();
                    } else if (lastRevision.getRevDoc() != null && !lastRevision.getRevDoc().isBlank()) {
                        revDoc = lastRevision.getRevDoc();
                    }
                }
            }


            String placingCd = uspParam.getPlacingCd();
            String bookCd = uspParam.getBookCd();
            // Auto-resolve actionType for HEADER based on comparationCd presence
            String actionTypeHdr;
            if (comparationCd == null || comparationCd.isBlank()) {
                actionTypeHdr = "1"; // create & generate code
            } else {
                actionTypeHdr = "2"; // update header
            }

            String urlPath = null;
            String baseFolder = dataUtil.getPathUpload();
            String description = uspParam.getDescription();
            String emailRemark = uspParam.getEmailRemark();
            String user = uspParam.getCreateBy();

            String finalRevDoc = revDoc;
            List<Map<String, Object>> docList = uspParam.getInsurances().stream()
                    .map(ins -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("insCd", ins.getInsCd());
                        map.put("revDoc", finalRevDoc);
                        return map;
                    })
                    .collect(Collectors.toList());

            String docListJson = gson.toJson(docList);

            // Emergency Force params
            final boolean force = Boolean.TRUE.equals(uspParam.getForce());
            final String forceMode = uspParam.getForceMode();
            final String forceReason = uspParam.getForceReason();
            final List<String> forcedInsurers = Optional.ofNullable(uspParam.getForcedInsurers()).orElse(List.of());

            if (force) {
                if (forcedInsurers.isEmpty()) {
                    throw new IllegalArgumentException("forcedInsurers is required when force=true");
                }
                if (forceReason == null || forceReason.isBlank()) {
                    throw new IllegalArgumentException("forceReason is required when force=true");
                }
            }
            // Convert to NUMBER[] JSON (Long)
            final List<Long> forcedInsNumeric = forcedInsurers.stream()
                    .map(s -> {
                        try { return Long.valueOf(s); } catch (Exception e) { return null; }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            final String forcedInsJson = forcedInsNumeric.isEmpty() ? "[]" : gson.toJson(forcedInsNumeric);

            logger.info("baseFolder====>" + baseFolder);
            if (files != null && !files.isEmpty()) {
                for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
                    String docType = entry.getKey(); // ini adalah docType (misal: "8")
                    MultipartFile file = entry.getValue();

                    if (file != null && !file.isEmpty()) {
                        java.nio.file.Path savedPath = fileStorageService.saveWithOverwriteAndBackup(
                                "comparation", bookCd, Integer.valueOf(revDoc), placingCd, "comparation", file, uspParam.getCreateBy()
                        );
                        String relativePath = savedPath.toString().replace('\\', '/');
                        logger.info("📤 File uploaded for docType {}: {}", docType, relativePath);
                        urlPath = relativePath;
                        break;
                    }
                }
            }

            logger.info("relativePath===>" + urlPath);

            for (ComparationRequestModel.ComparationModel comparationModel : uspParam.getComparations()) {
                if (urlPath != null && !urlPath.isBlank()) {
                    comparationModel.setUrlPath(urlPath);
                }
                comparationCd = comparationModel.getComparationCd();
                if (comparationCd == null || comparationCd.isBlank()) {
                    comparationCd = uspParam.getComparationCd();
                }
                description = comparationModel.getDescription();
                if (description == null || description.isBlank()) {
                    description = uspParam.getDescription();
                }
                emailRemark = comparationModel.getEmailRemark();
                if (emailRemark == null || emailRemark.isBlank()) {
                    emailRemark = uspParam.getEmailRemark();
                }
                if (urlPath == null || urlPath.isBlank()) {
                    urlPath = comparationModel.getUrlPath();
                }
                List<ProcedureParamModel> procParams = List.of(
                        new ProcedureParamModel("p_comparationCd", comparationCd, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_placingCd", placingCd, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_urlPath", urlPath, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_description", description, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_emailRemark", emailRemark, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_rev", revDoc, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_docListJson", docListJson, String.class, ParameterMode.IN),

                        // Emergency Force params
                        new ProcedureParamModel("p_force",       force ? "1" : "0", String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_forceMode",   forceMode,          String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_forceReason", force ? forceReason : null, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_forcedIns",   forcedInsJson,      String.class, ParameterMode.IN),

                        new ProcedureParamModel("p_user", user, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_actionType", actionTypeHdr, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT),
                        new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT),
                        new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT)
                );

                Map<String, Object> result = repository.callPlacingProcedure("USP_COMPARATION", procParams);
                String resultJson = (String) result.get("p_resultJson");
                logger.info("[USP_COMPARATION#{}] resultCode={}, message={}, p_resultJson={}",
                            actionTypeHdr, result.get("p_resultCode"), result.get("p_message"), resultJson);
                JsonObject obj = JsonParser.parseString(resultJson).getAsJsonObject();

                // Safe extraction of comparationCd
                String spComparationCd = null;
                if (obj.has("comparationCd") && !obj.get("comparationCd").isJsonNull()) {
                    spComparationCd = obj.get("comparationCd").getAsString();
                }
                if ((comparationCd == null || comparationCd.isBlank()) && (spComparationCd != null && !spComparationCd.isBlank())) {
                    comparationCd = spComparationCd;
                    uspParam.setComparationCd(comparationCd);
                }
                if (comparationCd == null || comparationCd.isBlank()) {
                    throw new IllegalStateException("SP did not return comparationCd for actionType=" + actionTypeHdr);
                }

                Map<String, Object> resultDtl = new HashMap<>();
                int resultCode = (Integer) result.get("p_resultCode");

                if (resultCode != 200) {
                    responseGlobalModel.setResultCode(resultCode);
                    responseGlobalModel.setMessage((String) result.get("p_message"));
                    responseGlobalModel.setError(Collections.singletonMap("error", String.valueOf(result.get("p_resultJson"))));
                    logger.error("❌ Failed insert comparation: {}", responseGlobalModel);

                    return responseGlobalModel;
                } else {
                    List<ProcedureParamModel> procParamsDetail = List.of(
                            new ProcedureParamModel("p_comparationCd", comparationCd, String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_placingCd", placingCd, String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_urlPath", urlPath, String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_description", description, String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_emailRemark", emailRemark, String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_rev", revDoc, String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_docListJson", docListJson, String.class, ParameterMode.IN),

                            // Emergency Force params
                            new ProcedureParamModel("p_force",       force ? "1" : "0", String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_forceMode",   forceMode,          String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_forceReason", force ? forceReason : null, String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_forcedIns",   forcedInsJson,      String.class, ParameterMode.IN),

                            new ProcedureParamModel("p_user", user, String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_actionType", "4", String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT),
                            new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT),
                            new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT)
                    );
                    resultDtl = repository.callPlacingProcedure("USP_COMPARATION", procParamsDetail);
                    logger.info("[USP_COMPARATION#4] resultCode={}, message={}, p_resultJson={}",
                                resultDtl.get("p_resultCode"), resultDtl.get("p_message"), (String) resultDtl.get("p_resultJson"));
                    resultCode = (Integer) resultDtl.get("p_resultCode");
                    if (resultCode == 200) {
                        logger.info("✅ Comparation inserted successfully: comparationCd={}, placingCd={}, urlPath={}, description={}, emailRemark={}, revDoc={}, uspParam={}",
                                comparationCd, placingCd, urlPath, description, emailRemark, revDoc, uspParam);
                        uspParam.setComparationCd(comparationCd);

                        // Optional: add disclaimer email when emergency force is applied
                        if (force) {
                            try {
                                String forcedNames = String.join(", ", forcedInsurers);
                                String disclaimer = "Emergency Compare applied to: " + forcedNames + ". Files for the current revision are pending.";
                                String existing = uspParam.getEmailRemark();
                                uspParam.setEmailRemark((existing == null || existing.isBlank()) ? disclaimer : (existing + "\n" + disclaimer));
                            } catch (Exception ignore) {}
                        }

                        responseGlobalModel = doProcessSendComparation(gson.toJson(uspParam));
                    }
                }
                Map<String, Object> responseData = gson.fromJson((String) resultDtl.get("p_resultJson"), Map.class);
                responseGlobalModel.setResultCode(200);
                responseGlobalModel.setMessage("Comparation success");
                responseGlobalModel.setData(responseData);
            }
        } catch (Exception e) {
            logger.error("❌ Error processing placing confirm: ", e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error: " + e.getMessage());
        }

        return responseGlobalModel;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessInsProposalRevision(PlacingRequestModel proposalRevision) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        List<Map<String, Object>> insuranceResults = new ArrayList<>();

        try {

            String placingCd = proposalRevision.getPlacingCd();
            String bookCd = proposalRevision.getBookCd();
            String revHeader = proposalRevision.getRev();

            for (PlacingRequestModel.Insurance insurance : proposalRevision.getInsurances()) {
                // Set file path & update doc url
                String insCd = String.valueOf(insurance.getInsCd());
                String actionType = proposalRevision.getActionType();
                String revDoc = insurance.getDocTypes() != null && !insurance.getDocTypes().isEmpty()
                        ? String.valueOf(insurance.getDocTypes().get(0).getRevDoc())
                        : null;

                String pRev = (revHeader != null && !revHeader.isEmpty()) ? revHeader : (revDoc != null ? revDoc : "");


                List<PlacingRequestModel.DocType> docTypes = insurance.getDocTypes();
                String docJson = gson.toJson(docTypes);

                String proposalDtStr = proposalRevision.getPlacingDate();
                Date proposalDt = null;
                if (proposalDtStr != null && !proposalDtStr.isEmpty()) {
                    proposalDt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(proposalDtStr);
                }
                String proposalDtDtlStr = insurance.getPlacingDate();
                Date proposalDtDtl = null;
                if (proposalDtDtlStr != null && !proposalDtDtlStr.isEmpty()) {
                    proposalDtDtl = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(proposalDtDtlStr);
                }

                List<ProcedureParamModel> procParams = List.of(
                        new ProcedureParamModel("p_placingCd", placingCd, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_bookCd", bookCd, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_actionType", actionType, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_confirmDt", new Date(), Date.class, ParameterMode.IN),
                        new ProcedureParamModel("p_user", proposalRevision.getCreatedBy(), String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_insCd", insurance.getInsCd(), Long.class, ParameterMode.IN),
                        new ProcedureParamModel("p_description", proposalRevision.getDescription() == null ? "" : proposalRevision.getDescription(), String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_descriptionDtl", insurance.getDescriptionDtl() == null ? "" : insurance.getDescriptionDtl(), String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_proposalDt", proposalDt, Date.class, ParameterMode.IN),
                        new ProcedureParamModel("p_proposalDtDtl", proposalDtDtl, Date.class, ParameterMode.IN),
                        new ProcedureParamModel("p_premiumTot", 0, BigDecimal.class, ParameterMode.IN),
                        new ProcedureParamModel("p_compct", 0, BigDecimal.class, ParameterMode.IN),
                        new ProcedureParamModel("p_docListJson", docJson, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_rev", pRev, String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT),
                        new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT),
                        new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT)
                );

                logger.info("procParams====> {}", procParams);

                Map<String, Object> result = repository.callPlacingProcedure("USP_PROPOSAL_REQUEST", procParams);
                int resultCode = (Integer) result.get("p_resultCode");

                if (resultCode != 200) {
                    responseGlobalModel.setResultCode(resultCode);
                    responseGlobalModel.setMessage((String) result.get("p_message"));
                    responseGlobalModel.setError(Collections.singletonMap("error", String.valueOf(result.get("p_resultJson"))));
                    logger.error("❌ Failed for insCd={}, msg={} , responseGlobalModel={}", insCd, (String) result.get("p_message"), responseGlobalModel);
                    return responseGlobalModel;
                }

                Map<String, Object> responseData = gson.fromJson((String) result.get("p_resultJson"), Map.class);

                // Simpan hasil per insurance
                insuranceResults.add(responseData);
                logger.info("✅ Success Proposal Doc: placingCd={}, insCd={}, revDoc={} , responseData={}", placingCd, insCd, revDoc, responseData);
            }

            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage("Proposal Revision Success");
            responseGlobalModel.setData(insuranceResults);

        } catch (Exception e) {
            logger.error("❌ Error processing proposal revision: ", e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error: " + e.getMessage());
        }

        return responseGlobalModel;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessSendComparation(String data) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        ResponseGlobalModel<Object> responseEmailGlobalModel = new ResponseGlobalModel<>();
        EmailRequestModel emailRequestModel = new EmailRequestModel();

        Map<String, Object> templateData = new HashMap<>();
        String responseData = "";
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        ComparationRequestModel uspParam = gson.fromJson(data, ComparationRequestModel.class);
        try {
            logger.info("uspParamdoProcessSendComparation===> {}", new Gson().toJson(uspParam));

            String placingCd = uspParam.getPlacingCd();
            String bookCd = uspParam.getBookCd();
            String actionType = uspParam.getActionType();
            String revDoc = uspParam.getRevDoc();
            String baseFolder = dataUtil.getPathUpload();
            String user = uspParam.getCreateBy();

            List<ComparationRequestModel.ComparationModel> comparations = uspParam.getComparations();

            if (comparations != null && !comparations.isEmpty()) {
                // Ambil comparation dengan rev terbesar
                Optional<ComparationRequestModel.ComparationModel> latestOpt = comparations.stream()
                        .max(Comparator.comparingInt(c -> Integer.parseInt(c.getRev())));

                if (latestOpt.isPresent()) {
                    ComparationRequestModel.ComparationModel latest = latestOpt.get();
                    String comparationCd = uspParam.getComparationCd();
                    String description = latest.getDescription();
                    String emailRemark = latest.getEmailRemark();
                    String fileName = latest.getFileName();
                    String urlPath = latest.getUrlPath();
                    String sendDate = latest.getSendDate();
                    List<String> insurances = latest.getInsurances();
                    String rev = latest.getRev();

                    // Contoh print / mapping ke DTO baru / masukkan ke map:
                    System.out.println("comparationCd: " + comparationCd);
                    System.out.println("description: " + description);
                    System.out.println("emailRemark: " + emailRemark);
                    System.out.println("fileName: " + fileName);
                    System.out.println("urlPath===>: " + urlPath);
                    System.out.println("sendDate: " + sendDate);
                    System.out.println("insurances: " + insurances);
                    System.out.println("rev: " + rev);

                    templateData.put("comparationCd", comparationCd);
                    templateData.put("description", description);
                    templateData.put("emailRemark", emailRemark);
                    templateData.put("fileName", fileName);
                    templateData.put("urlPath", urlPath);
                    templateData.put("sendDate", sendDate);
                    templateData.put("insurances", insurances);
                    templateData.put("rev", rev);
                    templateData.put("actionType", uspParam.getActionType());
                    templateData.put("placingCd", placingCd);
                    templateData.put("bookCd", bookCd);
                    templateData.put("code", comparationCd);
                    templateData.put("insCd", "");

                    String insuranceList = comparations.stream()
                            .max(Comparator.comparingInt(c -> Integer.parseInt(c.getRev())))
                            .map(ComparationRequestModel.ComparationModel::getInsurances)
                            .map(serviceTool::joinArray)
                            .orElse("");
                    logger.info("insuranceList==>" + insuranceList);

                    String mailType = "";
                    if (actionType.equalsIgnoreCase("1") || actionType.equalsIgnoreCase("4")) {
                        mailType = "PLCP";
                        emailRequestModel.setActionType("3");
                    } else if (actionType.equalsIgnoreCase("2")) {
                        mailType = "CPCL";
                        emailRequestModel.setActionType("4");
                    }

                    emailRequestModel.setMailType(mailType);
                    emailRequestModel.setCode(comparationCd);
                    emailRequestModel.setBookCd(bookCd);
                    emailRequestModel.setCreatedBy(uspParam.getCreateBy() == null ? "System" : uspParam.getCreateBy());
                    emailRequestModel.setParamTemplate(templateData);
                    System.out.println("emailRequestModel==>" + new Gson().toJson(emailRequestModel));
                    if (!"1".equalsIgnoreCase(actionType)) {
                        responseEmailGlobalModel = emailService.sendEmailWithAttachments(
                                new Gson().toJson(emailRequestModel),
                                serviceTool.convertUrlsToMultipartFiles(Collections.singletonList(urlPath)),
                                serviceTool.getProperty("email.service.url") + "/email/send"
                        );
                        logger.info("responseData===>" + new Gson().toJson(responseEmailGlobalModel));
                    } else {
                        responseEmailGlobalModel = emailService.sendEmailWithAttachments(
                                new Gson().toJson(emailRequestModel),
                                serviceTool.convertUrlsToMultipartFiles(Collections.singletonList(urlPath)),
                                serviceTool.getProperty("email.service.url") + "/email/send"
                        );
                    }
                }
            }

//            Map<String, Object> responseData = null ; // gson.fromJson((String) result.get("p_resultJson"), Map.class);
            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage(responseEmailGlobalModel.getMessage());
            responseGlobalModel.setData(responseEmailGlobalModel.getData());

        } catch (Exception e) {
            logger.error("❌ Error processing placing confirm: ", e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error: " + e.getMessage());
        }
        return responseGlobalModel;
    }
}