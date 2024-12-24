package com.alpha.booking_account.service;

import com.alpha.booking_account.model.ProcedureParamModel;
import com.alpha.booking_account.model.response.ResponseGlobalModel;
import com.alpha.booking_account.repository.SyspropRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SyspropGeodataService {
    private final Logger logger = LoggerFactory.getLogger(SyspropGeodataService.class);

    @Autowired
    private SyspropRepository repository;

    public ResponseGlobalModel<Object> callSyspropGeodataProcedure(String procedureName,
                                                                   List<ProcedureParamModel> params,
                                                                   List<String> resultColumns) {
        ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
        try {
            List<Object[]> results = repository.callStoredProcedure(procedureName, params);
//            logger.info("results===>" + results.toString());
            List<Map<String, Object>> mappedResults = results.stream()
                    .map(row -> {
                        Map<String, Object> map = new LinkedHashMap<>();
                        for (int i = 0; i < resultColumns.size(); i++) {
                            map.put(resultColumns.get(i), row[i]);
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage("Data Geodata");
            responseGlobalModel.setData(mappedResults);
        } catch (Exception e) {
            logger.error("Error calling stored procedure: " + procedureName, e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Error executing procedure");
            responseGlobalModel.setData(Collections.singletonMap("error", e.getMessage()));
        }

        return responseGlobalModel;
    }
}
