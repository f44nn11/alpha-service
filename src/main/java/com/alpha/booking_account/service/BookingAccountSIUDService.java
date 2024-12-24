package com.alpha.booking_account.service;


import com.alpha.booking_account.model.ProcedureParamModel;
import com.alpha.booking_account.model.response.ResponseGlobalModel;
import com.alpha.booking_account.repository.BookingAccountRepository;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 12/15/2024
 */
@Service
public class BookingAccountSIUDService {
    private final Logger logger = LoggerFactory.getLogger(BookingAccountSIUDService.class);

    @Autowired
    private BookingAccountRepository repository;

    public ResponseGlobalModel<Object> bookingAccountProcedure(String procedureName,
                                                               List<ProcedureParamModel> params) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        try {
            logger.info("param===>" + params.toString());
            Map<String, Object> results = repository.callBookingAccountSIUDProcedure(procedureName, params);

            responseGlobalModel.setResultCode((Integer) results.get("p_resultCode"));
            responseGlobalModel.setMessage((String) results.get("p_message"));
            Map<String, Object> jsonData = new Gson().fromJson((String) results.get("p_resultJson"), Map.class);
            if (responseGlobalModel.getResultCode() == 200) {
                responseGlobalModel.setData(jsonData);
            } else if (responseGlobalModel.getResultCode() == 404) {
                responseGlobalModel.setData(jsonData);
            }else  {
                Map<String, String> error = new HashMap<>();
                error.put("sqlError", (String) results.get("p_resultJson"));
                responseGlobalModel.setError(error);
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
