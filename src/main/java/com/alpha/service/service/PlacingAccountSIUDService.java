package com.alpha.service.service;


import com.alpha.service.mapper.PlacingAccountMapper;
import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.model.placing.PlacingRequestModel;
import com.alpha.service.model.procedure.UspBookingAccountGetParam;
import com.alpha.service.model.procedure.UspPlacingAccountDtlParam;
import com.alpha.service.model.procedure.UspPlacingAccountParam;
import com.alpha.service.model.procedure.UspPlacingParam;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.repository.PlacingAccountRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.persistence.ParameterMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
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

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessPlacingIUD(PlacingRequestModel placingAccountModel, String actionType) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        String procedureName = "USP_PLACING";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            if (actionType.equalsIgnoreCase("1")) {
                UspPlacingAccountParam uspParam = PlacingAccountMapper.INSTANCE.toUspPlacingAccountParam(placingAccountModel);

                String insPlacingJson = new Gson().toJson(placingAccountModel.getInsurances());
                uspParam.setInsPlacing(insPlacingJson);

                List<ProcedureParamModel> procedureParams = buildParamProcedure(uspParam);
                logger.info("procedureParams===>" + procedureParams);
                Map<String, Object> procedureResult = repository.callPlacingProcedure(procedureName, procedureParams);

                responseGlobalModel.setResultCode((Integer) procedureResult.get("p_resultCode"));
                responseGlobalModel.setMessage((String) procedureResult.get("p_message"));

                if (responseGlobalModel.getResultCode() == 200) {
                    responseGlobalModel.setData(new Gson().fromJson((String) procedureResult.get("p_resultJson"), Map.class));
                } else {
                    responseGlobalModel.setError(Map.of("error", String.valueOf(procedureResult.get("p_resultJson"))));
                }

            }


        } catch (Exception e) {
            logger.error("Error processing placing: ", e);
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage(e.getMessage());
        }
        return responseGlobalModel;
    }

    private <T> List<ProcedureParamModel> buildParamProcedure(T model) {
        List<ProcedureParamModel> params = new ArrayList<>();
        Field[] fields = model.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true); // Mengakses field private
                Object value = field.get(model);
                if (value != null) {
                    params.add(new ProcedureParamModel(
                            "p_" + toSnakeCase(field.getName()), // Ubah nama field ke format snake_case
                            value,
                            field.getType(),
                            ParameterMode.IN
                    ));
                } else {
                    params.add(new ProcedureParamModel(
                            "p_" + toSnakeCase(field.getName()),
                            "",
                            String.class,
                            ParameterMode.IN
                    ));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error building procedure parameters", e);
        }

        // Tambahkan parameter OUT jika diperlukan
        params.add(new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT));

        return params;
    }

    // Utility to convert camelCase to snake_case
    private String toSnakeCase(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
