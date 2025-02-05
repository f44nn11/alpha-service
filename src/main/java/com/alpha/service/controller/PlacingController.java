package com.alpha.service.controller;


import com.alpha.service.model.placing.PlacingRequestModel;
import com.alpha.service.model.procedure.UspPlacingParam;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.service.PlacingAccountSIUDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/*
 * Created by: fkusu
 * Date: 1/19/2025
 */
@RestController
@RequestMapping("/placing")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true",
        allowedHeaders = {"Content-Type", "Authorization", "X-Requested-With"})
@Validated
public class PlacingController {
    private final Logger logger = LoggerFactory.getLogger(PlacingController.class);
    @Autowired
    private PlacingAccountSIUDService placingAccountService;

    @PostMapping("/doplacing")
    public ResponseEntity<ResponseGlobalModel<Object>> doPlacingBooking(@RequestBody UspPlacingParam placingRequest) {
        logger.info("Received placing request: {}", placingRequest);

        try {
            ResponseGlobalModel<Object> response = placingAccountService.doProcessPlacing(placingRequest);

            if (response.getResultCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            logger.error("Error processing placing: ", e);
            ResponseGlobalModel<Object> errorResponse = new ResponseGlobalModel<>();
            errorResponse.setResultCode(500);
            errorResponse.setMessage("Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/booking")
    public ResponseEntity<ResponseGlobalModel<Object>> placeBooking(@RequestBody PlacingRequestModel placingRequest) {
        logger.info("Received placing request: {}", placingRequest);

        try {
            ResponseGlobalModel<Object> response = placingAccountService.doProcessPlacingIUD(placingRequest, "1");

            if (response.getResultCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            logger.error("Error processing placing: ", e);
            ResponseGlobalModel<Object> errorResponse = new ResponseGlobalModel<>();
            errorResponse.setResultCode(500);
            errorResponse.setMessage("Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}
