package com.alpha.service.controller;

import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.service.SendDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SendDocumentController {

    @Autowired
    private SendDocumentService sendDocumentService;

    @PostMapping(value = "/send-document", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendDocument(@RequestBody String body) {
        ResponseGlobalModel<Object> result = sendDocumentService.processSendDocument(body);
        int code = result.getResultCode();
        if (code == 200) {
            return ResponseEntity.ok(result.getData());
        } else if (code == 422) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                    "resultCode", 422,
                    "message", result.getMessage()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "resultCode", 500,
                    "message", result.getMessage()
            ));
        }
    }
}