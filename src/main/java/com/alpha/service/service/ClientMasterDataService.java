package com.alpha.service.service;

import com.alpha.service.mapper.ClientMasterMapper;
import com.alpha.service.mapper.ClientMasterPicMapper;
import com.alpha.service.model.ClientMasterModel;
import com.alpha.service.model.ClientMasterPicModel;
import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.repository.SyspropRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Created by: fkusu
 * Date: 12/4/2024
 */
@Service
public class ClientMasterDataService {
    private final Logger logger = LoggerFactory.getLogger(ClientMasterDataService.class);
    @Autowired
    private SyspropRepository repository;

    public ResponseGlobalModel<Object> callMasterClientProcedure(String procedureName,
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
                            String columnName = resultColumns.get(i);
                            Object value = row[i];

                            if (value instanceof Character) {
                                map.put(columnName, value.toString());
                            } else if (value instanceof Boolean) {
                                map.put(columnName, value.toString());
                            } else {
                                map.put(columnName, value);
                            }
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            List<ClientMasterModel> clientMasterList = ClientMasterMapper.mapToClientMasterList(mappedResults);

            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage("Data Master Client");
            responseGlobalModel.setData(clientMasterList);
        } catch (Exception e) {
            logger.error("Error calling stored procedure: " + procedureName, e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Error executing procedure");
            responseGlobalModel.setData(Collections.singletonMap("error", e.getMessage()));
        }

        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> callMasterClientPicProcedure(String procedureName,
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
                            String columnName = resultColumns.get(i);
                            Object value = row[i];

                            if (value instanceof Character) {
                                map.put(columnName, value.toString());
                            } else if (value instanceof Boolean) {
                                map.put(columnName, value.toString());
                            } else {
                                map.put(columnName, value);
                            }
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            List<ClientMasterPicModel> clientMasterList = ClientMasterPicMapper.mapToClientMasterList(mappedResults);

            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage("Data Master Client");
            responseGlobalModel.setData(clientMasterList);
        } catch (Exception e) {
            logger.error("Error calling stored procedure: " + procedureName, e);
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Error executing procedure");
            responseGlobalModel.setData(Collections.singletonMap("error", e.getMessage()));
        }

        return responseGlobalModel;
    }
}
