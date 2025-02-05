package com.alpha.service.service;

import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.model.SystemPropertiesModel;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.repository.SyspropRepository;
import jakarta.persistence.ParameterMode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SyspropService {
    @Autowired
    private SyspropRepository repository;
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
}
