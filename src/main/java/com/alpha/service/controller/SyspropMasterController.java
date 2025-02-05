package com.alpha.service.controller;

import com.alpha.service.helper.GeodataHelper;
import com.alpha.service.model.SystemPropertiesModel;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.service.SyspropService;
import com.alpha.service.util.ServiceTool;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sysprop")
@CrossOrigin(origins = "*")
@Validated
public class SyspropMasterController {
    private static final Logger logger = LoggerFactory.getLogger(SyspropMasterController.class);
    @Autowired
    private SyspropService service;
    @Autowired
    GeodataHelper geodataHelper;

    @Autowired
    ServiceTool serviceTool;

    @PostMapping("/master")
    public ResponseEntity<Object> callProcedureMaster(@RequestBody @Valid SystemPropertiesModel request) {
        ResponseGlobalModel <Object> responseGlobalModel;
        try{
            logger.info("callProcedureMaster {}", request);
            validateRequest(request);
            responseGlobalModel = service.callSyspropProcedure(request);
        }catch (Exception e){
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", 500);
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", "Internal Server Error");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
    }
    @PostMapping("/geodata")
    public ResponseEntity<Object> callGeodataMaster(@RequestBody @Valid SystemPropertiesModel request) {
        ResponseGlobalModel<Object> responseGlobalModel;

        try{
            responseGlobalModel = geodataHelper.getGeodata(request);
        }catch (Exception e){
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", 500);
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", "Internal Server Error");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
    }


    public static void validateRequest(SystemPropertiesModel request) {
        if ("1".equals(request.getActionType())) {
            if (request.getType() == null || request.getType().isEmpty()) {
                throw new IllegalArgumentException("Type is mandatory when actionType is 1");
            }
        }
    }
}
