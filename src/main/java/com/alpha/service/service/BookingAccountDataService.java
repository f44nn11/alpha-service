package com.alpha.service.service;


import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.repository.BookingAccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/*
 * Created by: fkusu
 * Date: 12/15/2024
 */
@Service
public class BookingAccountDataService {
    private final Logger logger = LoggerFactory.getLogger(BookingAccountDataService.class);
    @Autowired
    private BookingAccountRepository repository;



    public ResponseGlobalModel<Object> callBookingAccountProcedure(String procedureName,
                                                                    List<ProcedureParamModel> params,
                                                                    List<String> resultColumns) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        try {
            Map<String, Object> results = repository.callBookingAccountSIUDProcedure(procedureName, params);

            responseGlobalModel.setResultCode((Integer) results.get("p_resultCode"));
            responseGlobalModel.setMessage((String) results.get("p_message"));
//            Map<String, Object> jsonData = new Gson().fromJson((String) results.get("p_resultJson"), Map.class);
            String resultJson = (String) results.get("p_resultJson");

            if (resultJson == null || resultJson.isEmpty()) {
                responseGlobalModel.setError(Collections.singletonMap("jsonError", "No data returned from stored procedure"));
                return responseGlobalModel;
            }
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                List<Map<String, Object>> jsonArray = objectMapper.readValue(resultJson, List.class);
                responseGlobalModel.setData(jsonArray);
            } catch (Exception e) {
                try {
                    Map<String, Object> jsonObject = objectMapper.readValue(resultJson, Map.class);
                    responseGlobalModel.setData(Collections.singletonList(jsonObject));
                } catch (Exception ex) {
                    Map<String, String> error = new HashMap<>();
                    error.put("jsonError", "Invalid JSON format: " + ex.getMessage());
                    responseGlobalModel.setError(error);
                }
            }

            if (responseGlobalModel.getResultCode() != 200) {
                try {
                    Map<String, Object> errorDetails = objectMapper.readValue(resultJson, Map.class);
                    Map<String, String> stringErrorDetails = errorDetails.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue() != null ? entry.getValue().toString() : null
                            ));
                    responseGlobalModel.setError(stringErrorDetails);
                } catch (Exception e) {
                    Map<String, String> error = new HashMap<>();
                    error.put("sqlError", resultJson);
                    responseGlobalModel.setError(error);
                }
            }
        } catch (Exception e) {
            logger.error("Error calling stored procedure: " + procedureName, e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Error executing procedure");
            responseGlobalModel.setData(Collections.singletonMap("error", e.getMessage()));
        }

        return responseGlobalModel;
    }
    public ResponseGlobalModel<Object> callBookingReviewProcedure(String procedureName,
                                                                   List<ProcedureParamModel> params) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        try {
            Map<String, Object> results = repository.callBookingAccountSIUDProcedure(procedureName, params);

            responseGlobalModel.setResultCode((Integer) results.get("p_resultCode"));
            responseGlobalModel.setMessage((String) results.get("p_message"));
//            Map<String, Object> jsonData = new Gson().fromJson((String) results.get("p_resultJson"), Map.class);
            String resultJson = (String) results.get("p_resultJson");

            if (resultJson == null || resultJson.isEmpty()) {
                responseGlobalModel.setError(Collections.singletonMap("jsonError", "No data returned from stored procedure"));
                return responseGlobalModel;
            }
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                List<Map<String, Object>> jsonArray = objectMapper.readValue(resultJson, List.class);
                responseGlobalModel.setData(jsonArray);
            } catch (Exception e) {
                try {
                    Map<String, Object> jsonObject = objectMapper.readValue(resultJson, Map.class);
                    responseGlobalModel.setData(Collections.singletonList(jsonObject));
                } catch (Exception ex) {
                    Map<String, String> error = new HashMap<>();
                    error.put("jsonError", "Invalid JSON format: " + ex.getMessage());
                    responseGlobalModel.setError(error);
                }
            }

            if (responseGlobalModel.getResultCode() != 200) {
                try {
                    Map<String, Object> errorDetails = objectMapper.readValue(resultJson, Map.class);
                    Map<String, String> stringErrorDetails = errorDetails.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue() != null ? entry.getValue().toString() : null
                            ));
                    responseGlobalModel.setError(stringErrorDetails);
                } catch (Exception e) {
                    Map<String, String> error = new HashMap<>();
                    error.put("sqlError", resultJson);
                    responseGlobalModel.setError(error);
                }
            }
        } catch (Exception e) {
            logger.error("Error calling stored procedure: " + procedureName, e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Error executing procedure");
            responseGlobalModel.setData(Collections.singletonMap("error", e.getMessage()));
        }

        return responseGlobalModel;
    }
}
