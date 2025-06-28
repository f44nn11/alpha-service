package com.alpha.service.controller;

import com.alpha.service.helper.SyspropHelper;
import com.alpha.service.model.*;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.service.ClientMasterPicService;
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
@RequestMapping("/master")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.1.2:3000"}, allowCredentials = "true" ,
        allowedHeaders = {"Content-Type", "Authorization", "X-Requested-With"})
@Validated
public class MasterClienController {
    private static final Logger logger = LoggerFactory.getLogger(SyspropMasterController.class);

    @Autowired
    private ClientMasterPicService service;
    @Autowired
    SyspropHelper syspropHelper;
    @Autowired
    ServiceTool serviceTool;

    @PostMapping("/client")
    public ResponseEntity<Object> saveClient(@RequestBody @Valid ClientMasterModel clientMaster) {
        try {
            logger.info("code===>");
            logger.info("Request Body: {}", clientMaster);
            logger.info("clientCode: {}", clientMaster.getClientCode());
            ResponseGlobalModel<Object> responseGlobalModel;
            responseGlobalModel = syspropHelper.doProcessClient(clientMaster);
            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", 500);
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", "Failed to save client:");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }
    @PostMapping("/doclient")
    public ResponseEntity<Object> doClient(@RequestBody @Valid ClientMasterParameterModel clientMaster) {
        try {
            logger.info("code===>");
            logger.info("Request Body: {}", clientMaster);
            logger.info("clientCode: {}", clientMaster.getClientCode());
            ResponseGlobalModel<Object> responseGlobalModel;
            responseGlobalModel = syspropHelper.doProcessMasterClient(clientMaster);
            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", 500);
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", "Failed to save client:");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }
    @PostMapping("/clientpic")
    public ResponseEntity<Object> saveClientPic(@RequestBody @Valid ClientMasterPicModel clientMasterPic) {
        try {
            logger.info("code===>");
            logger.info("Request Body: {}", clientMasterPic);
            logger.info("clientCode: {}", clientMasterPic.getClientCode());
            logger.info("picType: {}", clientMasterPic.getPicType());
            ResponseGlobalModel<Object> responseGlobalModel;
            responseGlobalModel = syspropHelper.doProcessClientPic(clientMasterPic);
            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", 500);
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", "Failed to save client:");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }
    @PostMapping("/doclientpic")
    public ResponseEntity<Object> doClientPic(@RequestBody @Valid ClientMasterPicParameterModel clientMasterPic) {
        try {
            logger.info("code===>");
            logger.info("Request Body: {}", clientMasterPic);
            logger.info("clientCode: {}", clientMasterPic.getClientCode());
            ResponseGlobalModel<Object> responseGlobalModel;
            responseGlobalModel = syspropHelper.doProcessMasterClientPic(clientMasterPic);
            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", 500);
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", "Failed to save client:");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }
    public static void main(String[] args) {
        SystemPropertiesModel model = new SystemPropertiesModel();
        model.setCode("BR");
        System.out.println(model.getCode());
    }
}
