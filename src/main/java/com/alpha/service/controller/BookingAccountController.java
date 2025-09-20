package com.alpha.service.controller;


import com.alpha.service.exception.CustomException;
import com.alpha.service.helper.BookingAccountHelper;
import com.alpha.service.model.BookingAccountModel;
import com.alpha.service.model.BookingReviewModel;
import com.alpha.service.model.BookingStatusModel;
import com.alpha.service.model.placing.PlacingRequestModel;
import com.alpha.service.model.procedure.UspBookingAccountGetParam;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.model.sendemail.EmailRequestModel;
import com.alpha.service.repository.BookingAccountRepository;
import com.alpha.service.repository.PlacingAccountRepository;
import com.alpha.service.repository.LogEmailRepository;
import com.alpha.service.service.BookingService;
import com.alpha.service.service.sendemail.EmailService;
import com.alpha.service.service.sendemail.EmailDispatcher;
import com.alpha.service.util.Constants;
import com.alpha.service.util.DataUtil;
import com.alpha.service.util.ServiceTool;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
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
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Created by: fkusu
 * Date: 12/15/2024
 */
@RestController
@RequestMapping("/booking")
@Validated
public class BookingAccountController {
    private static final Logger logger = LoggerFactory.getLogger(BookingAccountController.class);
    @Autowired
    private BookingAccountHelper helper;
    @Autowired
    private BookingAccountRepository repository;
    @Autowired
    private ServiceTool serviceTool;
    @Autowired
    private DataUtil dataUtil;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EmailDispatcher emailDispatcher;
    @Autowired
    private com.alpha.service.service.FileStorageService fileStorageService;
    @Autowired
    private LogEmailRepository logEmailRepository;

    @PostMapping("/doaccount")
    public ResponseEntity<Object> doDataBookingAccount(@RequestBody @Valid UspBookingAccountGetParam bpm) {
        try {
            logger.info("code===>");
            logger.info("Request Body doaccount: {}", bpm);
            logger.info("clientCode: {}", bpm.getBookCd());
            ResponseGlobalModel<Object> responseGlobalModel;
            responseGlobalModel = helper.doProcessBookingAccount(bpm);
            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", Optional.of(500));
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", "Failed to save client:");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }

    @PostMapping("/doreview")
    public ResponseEntity<Object> doDataBookingReview(@RequestBody @Valid UspBookingAccountGetParam bpm) {
        try {
            logger.info("code===>");
            logger.info("Request Body doreview: {}", bpm);
            logger.info("getBookCd: {}", bpm.getBookCd());
            ResponseGlobalModel<Object> responseGlobalModel;
            responseGlobalModel = helper.doProcessBookingReview(bpm);
            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
        } catch (Exception e) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", Optional.of(500));
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", "Failed to save client:");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }

    @PostMapping("/account")
    public ResponseEntity<Object> doProcessBooking(@RequestParam Map<String, MultipartFile> files,
                                                   @RequestParam("data") String data,
                                                   HttpServletRequest request) {

        Map<String, Object> templateData = new HashMap<>();

        try {
            logger.info("code===>");
            logger.info("Request Body account: {}", data);
            logger.info("bookCd: {}", data);
            logger.info("pathUrl====>" + dataUtil.getPathUpload());
            logger.info("profile====>" + serviceTool.getActiveProfile());
            String idem = request.getHeader("X-Idempotency-Key");
            MDC.put("idemKey", Optional.ofNullable(idem).orElse("-"));

            BookingAccountModel bookingAccountModel = new Gson().fromJson(data, BookingAccountModel.class);
            List<BookingAccountModel.DocType> docTypes = new ArrayList<>();
            // Materialize attachments in-memory (byte[]) before leaving request thread
            List<com.alpha.service.model.sendemail.EmailAttachment> attachments = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                for (MultipartFile f : files.values()) {
                    try {
                        attachments.add(new com.alpha.service.model.sendemail.EmailAttachment(
                                f.getOriginalFilename(),
                                f.getContentType(),
                                f.getBytes()
                        ));
                    } catch (IOException ex) {
                        logger.warn("Failed to read attachment {}: {}", f.getOriginalFilename(), ex.getMessage());
                    }
                }
            }
            ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
            logger.info("bookingAccountModel==>0>{}", new Gson().toJson(bookingAccountModel));

            String originalActionType = bookingAccountModel.getActionType();

            if (originalActionType.equalsIgnoreCase("1")){
                if (repository.isClientBookingOpen(bookingAccountModel.getClientCode().getCode(), Integer.parseInt(bookingAccountModel.getRevDoc()))) {
                    responseGlobalModel.setResultCode(400);
                    responseGlobalModel.setMessage("Client Name " + bookingAccountModel.getClientCode().getDescp() + " already has an OPEN booking.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseGlobalModel);
                }
            }

            // 1. Proses Header
            ResponseGlobalModel<Object> headerResponse = helper.doProcessBookingIUD(bookingAccountModel, bookingAccountModel.getBookCd(), "1");
            logger.info("headerResponse==>" + new Gson().toJson(headerResponse));
            if (headerResponse.getResultCode() != 200) {
                throw new CustomException(headerResponse.getMessage(), headerResponse.getResultCode(), headerResponse.getMessage());
            }
            logger.info("bookingAccountModel==>1>{}", new Gson().toJson(bookingAccountModel));
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
            System.out.println("<===files==>" + files);

            int rev = Integer.parseInt(bookingAccountModel.getRevDoc());

            logger.info("rev==>"  + rev);
            logger.info("bookCd==>" + bookCd);
            logger.info("isBookingRevExist====>" + repository.isBookingRevExist(bookCd, rev));

            // Cek apakah revisi yang diedit ada kalau new booking tidak perlu cek revisi
            if (!bookingAccountModel.getActionType().equals(originalActionType)) {
                if (!repository.isBookingRevExist(bookCd, rev)) {
                    throw new CustomException("The revision being edited was not found!", 404, "Invalid REV");
                }
            }

            bookingAccountModel.setActionType(originalActionType);
            bookingAccountModel.setRevDoc(String.valueOf(rev));
            if (!bookingAccountModel.getActionType().equalsIgnoreCase("3")) {
                logger.info("bookingAccountModel==>2>{}", new Gson().toJson(bookingAccountModel));
                if (!files.isEmpty()) {
                    logger.info("Start forEach: code(s)={}, files={}", files.keySet(), files.values());
                    files.forEach((code, value) -> {
                        logger.info("Start forEach: code={}, fileName={}, size={}", code, value.getOriginalFilename(), value.getSize());
                        if (value == null) return;
                        processFile(code, value, bookCd, rev, dataUtil, bookingAccountModel, logger);
                    });
                }
                logger.info("✅ DocTypes after update files: " + new Gson().toJson(bookingAccountModel.getDocTypes()));
                if (bookingAccountModel.getDocTypes() != null) {

                    List<BookingAccountModel.DocType> docTypesToProcess;
                    if ("2".equalsIgnoreCase(bookingAccountModel.getActionType())) {
                        docTypesToProcess = bookingAccountModel.getDocTypes().stream()
                                .filter(d -> d.getActionType() != null && !d.getActionType().isBlank())
                                .collect(Collectors.toList());
                    } else {
                        docTypesToProcess = bookingAccountModel.getDocTypes();
                    }

                    //Proses Detail
                    if (!docTypesToProcess.isEmpty()) {
                        BookingAccountModel bookingDetailModel = new BookingAccountModel();

                        BeanUtils.copyProperties(bookingAccountModel, bookingDetailModel);
                        bookingDetailModel.setDocTypes(docTypesToProcess);
                        bookingDetailModel.setRevDoc(String.valueOf(rev));
                        responseGlobalModel = helper.doProcessBookingIUD(bookingDetailModel, bookCd, "2");
                    }
                }
            }

            if (responseGlobalModel.getResultCode() == 200) {
                if (bookingAccountModel.getActionType().equalsIgnoreCase("1")) {
                    String revNorm = (bookingAccountModel.getRevDoc() == null || bookingAccountModel.getRevDoc().isBlank()) ? "" : bookingAccountModel.getRevDoc();
                    if (logEmailRepository.existsSuccess("BKNEW", "bookCd", bookCd, revNorm)) {
                        logger.warn("Skip email BKNEW; already SUCCESS for bookCd={}, rev={}", bookCd, revNorm);
                    } else {
                        EmailRequestModel emailRequestModel = new EmailRequestModel();
                        String mailType = "";
                        if (bookingAccountModel.getActionType().equalsIgnoreCase("1")) {
                            mailType = "BKNEW";
                        }
                        emailRequestModel.setMailType(mailType);
                        emailRequestModel.setActionType("1");
                        emailRequestModel.setBookCd(bookCd);
                        emailRequestModel.setClientName(bookingAccountModel.getClientName());
                        emailRequestModel.setCode(bookCd);
                        emailRequestModel.setRevDoc(String.valueOf(rev));
                        emailRequestModel.setBookRev(String.valueOf(rev));
                        emailRequestModel.setCreatedBy(bookingAccountModel.getCreatedBy() == null ? "System" : bookingAccountModel.getCreatedBy());
                        bookingAccountModel.setBookCd(bookCd);
                        templateData.put("bookCd", bookCd);
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, Object> paramTemplateMap = objectMapper.convertValue(bookingAccountModel, new TypeReference<Map<String, Object>>() {});
                        emailRequestModel.setParamTemplate(paramTemplateMap);
                        System.out.println("<==emailRequestModel==" + new Gson().toJson(emailRequestModel));
                        emailDispatcher.sendEmailAsync(
                                new Gson().toJson(emailRequestModel),
                                attachments,
                                serviceTool.getProperty("email.service.url") + "/email/send"
                        );
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);

        } catch (CustomException e) {
            logger.error("Error handling request", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", Optional.of(e.getResultCode()));
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("errorDetails", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(e.getResultCode()).body(errorBody);
        }
    }

    @PostMapping("/account/status")
    public ResponseEntity<Object> doProcessBookingStatus(@RequestBody String data) {
        try {
            logger.info("Request Body account/status: {}", data);
            BookingStatusModel bookingStatusModel = new Gson().fromJson(data, BookingStatusModel.class);

            if (bookingStatusModel.getBookCd() == null || bookingStatusModel.getBookCd().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "resultCode", 400,
                        "message", "bookCd is required"
                ));
            }
            if (bookingStatusModel.getStatus() == null || bookingStatusModel.getStatus().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "resultCode", 400,
                        "message", "status is required"
                ));
            }
            // Additional validation for CLOSE status '2'
            if ("2".equalsIgnoreCase(bookingStatusModel.getStatus())) {
                if (bookingStatusModel.getInsCdClose() == null) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "resultCode", 400,
                            "message", "insCdClose is required when status is CLOSE"
                    ));
                }
            }

            ResponseGlobalModel<Object> responseGlobalModel = helper.doProcessBookingStatusIUD(bookingStatusModel);
            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
        } catch (CustomException e) {
            logger.error("Error handling request", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", Optional.of(e.getResultCode()));
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("errorDetails", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(e.getResultCode()).body(errorBody);
        } catch (Exception e) {
            logger.error("General error handling request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "resultCode", 500,
                    "message", "Internal server error",
                    "timestamp", serviceTool.generateTimestamp(),
                    "error", Map.of("errorDetails", e.getMessage())
            ));
        }
    }

    @PostMapping("/account/status/old")
    public ResponseEntity<Object> doProcessBookingStatusOld(@RequestBody String data) {

        Map<String, Object> templateData = new HashMap<>();

        try {
            logger.info("code===>");
            logger.info("Request Body account: {}", data);
            logger.info("bookCd: {}", data);
            logger.info("pathUrl====>" + dataUtil.getPathUpload());
            logger.info("profile====>" + serviceTool.getActiveProfile());

            BookingAccountModel bookingAccountModel = new Gson().fromJson(data, BookingAccountModel.class);


            ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
            logger.info("bookingAccountModel==>0>{}", new Gson().toJson(bookingAccountModel));

            // 1. Proses Header
            ResponseGlobalModel<Object> headerResponse = helper.doProcessBookingIUD(bookingAccountModel, bookingAccountModel.getBookCd(), "1");
            logger.info("headerResponse==>" + new Gson().toJson(headerResponse));
            if (headerResponse.getResultCode() != 200) {
                throw new CustomException(headerResponse.getMessage(), headerResponse.getResultCode(), headerResponse.getMessage());
            }
            logger.info("bookingAccountModel==>1>{}", new Gson().toJson(bookingAccountModel));
            @SuppressWarnings("unchecked")
            Map<String, Object> headerData = (Map<String, Object>) headerResponse.getData();
            String bookCd = (String) headerData.get("bookCd");
            logger.info("bookCd from Header Response: {}", bookCd);
            if (headerResponse.getResultCode() != 200) {
                throw new RuntimeException("Header processing failed: " + headerResponse.getMessage());
            }
            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage(headerResponse.getMessage());
            responseGlobalModel.setData(headerResponse.getData());

            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);

        } catch (CustomException e) {
            logger.error("Error handling request", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", Optional.of(e.getResultCode()));
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("errorDetails", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(e.getResultCode()).body(errorBody);
        } catch (Exception e) {
            logger.error("General error handling request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "resultCode", 500,
                    "message", "Internal server error",
                    "timestamp", serviceTool.generateTimestamp(),
                    "error", Map.of("errorDetails", e.getMessage())
            ));
        }
    }

    @PostMapping("/account/review")
    public ResponseEntity<Object> doProcessBookingReview(@RequestBody String data) {

        Map<String, Object> templateData = new HashMap<>();

        try {
            logger.info("code===>");
            logger.info("Request Body account: {}", data);
            logger.info("bookCd: {}", data);
            logger.info("pathUrl====>" + dataUtil.getPathUpload());
            logger.info("profile====>" + serviceTool.getActiveProfile());

            BookingReviewModel bookingReviewModel = new Gson().fromJson(data, BookingReviewModel.class);

            if (bookingReviewModel.getBookCd() == null || bookingReviewModel.getBookCd().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "resultCode", 400,
                        "message", "bookCd is required"
                ));
            }

            ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
            logger.info("bookingReviewModel==>0>{}", new Gson().toJson(bookingReviewModel));

            // 1. Proses Header
            ResponseGlobalModel<Object> headerResponse = helper.doProcessBookingReviewIUD(bookingReviewModel, bookingReviewModel.getBookCd(), "1");
            logger.info("headerResponse==>" + new Gson().toJson(headerResponse));
            if (headerResponse.getResultCode() != 200) {
                throw new CustomException(headerResponse.getMessage(), headerResponse.getResultCode(), headerResponse.getMessage());
            }
            logger.info("bookingAccountModel==>1>{}", new Gson().toJson(bookingReviewModel));
            @SuppressWarnings("unchecked")
            Map<String, Object> headerData = (Map<String, Object>) headerResponse.getData();
            String bookCd = (String) headerData.get("bookCd");
            logger.info("bookCd from Header Response: {}", bookCd);
            if (headerResponse.getResultCode() != 200) {
                throw new RuntimeException("Header processing failed: " + headerResponse.getMessage());
            }
            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage(headerResponse.getMessage());
            responseGlobalModel.setData(headerResponse.getData());

            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);

        } catch (CustomException e) {
            logger.error("Error handling request", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", Optional.of(e.getResultCode()));
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("errorDetails", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(e.getResultCode()).body(errorBody);
        } catch (Exception e) {
            logger.error("General error handling request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "resultCode", 500,
                    "message", "Internal server error",
                    "timestamp", serviceTool.generateTimestamp(),
                    "error", Map.of("errorDetails", e.getMessage())
            ));
        }
    }

    @PostMapping("/account/detail")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> doProcessBookingDetail(@RequestParam Map<String, MultipartFile> files,
                                                         @RequestParam("data") String data) {


        try {
            logger.info("code===>detail");
            logger.info("Request Body Detail: {}", data);
            logger.info("bookCd: {}", data);
            logger.info("pathUrl====>" + dataUtil.getPathUpload());
            logger.info("profile====>" + serviceTool.getActiveProfile());

            BookingAccountModel bookingAccountModel = new Gson().fromJson(data, BookingAccountModel.class);
            List<BookingAccountModel.DocType> docTypes = new ArrayList<>();
            ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();


            String bookCd = bookingAccountModel.getBookCd();
            System.out.println("<===files==>" + files);
            if (!bookingAccountModel.getActionType().equalsIgnoreCase("3")) {
                if (!files.isEmpty()) {
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
                            case "7" -> "companyprofile";
                            case "8" -> "Proposal";
                            case "9" -> "Comparation";
                            default -> docTypeOri;
                        };
                        logger.info("docTypeDesc==>{}", docTypeDesc);

                        int counter = calculateRevisionNumber(dataUtil.getPathUpload() + "/" + bookCd + "/");

                        try {
                            Path saved = fileStorageService.saveWithOverwriteAndBackup(
                                    "booking", bookCd, counter, null, docTypeDesc, file, Optional.ofNullable(bookingAccountModel.getCreatedBy()).orElse("system")
                            );
                            logger.info("Revised file saved as {}", saved);

                            docType.setCode(code);
                            docType.setRevDoc(String.valueOf(counter));
                            docType.setDescp(docTypeDesc);
                            docType.setUrlPath(saved.toString().replace(File.separatorChar, '/'));

                            docTypes.add(docType);

                        } catch (IOException e) {
                            logger.error("Failed to save file", e);
                        }
                        bookingAccountModel.setDocTypes(docTypes);
                    });
                }
                if (bookingAccountModel.getDocTypes() != null) {
                    responseGlobalModel = helper.doProcessBookingIUD(bookingAccountModel, bookCd, "2");
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);
        } catch (CustomException e) {
            logger.error("Error handling request", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", Optional.of(e.getResultCode()));
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("errorDetails", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(e.getResultCode()).body(errorBody);
        }
    }


    @PostMapping("/account/revisi")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> doProcessBookingRevisi(@RequestParam Map<String, MultipartFile> files,
                                                   @RequestParam("data") String data) {


        Map<String, Object> templateData = new HashMap<>();

        try {

            logger.info("Request Body account Revisi: {}", data);
            logger.info("bookCd: {}", data);
            logger.info("pathUrl====>" + dataUtil.getPathUpload());
            logger.info("profile====>" + serviceTool.getActiveProfile());

            BookingAccountModel bookingAccountModel = new Gson().fromJson(data, BookingAccountModel.class);
            List<BookingAccountModel.DocType> docTypes = new ArrayList<>();
            List<MultipartFile> attachments = new ArrayList<>(files.values());
            ResponseGlobalModel<Object> responseGlobalModel = new ResponseGlobalModel<>();
            bookingAccountModel.setStatus(Constants.BookingStatus.fromRaw(bookingAccountModel.getStatus()));
            logger.info("bookingAccountModel==>0>{}", new Gson().toJson(bookingAccountModel));

            String originalActionType = bookingAccountModel.getActionType();

            // 1. Proses Header
            ResponseGlobalModel<Object> headerResponse = helper.doProcessBookingIUD(bookingAccountModel, "", "1");
            logger.info("headerResponse==>" + new Gson().toJson(headerResponse));
            if (headerResponse.getResultCode() != 200) {
                throw new CustomException(headerResponse.getMessage(), headerResponse.getResultCode(), headerResponse.getMessage());
            }
            logger.info("bookingAccountModel==>1>{}", new Gson().toJson(bookingAccountModel));
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
            System.out.println("<===files==>" + files);
            boolean isLastRevSent = repository.isLastRevSent(bookingAccountModel.getBookCd());
            if (!isLastRevSent) {
                throw new CustomException("Cannot add a new revision because the last revision has not been sent!", 409, "The last revision has not been SENT");
            }
            int lastRev = repository.getNextBookingRev(bookingAccountModel.getBookCd());
            System.out.println("<===lastRev==>" + lastRev);
            bookingAccountModel.setActionType(originalActionType);
            //akomodir jika dokumen tidak ada perubahan
            bookingAccountModel.getDocTypes().forEach(docType -> docType.setRevDoc(String.valueOf(lastRev)));

            if (!bookingAccountModel.getActionType().equalsIgnoreCase("3")) {
                logger.info("bookingAccountModel==>2>{}", new Gson().toJson(bookingAccountModel));
                if (!files.isEmpty()) {
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
                            case "7" -> "companyprofile";
                            case "8" -> "Proposal";
                            case "9" -> "Comparation";
                            default -> docTypeOri;
                        };
                        try {
                            Path saved = fileStorageService.saveWithOverwriteAndBackup(
                                    "booking", bookCd, lastRev, null, docTypeDesc, file, Optional.ofNullable(bookingAccountModel.getCreatedBy()).orElse("system")
                            );
                            String finalPath = saved.toString().replace(File.separatorChar, '/');

                            logger.info("bookingAccountModel===>3>{}", new Gson().toJson(bookingAccountModel));
                            // Update isi di bookingAccountModel
                            bookingAccountModel.getDocTypes().forEach(d -> {
                                if (d.getCode().equalsIgnoreCase(code)) {
                                    d.setDescp(docTypeDesc);
                                    String oldPath = d.getUrlPath();

                                    if (oldPath == null || oldPath.startsWith("blob:") || oldPath.isBlank()) {
                                        d.setActionType("1");
                                    } else if (!oldPath.equals(finalPath)) {
                                        d.setActionType("2");
                                    } else {
                                        d.setActionType("2");
                                    }
                                    d.setUrlPath(finalPath);
                                }
                                d.setRevDoc(String.valueOf(lastRev));
                            });
                        } catch (IOException e) {
                            logger.error("Failed to save file", e);
                        }
                    });
                }
                logger.info("✅ DocTypes after update files: " + new Gson().toJson(bookingAccountModel.getDocTypes()));
                if (bookingAccountModel.getDocTypes() != null) {

                    List<BookingAccountModel.DocType> docTypesToProcess;
                    if ("2".equalsIgnoreCase(bookingAccountModel.getActionType())) {
                        docTypesToProcess = bookingAccountModel.getDocTypes().stream()
                                .filter(d -> d.getActionType() != null && !d.getActionType().isBlank())
                                .collect(Collectors.toList());
                    } else {
                        docTypesToProcess = bookingAccountModel.getDocTypes();
                    }

                    //Proses Detail
                    if (!docTypesToProcess.isEmpty()) {
                        BookingAccountModel bookingDetailModel = new BookingAccountModel();

                        BeanUtils.copyProperties(bookingAccountModel, bookingDetailModel);
                        bookingDetailModel.setDocTypes(docTypesToProcess);
                        bookingDetailModel.setRevDoc(String.valueOf(lastRev));

                        responseGlobalModel = helper.doProcessBookingIUD(bookingDetailModel, bookCd, "2");
                    }
                    List<String> attachmentUrls = new ArrayList<>();

                    for (BookingAccountModel.DocType docType : bookingAccountModel.getDocTypes()) {
                        String urlPath = docType.getUrlPath();
                        if (urlPath != null && !urlPath.isEmpty()) {
                            if (!docType.isNew()) {
                                attachmentUrls.add(urlPath);
                            }
                        }
                    }
                    List<MultipartFile> urlFiles = serviceTool.convertUrlsToMultipartFiles(attachmentUrls);
                    attachments.addAll(urlFiles);
                }
            }



            if (responseGlobalModel.getResultCode() == 200) {
                if (bookingAccountModel.getActionType().equalsIgnoreCase("1")) {
                    EmailRequestModel emailRequestModel = new EmailRequestModel();
                    String mailType = "";
                    if (bookingAccountModel.getActionType().equalsIgnoreCase("1")) {
                        mailType = "BKNEW";
                    }
                    emailRequestModel.setMailType(mailType);
                    emailRequestModel.setActionType("1");
                    emailRequestModel.setBookCd(bookCd);
                    emailRequestModel.setClientName(bookingAccountModel.getClientName());
                    emailRequestModel.setCode(bookCd);
                    emailRequestModel.setCreatedBy(bookingAccountModel.getCreatedBy() == null ? "System" : bookingAccountModel.getCreatedBy());
                    bookingAccountModel.setBookCd(bookCd);
                    templateData.put("bookCd", bookCd);
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> paramTemplateMap = objectMapper.convertValue(bookingAccountModel, new TypeReference<Map<String, Object>>() {});
                    emailRequestModel.setParamTemplate(paramTemplateMap);
                    System.out.println("<==emailRequestModel==" + new Gson().toJson(emailRequestModel));
                    emailService.sendEmailWithAttachments(
                            new Gson().toJson(emailRequestModel),
                            attachments, serviceTool.getProperty("email.service.url") + "/email/send"
                    );
                } else if (bookingAccountModel.getActionType().equalsIgnoreCase("4")) {
                    EmailRequestModel emailRequestModel = new EmailRequestModel();
                    String mailType = "";
                    if (bookingAccountModel.getActionType().equalsIgnoreCase("4")) {
                        mailType = "BKREV";
                    }
                    emailRequestModel.setMailType(mailType);
                    emailRequestModel.setActionType("1");
                    emailRequestModel.setBookCd(bookCd);
                    emailRequestModel.setClientName(bookingAccountModel.getClientName());
                    emailRequestModel.setCode(bookCd);
                    emailRequestModel.setCreatedBy(bookingAccountModel.getCreatedBy() == null ? "System" : bookingAccountModel.getCreatedBy());
                    bookingAccountModel.setBookCd(bookCd);
                    templateData.put("bookCd", bookCd);
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> paramTemplateMap = objectMapper.convertValue(bookingAccountModel, new TypeReference<Map<String, Object>>() {});
                    emailRequestModel.setParamTemplate(paramTemplateMap);
                    System.out.println("<==emailRequestModel==" + new Gson().toJson(emailRequestModel));
                    emailService.sendEmailWithAttachments(
                            new Gson().toJson(emailRequestModel),
                            attachments, serviceTool.getProperty("email.service.url") + "/email/send"
                    );
                }
            }
            logger.info("responseGlobalModel==>" + new Gson().toJson(responseGlobalModel));
            return ResponseEntity.status(HttpStatus.OK).body(responseGlobalModel);

        } catch (CustomException e) {
            logger.error("Error handling request", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("resultCode", Optional.of(e.getResultCode()));
            errorBody.put("timestamp", serviceTool.generateTimestamp());
            errorBody.put("message", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("errorDetails", e.getMessage());
            errorBody.put("error", error);
            return ResponseEntity.status(e.getResultCode()).body(errorBody);
        }
    }

    private void processFile(String code, MultipartFile file, String bookCd, int rev, DataUtil dataUtil,
                             BookingAccountModel bookingAccountModel, Logger logger) {
        try {
            logger.info("Processing code={}, fileOriginalName={}", code, file.getOriginalFilename());

            String docTypeOri = file.getName();
            String docTypeDesc = switch (code) {
                case "1" -> "memberlist";
                case "2" -> "benefit";
                case "3" -> "claimdetail";
                case "4" -> "claimratio";
                case "5" -> "termcondition";
                case "6" -> "loa";
                case "7" -> "companyprofile";
                case "8" -> "Proposal";
                case "9" -> "Comparation";
                default -> docTypeOri;
            };

            String actor = Optional.ofNullable(bookingAccountModel.getCreatedBy()).orElse("system");
            Path saved = fileStorageService.saveWithOverwriteAndBackup(
                    "booking", bookCd, rev, null, docTypeDesc, file, actor
            );
            String finalPath = saved.toString().replace(java.io.File.separatorChar, '/');

            // Update isi di bookingAccountModel
            bookingAccountModel.getDocTypes().forEach(d -> {
                if (d.getCode().equalsIgnoreCase(code)) {
                    d.setDescp(docTypeDesc);
                    d.setRevDoc(String.valueOf(rev));
                    String oldPath = d.getUrlPath();
                    if (oldPath == null || oldPath.startsWith("blob:") || oldPath.isBlank()) {
                        d.setActionType("1");
                    } else if (!oldPath.equals(finalPath)) {
                        d.setActionType("2");
                    } else {
                        d.setActionType("2");
                    }
                    d.setUrlPath(finalPath);
                }
            });
            logger.info("✅ DocTypes after update files: " + new Gson().toJson(bookingAccountModel.getDocTypes()));
        } catch (Exception e) {
            logger.error("Failed to save file", e);
        }
    }


    private int calculateRevisionNumber(String bookFolderPath) {
        File bookFolder = new File(bookFolderPath);

        if (!bookFolder.exists() || !bookFolder.isDirectory()) {
            return 0;
        }

        File[] revFolders = bookFolder.listFiles(File::isDirectory);
        int maxRevision = 0;

        if (revFolders != null) {
            for (File folder : revFolders) {
                String folderName = folder.getName();
                if (folderName.matches("rev\\d+")) {
                    try {
                        int revNumber = Integer.parseInt(folderName.substring(3)); // Ambil angka setelah "rev"
                        maxRevision = Math.max(maxRevision, revNumber);
                    } catch (NumberFormatException ignored) {
                        // Abaikan jika bukan angka valid
                    }
                }
            }
        }

        return maxRevision + 1;
    }

    private String determineActionType(BookingAccountModel.DocType doc) {
        String urlPath = doc.getUrlPath();
        if (urlPath != null && urlPath.contains("/document/support/")) {
            File existingFile = new File(urlPath);
            return existingFile.exists() ? "2" : "1";
        }
        return "1";
    }

    private void createDirectoriesRecursively(File folder) {
        if (folder == null) return;
        if (folder.exists()) return;

        File parent = folder.getParentFile();
        if (parent != null && !parent.exists()) {
            createDirectoriesRecursively(parent);
        }

        if (!folder.exists()) {
            boolean created = folder.mkdir();
            logger.info("Created folder: {} => {}", folder.getAbsolutePath(), created);
        }
    }
}
