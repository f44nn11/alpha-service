package com.alpha.service.controller;


import com.alpha.service.model.EmailCheckModel;
import com.alpha.service.model.placing.PlacingRequestModel;
import com.alpha.service.model.procedure.PlacingConfirmRequestParam;
import com.alpha.service.model.procedure.UspComparationParam;
import com.alpha.service.model.procedure.UspPlacingParam;
import com.alpha.service.model.procedure.UspProposalParam;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.service.PlacingAccountSIUDService;
import com.alpha.service.util.DataUtil;
import com.alpha.service.util.ServiceTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 1/19/2025
 */
@RestController
@RequestMapping("/placing")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.1.2:3000"}, allowCredentials = "true",
        allowedHeaders = {"Content-Type", "Authorization", "X-Requested-With"})
@Validated
public class PlacingController {
    private final Logger logger = LoggerFactory.getLogger(PlacingController.class);
    @Autowired
    private PlacingAccountSIUDService placingAccountService;
    @Autowired
    private ServiceTool serviceTool;
    @Autowired
    private DataUtil dataUtil;

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

    @PostMapping("/doplacing/confirm")
    public ResponseEntity<ResponseGlobalModel<Object>> doPlacingConfirm(@RequestBody UspPlacingParam placingRequest) {
        logger.info("Received placing request: {}", placingRequest);

        try {
            ResponseGlobalModel<Object> response = placingAccountService.doProcessPlacingConfirm(placingRequest);

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

    @PostMapping("/doproposal")
    public ResponseEntity<ResponseGlobalModel<Object>> doProposal(@RequestBody UspProposalParam proposalRequest) {
        logger.info("Received placing request: {}", proposalRequest);

        try {
            ResponseGlobalModel<Object> response = placingAccountService.doProcessProposal(proposalRequest);

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

    @PostMapping("/doproposal/revision")
    public ResponseEntity<ResponseGlobalModel<Object>> doProposalRequest(@RequestBody UspProposalParam proposalRequest) {
        logger.info("Received placing request: {}", proposalRequest);

        try {
            ResponseGlobalModel<Object> response = placingAccountService.doProcessProposalRequest(proposalRequest);

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

    @PostMapping("/proposal/revision")
    public ResponseEntity<ResponseGlobalModel<Object>> doProposalRevision(
            @RequestParam(value = "data") String data,
            @RequestParam(value = "globalFiles[]", required = false) List<MultipartFile> globalFiles,
            @RequestParam Map<String, MultipartFile> files
    ) {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        try {
            logger.info("Received proposal revision: {}", data);

            PlacingRequestModel proposalRevision = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .setPrettyPrinting()
                    .create()
                    .fromJson(data, PlacingRequestModel.class);

            String baseFolder = dataUtil.getPathUpload();
            String bookCd = proposalRevision.getBookCd();
            String placingCd = proposalRevision.getPlacingCd();

            for (PlacingRequestModel.Insurance insurance : proposalRevision.getInsurances()) {
                List<PlacingRequestModel.DocType> docTypes = insurance.getDocTypes();
                // Set file path & update doc url
                String insCd = String.valueOf(insurance.getInsCd());
                String actionType = proposalRevision.getActionType();
                String revDoc = String.valueOf(insurance.getDocTypes().get(0).getRevDoc());

                String targetPath = baseFolder + "/" + bookCd + "/rev" + revDoc + "/" + placingCd;


                for (PlacingRequestModel.DocType doc : docTypes) {
                    String fileKey;
                    int globalIdx = 0;
                    if (doc.isGlobal()) {
                        if (globalFiles != null && globalFiles.size() > globalIdx) {
                            MultipartFile globalFile = globalFiles.get(globalIdx++);
                            String docFolder = "other";
                            String urlPath = serviceTool.saveFile(globalFile, baseFolder, bookCd, revDoc, placingCd, String.valueOf(revDoc), docFolder, "proposalRevision");
                            doc.setUrlPath(urlPath);
                        }
                    } else if (doc.isPerInsurance()) {
                        fileKey = "insuranceFiles[" + insurance.getInsCd() + "][]";

                        logger.info("🔍Checking perInsurance fileKey: {}", fileKey);
                        if (files != null && !files.isEmpty()) {
                            MultipartFile file = files.get(fileKey);
                            if (file != null && !file.isEmpty()) {
                                String docFolder = "other";
                                String urlPath = serviceTool.saveFile(file, baseFolder, bookCd, revDoc, placingCd, String.valueOf(revDoc), docFolder, "proposalRevision");

                                // Update urlPath
                                doc.setUrlPath(urlPath);
                            }

                        }
                    } else {
                        fileKey = insCd + "-" + doc.getCode();
                    }
                }


                String docJson = "";
                if (actionType.equalsIgnoreCase("1")) {
                    docJson = gson.toJson(insurance.getDocTypes());
                } else {
                    if (files != null && !files.isEmpty()) {
                        docJson = gson.toJson(insurance.getDocTypes());
                    }
                }


                logger.info("dataJson====> {}", docJson);

            }

            logger.info("proposalRevision====>" + gson.toJson(proposalRevision));
            ResponseGlobalModel<Object> response = placingAccountService.doProcessInsProposalRevision(proposalRevision);

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

    @PostMapping("/docomparation")
    public ResponseEntity<ResponseGlobalModel<Object>> doComparation(@RequestBody UspComparationParam paramRequest) {
        logger.info("Received placing request: {}", paramRequest);

        try {
            ResponseGlobalModel<Object> response = placingAccountService.doProcessComparation(paramRequest);

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

    @PostMapping("/booking/resend")
    public ResponseEntity<ResponseGlobalModel<Object>> placeBookingResend(@RequestBody PlacingRequestModel placingRequest) {
        logger.info("Received placing request: {}", placingRequest);

        try {

            ResponseGlobalModel<Object> response = placingAccountService.doProcessPlacingResend(placingRequest, "2");

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

    @PostMapping("/check/email")
    public ResponseEntity<ResponseGlobalModel<Object>> doCheckEmail(@RequestBody EmailCheckModel emailCheckModel) {
        logger.info("Received emailCheckModel request: {}", emailCheckModel);

        try {

            ResponseGlobalModel<Object> response = placingAccountService.doCheckEmail(emailCheckModel);

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

    @PostMapping("/confirm")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> doProcessPlacingConfirm(@RequestBody String data) {
        logger.info("Received placing confirm request: {}", data);
        ResponseGlobalModel<Object> response = new ResponseGlobalModel<>();
        try {

            if (data == null || data.isBlank()) {
                response.setResultCode(400);
                response.setMessage("Data JSON is required.");
                return ResponseEntity.badRequest().body(response);
            }


            response = placingAccountService.doProcessPlacingConfirm(data);

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

    @PostMapping("/confirm/v_old")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> doProcessPlacingConfirm_V_old(@RequestParam Map<String, MultipartFile> files,
                                                                @RequestParam("data") String data) {
        logger.info("Received placing confirm request: {}", data);
        ResponseGlobalModel<Object> response = new ResponseGlobalModel<>();
        try {

            if (data == null || data.isBlank()) {
                response.setResultCode(400);
                response.setMessage("Data JSON is required.");
                return ResponseEntity.badRequest().body(response);
            }
            if (files == null || files.isEmpty()) {
                response.setResultCode(400);
                response.setMessage("At least one file must be uploaded.");
                return ResponseEntity.badRequest().body(response);
            }

//            response = placingAccountService.doProcessPlacingConfirm(files, data);

            //Override response for testing purposes
            response.setMessage("Success");
            response.setResultCode(200);
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


    @PostMapping("/proposal")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> doProcessInsProposal(@RequestParam Map<String, MultipartFile> files,
                                                       @RequestParam("data") String data) {
        logger.info("Receivedproposal request: {}", data);
        ResponseGlobalModel<Object> response = new ResponseGlobalModel<>();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        try {

            PlacingConfirmRequestParam placingConfirmRequestParam = gson.fromJson(data, PlacingConfirmRequestParam.class);

            if (data == null || data.isBlank()) {
                response.setResultCode(400);
                response.setMessage("Data JSON is required.");
                return ResponseEntity.badRequest().body(response);
            }

            if ("1".equals(placingConfirmRequestParam.getActionType()) && (files == null || files.isEmpty())) {
                response.setResultCode(400);
                response.setMessage("At least one file must be uploaded.");
                return ResponseEntity.badRequest().body(response);
            }


            response = placingAccountService.doProcessInsProposal(files, data);

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

    @PostMapping("/comparation")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> doProcessInsComparation(@RequestParam Map<String, MultipartFile> files,
                                                          @RequestParam("data") String data) {
        logger.info("Receivedcomparation request: {}", data);
        ResponseGlobalModel<Object> response = new ResponseGlobalModel<>();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        try {

            if (data == null || data.isBlank()) {
                response.setResultCode(400);
                response.setMessage("Data JSON is required.");
                return ResponseEntity.badRequest().body(response);
            }

            UspComparationParam comparationRequestParam = gson.fromJson(data, UspComparationParam.class);

            if ("1".equals(comparationRequestParam.getActionType()) && (files == null || files.isEmpty())) {
                response.setResultCode(400);
                response.setMessage("At least one file must be uploaded.");
                return ResponseEntity.badRequest().body(response);
            }

            response = placingAccountService.doProcessInsComparation(files, data);

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

    @PostMapping("/comparation/sendmail")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> doProcessSendMailComparation(@RequestBody String data) {
        logger.info("Receivedcomparation SendMail request: {}", data);
        ResponseGlobalModel<Object> response = new ResponseGlobalModel<>();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        try {

            if (data == null || data.isBlank()) {
                response.setResultCode(400);
                response.setMessage("Data JSON is required.");
                return ResponseEntity.badRequest().body(response);
            }

            UspComparationParam comparationRequestParam = gson.fromJson(data, UspComparationParam.class);
            logger.info("comparationRequestParam==>" + new Gson().toJson(comparationRequestParam));


            response = placingAccountService.doProcessSendComparation(data);

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
