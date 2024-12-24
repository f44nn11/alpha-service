package com.alpha.booking_account.controller;


import com.alpha.booking_account.exception.CustomException;
import com.alpha.booking_account.helper.BookingAccountHelper;
import com.alpha.booking_account.model.BookingAccountModel;
import com.alpha.booking_account.model.procedure.UspBookingAccountGetParam;
import com.alpha.booking_account.model.response.ResponseGlobalModel;
import com.alpha.booking_account.util.DataUtil;
import com.alpha.booking_account.util.ServiceTool;
import com.google.gson.Gson;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/*
 * Created by: fkusu
 * Date: 12/15/2024
 */
@RestController
@RequestMapping("/booking")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true",
        allowedHeaders = {"Content-Type", "Authorization", "X-Requested-With"})
@Validated
public class BookingAccountController {
    private static final Logger logger = LoggerFactory.getLogger(BookingAccountController.class);
    @Autowired
    private BookingAccountHelper helper;
    @Autowired
    ServiceTool serviceTool;
    @Autowired
    DataUtil dataUtil;

    @PostMapping("/doaccount")
    public ResponseEntity<Object> doDataBookingAccount(@RequestBody @Valid UspBookingAccountGetParam bpm) {
        try {
            logger.info("code===>");
            logger.info("Request Body: {}", bpm);
            logger.info("clientCode: {}", bpm.getBookCd());
            ResponseGlobalModel<Object> responseGlobalModel;
            responseGlobalModel = helper.doProcessBookingAccount(bpm);
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

    @PostMapping("/account")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> doProcessBooking(@RequestParam Map<String, MultipartFile> files,
                                                   @RequestParam("data") String data) {

        try {
            logger.info("code===>");
            logger.info("Request Body: {}", data);
            logger.info("bookCd: {}", data);
            logger.info("pathUrl====>" + dataUtil.getPathUpload());
            logger.info("profile====>" + serviceTool.getActiveProfile());

            BookingAccountModel bookingAccountModel = new Gson().fromJson(data, BookingAccountModel.class);
            List<BookingAccountModel.DocType> docTypes = new ArrayList<>();
            ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();

            // 1. Proses Header
            ResponseGlobalModel<Object> headerResponse = helper.doProcessBookingIUD(bookingAccountModel, "", "1");
            logger.info("headerResponse==>" + new Gson().toJson(headerResponse));
            if (headerResponse.getResultCode() != 200) {
                throw new CustomException(headerResponse.getMessage(), headerResponse.getResultCode(), headerResponse.getMessage());
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> headerData = (Map<String, Object>) headerResponse.getData();
            String bookCd = (String) headerData.get("bookCd");
            logger.info("bookCd from Header Response: {}", bookCd);
            if (headerResponse.getResultCode() != 200) {
                throw new RuntimeException("Header processing failed: " + headerResponse.getMessage());
            }
            if (bookCd == null || bookCd.isEmpty()) {
                throw new RuntimeException("Header processing failed: " + headerResponse.getMessage());
            }

            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage(headerResponse.getMessage());
            responseGlobalModel.setData(headerResponse.getData());

            if (!bookingAccountModel.getActionType().equalsIgnoreCase("3")){
                // 2. Proses Detail
                files.forEach((code, file) -> {
                    BookingAccountModel.DocType docType = new BookingAccountModel.DocType();
                    String fileName = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("[^a-zA-Z0-9\\.\\-_]+", "_");

                    String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                    String extension = fileName.substring(fileName.lastIndexOf('.'));

                    String docTypeOri = file.getName();
                    String docTypeDesc = switch (code) {
                        case "1" -> "memberlist";
                        case "2" -> "benefit";
                        case "3" -> "claimdetail";
                        case "4" -> "claimratio";
                        case "5" -> "termcondition";
                        case "6" -> "loa";
                        default -> docTypeOri;
                    };
                    logger.info("docTypeDesc==>{}", docTypeDesc);
                    String folder = dataUtil.getPathUpload() + "/" + bookCd + "/" + docTypeDesc;
                    File folders = new File(folder);
                    if (!folders.exists()) {
                        boolean created = folders.mkdirs();
                        logger.info("Directory created: {}", created);
                        if (!created) {
                            logger.error("Failed to create directory: {}", folder);
                        }
                    }

                    int counter = calculateRevisionNumber(folder, baseName, extension);
                    String newName = baseName + "_rev" + counter + extension;

                    try {
                        Path pathDoc = Paths.get(folder, newName);
                        InputStream inDoc = file.getInputStream();
                        Files.copy(inDoc, pathDoc);
                        logger.info("Revised file saved as {}", pathDoc);


                        docType.setCode(code);
                        docType.setRevDoc(String.valueOf(counter));
                        docType.setDescp(docTypeDesc);
                        docType.setUrlPath(pathDoc.toString().replace(File.separatorChar, '/'));

                        docTypes.add(docType);
                    } catch (IOException e) {
                        logger.error("Failed to save file", e);
                    }
                    bookingAccountModel.setDocTypes(docTypes);
                });

                responseGlobalModel = helper.doProcessBookingIUD(bookingAccountModel,bookCd,"2");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
        } catch (CustomException e) {
            logger.error("Error handling request", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", e.getResultCode());
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("errorDetails", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(e.getResultCode()).body(errorBody);
        }
    }

    private int calculateRevisionNumber(String folderPath, String baseName, String extension) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.startsWith(baseName) && name.endsWith(extension));

        if (files == null || files.length == 0) {
            return 1; // Revisi pertama
        }

        // Cari revisi tertinggi
        int maxRevision = 0;
        for (File file : files) {
            String name = file.getName();
            String revPart = name.substring(baseName.length(), name.lastIndexOf(extension));
            if (revPart.startsWith("_rev")) {
                try {
                    int revNumber = Integer.parseInt(revPart.substring(4));
                    maxRevision = Math.max(maxRevision, revNumber);
                } catch (NumberFormatException ignored) {
                    // Abaikan jika tidak valid
                }
            }
        }

        return maxRevision + 1; // Revisi berikutnya
    }
}
