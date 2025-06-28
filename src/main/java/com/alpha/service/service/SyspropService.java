package com.alpha.service.service;

import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.model.SystemPropertiesModel;
import com.alpha.service.model.comparation.ComparationRequestModel;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.model.sendemail.EmailRequestModel;
import com.alpha.service.model.sendemail.ErrorReportRequestModel;
import com.alpha.service.repository.SyspropRepository;
import com.alpha.service.service.sendemail.EmailService;
import com.alpha.service.util.ServiceTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.persistence.ParameterMode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SyspropService {
    @Autowired
    private SyspropRepository repository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ServiceTool serviceTool;
    private final Logger logger = LoggerFactory.getLogger(SyspropService.class);

    public ResponseGlobalModel<Object> callSyspropProcedure(SystemPropertiesModel request) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        List<Object[]> results = repository.callStoredProcedure(
                "SP_SYSPROP",
                Arrays.asList(
                        new ProcedureParamModel("p_code", request.getCode(), String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_type", request.getType(), String.class, ParameterMode.IN),
                        new ProcedureParamModel("p_actionType", request.getActionType(), String.class, ParameterMode.IN)
                )
        );
//        logger.info("results===>" + results.toString());
        responseGlobalModel.setResultCode(200);
        responseGlobalModel.setMessage("Data Master");
        responseGlobalModel.setData(
                results.stream()
                        .map(row -> {
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("code", row[0]);
                            map.put("descp", row[1]);
                            return map;
                        })
                        .collect(Collectors.toList()));
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessSendMailError(ErrorReportRequestModel data) {
        EmailRequestModel emailRequestModel = new EmailRequestModel();
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        ResponseGlobalModel<Object> responseEmailGlobalModel = new ResponseGlobalModel<>();
        try {
            // Buat map template untuk inject ke email HTML
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("PROJECTNAME", data.getProjectName());
            templateData.put("ERRORTIME", data.getErrorTime());
            templateData.put("USERINFO", data.getUser());
            templateData.put("URL", data.getUrl());
            templateData.put("USERNOTE", data.getUserNote());
            templateData.put("USERMESSAGE", data.getUserMessage());
            templateData.put("ERRORDETAIL", data.getErrorDetail());

            emailRequestModel.setMailType("ERR");
            emailRequestModel.setCreatedBy(data.getUser() == null ? "System" : data.getUser());
            emailRequestModel.setParamTemplate(templateData);
            logger.info("emailRequestModel===>" + new GsonBuilder().setPrettyPrinting().create().toJson(emailRequestModel));
            // Kirim email (penerima ke dev, subject disesuaikan)
            responseEmailGlobalModel = emailService.sendEmailWithAttachments(
                    new Gson().toJson(emailRequestModel),
                    Collections.emptyList(),
                    serviceTool.getProperty("email.service.url") + "/email/send"
            );

            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage(responseEmailGlobalModel.getMessage());
            responseGlobalModel.setData(responseEmailGlobalModel.getData());

            return responseGlobalModel;
        } catch (Exception e) {
            // Log error di sini kalau gagal kirim email
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error: " + e.getMessage());
            return responseGlobalModel;
        }
    }
}
