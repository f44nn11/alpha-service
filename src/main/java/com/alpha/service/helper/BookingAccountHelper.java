package com.alpha.service.helper;


import com.alpha.service.exception.CustomException;
import com.alpha.service.mapper.BookingAccountMapper;
import com.alpha.service.model.BookingAccountModel;
import com.alpha.service.model.BookingReviewModel;
import com.alpha.service.model.BookingStatusModel;
import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.model.procedure.UspBookingAccountDtlParam;
import com.alpha.service.model.procedure.UspBookingAccountGetParam;
import com.alpha.service.model.procedure.UspBookingAccountParam;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.service.BookingAccountDataService;
import com.alpha.service.service.BookingAccountSIUDService;
import com.google.gson.Gson;
import jakarta.persistence.ParameterMode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.*;

/*
 * Created by: fkusu
 * Date: 12/15/2024
 */
@Slf4j
@Service
public class BookingAccountHelper {
    private static final Logger logger = LoggerFactory.getLogger(BookingAccountHelper.class);

    @Autowired
    private BookingAccountSIUDService bookingAccountSIUDService;

    @Autowired
    private BookingAccountDataService bookingAccountDataService;

    @Autowired
    private DataSource dataSource;


    public ResponseGlobalModel<Object> doProcessBookingAccount(UspBookingAccountGetParam bpm) {
        ResponseGlobalModel<Object> responseGlobalModel;
        ResponseGlobalModel<Object> resp;
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_BOOKING_ACCOUNT_GET";
            System.out.println("clientMasterModel===>" + new Gson().toJson(bpm));
            params = buildBookingAccountParamsFromModel(bpm);
            List<String> resultColumns = Arrays.asList(
                    "BOOKCD", "CLIENTCODE", "CLIENTNAME", "MKTID", "FULLNAME", "BOOKDT", "PREVINS",
                    "INSNAME", "ISSUEDT", "EXPDT", "PREMIUMBGT", "TOTMEMBERS",
                    "IP", "OP", "DT", "MT", "GL", "DESCRIPTION",
                    "STATUS", "CREATEDT"
            );

            responseGlobalModel = bookingAccountDataService.callBookingAccountProcedure(procedureName, params, resultColumns);
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
            throw e;
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessBookingReview(UspBookingAccountGetParam bpm) {
        ResponseGlobalModel<Object> responseGlobalModel;
        ResponseGlobalModel<Object> resp;
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_BOOKING_REVIEW_GET";
            params = buildBookingAccountParamsFromModel(bpm);

            responseGlobalModel = bookingAccountDataService.callBookingReviewProcedure(procedureName, params);
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
            throw e;
        }
        return responseGlobalModel;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessBookingIUD(BookingAccountModel bookingAccountModel, String bookCd, String actionType) {
        ResponseGlobalModel<Object> responseGlobalModel;
        ResponseGlobalModel<Object> resp = new ResponseGlobalModel<>();
        String HeaderProcedureName;
        String DetailProcedureName;

        try {
            if (actionType.equalsIgnoreCase("1")) {
                if (bookingAccountModel.getActionType().equalsIgnoreCase("3")) {
                    UspBookingAccountParam hdrParam = BookingAccountMapper.INSTANCE.toUspBookingAccountParam(bookingAccountModel);
                    if (bookCd == null || bookCd.isBlank()) {
                        bookCd = bookingAccountModel.getBookCd();
                    }
                    hdrParam.setBookCd(bookCd);
                    hdrParam.setActionType("3");
                    hdrParam.setPrevIns(String.valueOf(0));

                    List<ProcedureParamModel> hdrParams = buildParamProcedure(hdrParam);
                    resp = bookingAccountSIUDService.bookingAccountProcedure("USP_BOOKING_ACCOUNT", hdrParams);

                    if (resp.getResultCode() != 200) {
                        ResponseGlobalModel<Object> errorResp = new ResponseGlobalModel<>();
                        errorResp.setResultCode(400);
                        errorResp.setMessage(resp.getMessage());
                        errorResp.setError(Map.of("error", resp.getMessage()));
                        return errorResp;
                    }
                    return resp;
                }

                // Jika actionType adalah 1, maka kita akan melakukan insert atau update pada booking account
                // 1. Mapping ke UspBookingAccountParam
                HeaderProcedureName = "USP_BOOKING_ACCOUNT";
                bookingAccountModel.setActionType(bookingAccountModel.getActionType().equalsIgnoreCase("4") ? "2" : bookingAccountModel.getActionType());

                UspBookingAccountParam uspParam = BookingAccountMapper.INSTANCE.toUspBookingAccountParam(bookingAccountModel);
                logger.info("uspParam==> {}", uspParam);


                List<ProcedureParamModel> headerParams = buildParamProcedure(uspParam);
                logger.info("headerParams==> {}", headerParams);
                resp = bookingAccountSIUDService.bookingAccountProcedure(HeaderProcedureName, headerParams);

                if (resp.getResultCode() != 200) {
                    throw new CustomException(resp.getMessage(), resp.getResultCode(), resp.getData() == null ? resp.getError() : resp.getData());
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> headerData = (Map<String, Object>) resp.getData();
                bookCd = (String) headerData.get("bookCd");
                String clientCode = (String) headerData.get("clientCode");
                String marketingCode = (String) headerData.get("marketingCode");

                logger.info("Header Response Data: bookCd={}, clientCode={}, marketingCode={}", bookCd, clientCode, marketingCode);

            }

            if (actionType.equalsIgnoreCase("2")) {
                // 2. Mapping DocType ke UspBookingAccountDtlParam
                DetailProcedureName = "USP_BOOKING_ACCOUNT_DTL";
                String finalBookCd = bookCd;

                if (bookingAccountModel.getDocTypes() == null || bookingAccountModel.getDocTypes().isEmpty()) {
                    throw new RuntimeException("DocTypes is empty for actionType = 2");
                }

//                List<UspBookingAccountDtlParam> detailParamsDtl = bookingAccountModel.getDocTypes()
//                        .stream()
//                        .map(docType -> BookingAccountMapper.INSTANCE
//                                .toUspBookingAccountDtlParam(finalBookCd, docType,bookingAccountModel.getCreatedBy(),"1A"))
//                        .toList();
                List<UspBookingAccountDtlParam> detailParams = bookingAccountModel.getDocTypes()
                        .stream()
                        .map(docType -> BookingAccountMapper.INSTANCE.toUspBookingAccountDtlParam(
                                finalBookCd,
                                docType,
                                bookingAccountModel,
                                bookingAccountModel.getCreatedBy()
                        ))
                        .distinct()
                        .toList();

//                if (!detailParamsDtl.isEmpty()) {
//
//
//                    List<ProcedureParamModel> dtlParamsDtl = buildParamProcedure(detailParamsDtl.get(0));
//
//                    resp = bookingAccountSIUDService.bookingAccountProcedure(DetailProcedureName, dtlParamsDtl);
//
//                    if (resp.getResultCode() != 200) {
//                        throw new RuntimeException("Detail processing failed: " + resp.getMessage());
//                    }
//                }

                // Panggil procedure untuk setiap detail doc
                logger.info("<===detailParams0===>" + new Gson().toJson(detailParams));
                for (UspBookingAccountDtlParam detailParam : detailParams) {
                    logger.info("<===detailParam1===>" + new Gson().toJson(detailParam));
                    List<ProcedureParamModel> dtlParams = buildParamProcedure(detailParam);

                    resp = bookingAccountSIUDService.bookingAccountProcedure(DetailProcedureName, dtlParams);

                    if (resp.getResultCode() != 200) {
                        throw new RuntimeException("Detail Doc processing failed: " + resp);
                    }
                }
            }

            if (actionType.equalsIgnoreCase("4")) {
                // 2. Mapping DocType ke UspBookingAccountDtlParam
                DetailProcedureName = "USP_BOOKING_ACCOUNT_DTL";
                String finalBookCd = bookCd;

                // Step 1: Insert Dokumen Tipe ke DTL (khusus 1A)
                for (BookingAccountModel.DocType docType : bookingAccountModel.getDocTypes()) {
                    UspBookingAccountDtlParam param = BookingAccountMapper.INSTANCE
                            .toUspBookingAccountDtlParam(finalBookCd, docType, bookingAccountModel, bookingAccountModel.getCreatedBy());

                    param.setActionType("1A");

                    List<ProcedureParamModel> dtlParams = buildParamProcedure(param);
                    resp = bookingAccountSIUDService.bookingAccountProcedure(DetailProcedureName, dtlParams);

                    if (resp.getResultCode() != 200) {
                        throw new RuntimeException("Doc insert failed: " + resp.getMessage());
                    }
                }

                // Step 2: Insert/Update DTL data yang lengkap dengan actionType "4"
                for (BookingAccountModel.DocType docType : bookingAccountModel.getDocTypes()) {
                    UspBookingAccountDtlParam param = BookingAccountMapper.INSTANCE
                            .toUspBookingAccountDtlParam(finalBookCd, docType, bookingAccountModel, bookingAccountModel.getCreatedBy());

                    param.setActionType(bookingAccountModel.getActionType()); // "4"

                    List<ProcedureParamModel> dtlParams = buildParamProcedure(param);
                    resp = bookingAccountSIUDService.bookingAccountProcedure(DetailProcedureName, dtlParams);

                    if (resp.getResultCode() != 200) {
                        throw new RuntimeException("Detail DTL update failed: " + resp.getMessage());
                    }
                }
            }


            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage("Booking account processed successfully");
            responseGlobalModel.setData(resp.getData());
        } catch (CustomException e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(e.getResultCode());
            responseGlobalModel.setMessage(e.getMessage());
            responseGlobalModel.setError((Map<String, String>) e.getData());
            throw e;
        }
        return responseGlobalModel;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessBookingReviewIUD(BookingReviewModel model, String bookCd, String actionType) {
        ResponseGlobalModel<Object> responseGlobalModel;
        ResponseGlobalModel<Object> resp = new ResponseGlobalModel<>();
        String HeaderProcedureName;

        try {


            List<ProcedureParamModel> params = buildBookingReviewParams(model, bookCd, actionType);
            resp = bookingAccountSIUDService.bookingAccountProcedure("USP_BOOKING_REVIEW", params);

            if (resp.getResultCode() != 200) {
                throw new CustomException(resp.getMessage(), resp.getResultCode(), resp.getData());
            }

            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage("Booking account processed successfully");
            responseGlobalModel.setData(resp.getData());
        } catch (CustomException e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(e.getResultCode());
            responseGlobalModel.setMessage(e.getMessage());
            responseGlobalModel.setError((Map<String, String>) e.getData());
            throw e;
        }
        return responseGlobalModel;
    }

    private List<ProcedureParamModel> buildBookingReviewParams(BookingReviewModel model, String bookCd, String actionType) {
        List<ProcedureParamModel> params = new ArrayList<>();
        try {
            params.add(new ProcedureParamModel("p_bookCd", bookCd != null ? bookCd : model.getBookCd(), String.class, ParameterMode.IN));
            // Convert reviewDate String to Timestamp if necessary
            Object reviewDateObj = model.getReviewDate();
            java.sql.Timestamp reviewDate = null;
            if (reviewDateObj instanceof String) {
                String dateStr = ((String) reviewDateObj).trim();
                if (!dateStr.isEmpty() && !"null".equalsIgnoreCase(dateStr)) {
                    if (dateStr.length() == 10) {
                        dateStr = dateStr + " 00:00:00";
                    }
                    if (dateStr.length() > 19) {
                        dateStr = dateStr.substring(0, 19);
                    }
                    reviewDate = java.sql.Timestamp.valueOf(dateStr);
                }
            } else if (reviewDateObj instanceof java.sql.Timestamp) {
                reviewDate = (java.sql.Timestamp) reviewDateObj;
            }
            params.add(new ProcedureParamModel("p_reviewDate", reviewDate, java.sql.Timestamp.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_subject", model.getSubject(), String.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_subjectDtl", model.getSubjectDtl(), String.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_result", model.getResult(), String.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_createdBy", model.getCreatedBy(), String.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_actionType", actionType, String.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT));
            params.add(new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT));
            params.add(new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT));
        } catch (Exception e) {
            throw new RuntimeException("Error building procedure parameters", e);
        }
        return params;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessBookingStatusIUD(BookingStatusModel model) {
        ResponseGlobalModel<Object> responseGlobalModel;
        ResponseGlobalModel<Object> resp = new ResponseGlobalModel<>();
        try {
            List<ProcedureParamModel> params = buildBookingStatusParams(model);
            resp = bookingAccountSIUDService.bookingAccountProcedure("USP_BOOKING_STATUS", params);
            if (resp.getResultCode() != 200) {
                throw new CustomException(resp.getMessage(), resp.getResultCode(), resp.getData());
            }
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(200);
            responseGlobalModel.setMessage("Booking status processed successfully");
            responseGlobalModel.setData(resp.getData());
        } catch (CustomException e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(e.getResultCode());
            responseGlobalModel.setMessage(e.getMessage());
            responseGlobalModel.setError((Map<String, String>) e.getData());
            throw e;
        }
        return responseGlobalModel;
    }

    private List<ProcedureParamModel> buildBookingStatusParams(BookingStatusModel model) {
        List<ProcedureParamModel> params = new ArrayList<>();
        try {
            final String status = model.getStatus();
            final boolean isClose = "2".equals(status);

            // Normalisasi insCdClose & closeDt
            Long insCdClose = isClose ? model.getInsCdClose() : null;
            java.sql.Timestamp closeDt = isClose ? coerceToTimestamp(model.getCloseDt()) : null;

            params.add(new ProcedureParamModel("p_bookCd", model.getBookCd(), String.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_status", status, String.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_description", model.getDescription(), String.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_insCdClose", insCdClose, Long.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_closeDt", closeDt, java.sql.Timestamp.class, ParameterMode.IN));
            params.add(new ProcedureParamModel("p_createdBy", model.getCreatedBy(), String.class, ParameterMode.IN));

            // OUT params
            params.add(new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT));
            params.add(new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT));
            params.add(new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT));
        } catch (Exception e) {
            throw new RuntimeException("Error building procedure parameters", e);
        }
        return params;
    }

    private static java.sql.Timestamp coerceToTimestamp(Object closeDtObj) {
        if (closeDtObj == null) return null;
        if (closeDtObj instanceof java.sql.Timestamp ts) return ts;
        if (closeDtObj instanceof java.time.LocalDateTime ldt) return java.sql.Timestamp.valueOf(ldt);
        if (closeDtObj instanceof java.util.Date d) return new java.sql.Timestamp(d.getTime());

        if (closeDtObj instanceof CharSequence cs) {
            String s = cs.toString().trim();
            if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
            if (s.length() == 10) s += " 00:00:00";     // yyyy-MM-dd → pad time
            if (s.length() > 19)  s  = s.substring(0, 19); // trim millis/timezone
            return java.sql.Timestamp.valueOf(s);      // expects yyyy-MM-dd HH:mm:ss
        }
        throw new IllegalArgumentException("Unsupported closeDt type: " + closeDtObj.getClass());
    }

    private List<ProcedureParamModel> buildBookingAccountParamsFromModel(UspBookingAccountGetParam model) {
        Map<String, String> fieldToParamMap = Map.ofEntries(
                Map.entry("bookCd", "p_bookCd"),
                Map.entry("clientCode", "p_clientCode"),
                Map.entry("mktId", "p_mktId"),
                Map.entry("clientName", "p_clientName"),
                Map.entry("param1", "p_param1"),
                Map.entry("param2", "p_param2"),
                Map.entry("param3", "p_param3"),
                Map.entry("actionType", "p_actionType")
        );

        List<ProcedureParamModel> params = new ArrayList<>();
        Field[] fields = model.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(model);
                String fieldName = field.getName();


                String paramName;
                if (fieldToParamMap.containsKey(fieldName)) {
                    paramName = fieldToParamMap.get(fieldName);
                } else if (fieldName.toLowerCase().startsWith("p_")) {
                    paramName = fieldName;
                } else {
                    paramName = "p_" + fieldName.toLowerCase();
                }

                Class<?> type = (value != null) ? value.getClass() : String.class;
                Object finalValue;
                if (field.getType().equals(String.class) &&
                        (fieldName.equalsIgnoreCase("bookDate") ||
                                fieldName.equalsIgnoreCase("effDate") ||
                                fieldName.equalsIgnoreCase("expDate"))) {

                    if (value == null || ((String) value).isBlank()) {
                        finalValue = null;
                    } else {
                        // Convert String ke java.sql.Date
                        finalValue = java.sql.Date.valueOf((String) value);
                    }
                } else if (value != null) {
                    finalValue = value;
                } else if (field.getType().equals(String.class)) {
                    finalValue = "";
                } else {
                    finalValue = null;
                }

                params.add(new ProcedureParamModel(paramName, finalValue, type, ParameterMode.IN));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error building procedure parameters", e);
        }
        params.add(new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT));
        return params;
    }


    private <T> List<ProcedureParamModel> buildParamProcedure(T model) {
        Map<String, String> fieldToParamMap = Map.ofEntries(
                // Common HDR fields
                Map.entry("bookCd", "p_bookCd"),
                Map.entry("clientCode", "p_clientCode"),
                Map.entry("mktId", "p_mktId"),
                Map.entry("bookDate", "p_bookDate"),
                Map.entry("effDate", "p_effDate"),
                Map.entry("expDate", "p_expDate"),
                Map.entry("descriptionDtl", "p_description"),
                Map.entry("status", "p_status"),
                Map.entry("insPlacing", "p_insPlacing"),
                Map.entry("clientName", "p_clientName"),
                Map.entry("param1", "p_param1"),
                Map.entry("param2", "p_param2"),
                Map.entry("param3", "p_param3"),

                // DTL fields
                Map.entry("rev", "p_rev"),
                Map.entry("prevIns", "p_prevIns"),
                Map.entry("premiumBudget", "p_premiumBudget"),
                Map.entry("totMembers", "p_totMembers"),
                Map.entry("ip", "p_ip"),
                Map.entry("op", "p_op"),
                Map.entry("dt", "p_dt"),
                Map.entry("mt", "p_mt"),
                Map.entry("gl", "p_gl"),

                // Common
                Map.entry("createdBy", "p_createdby"),
                Map.entry("actionType", "p_actionType")
        );

        List<ProcedureParamModel> params = new ArrayList<>();
        Field[] fields = model.getClass().getDeclaredFields();

        try {
            Set<String> codeFields = Set.of("prevIns", "mktId", "clientCode");

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(model);
                String fieldName = field.getName();


                String paramName;
                if (fieldToParamMap.containsKey(fieldName)) {
                    paramName = fieldToParamMap.get(fieldName);
                } else if (fieldName.toLowerCase().startsWith("p_")) {
                    paramName = fieldName;
                } else {
                    paramName = "p_" + fieldName.toLowerCase();
                }

                Class<?> type = (value != null) ? value.getClass() : String.class;
                Object finalValue;
                if (codeFields.contains(fieldName)) {
                    finalValue = extractCode(value);
                } else if (Set.of("bookDate", "effDate", "expDate").contains(fieldName)) {
                    String dateStr = null;
                    if (value instanceof String) {
                        dateStr = ((String) value).trim();
                    } else if (value != null) {
                        dateStr = value.toString().trim();
                    }
                    // PENCEGAHAN TAMBAHAN: "null" string juga diabaikan
                    if (dateStr == null || dateStr.isEmpty() || "null".equalsIgnoreCase(dateStr)) {
                        finalValue = null;
                        type = java.sql.Timestamp.class;
                    } else {
                        try {
                            if (dateStr.length() == 10) {
                                dateStr = dateStr + " 00:00:00";
                            }
                            if (dateStr.length() > 19) {
                                dateStr = dateStr.substring(0, 19);
                            }
                            finalValue = java.sql.Timestamp.valueOf(dateStr);
                            type = java.sql.Timestamp.class;
                        } catch (IllegalArgumentException e) {
                            // LOG kan errornya supaya tahu data aneh apa yang masuk
                            System.err.println("Invalid dateStr for " + fieldName + ": '" + dateStr + "'");
                            finalValue = null; // atau throw custom jika ingin error
                        }
                    }
                } else if (value != null) {
                    finalValue = value;
                } else if (field.getType().equals(String.class)) {
                    finalValue = "";
                } else {
                    finalValue = null;
                }

                params.add(new ProcedureParamModel(paramName, finalValue, type, ParameterMode.IN));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error building procedure parameters", e);
        }

        // Tambahkan parameter OUT jika diperlukan
        params.add(new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT));

        return params;
    }

    private Object extractCode(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            Object codeObj = ((Map<?, ?>) value).get("code");
            return (codeObj == null || codeObj.toString().isBlank()) ? null : codeObj;
        }
        if (hasGetCodeMethod(value)) {
            try {
                Object codeObj = value.getClass().getMethod("getCode").invoke(value);
                return (codeObj == null || codeObj.toString().isBlank()) ? null : codeObj;
            } catch (Exception e) {
                // ignore, fallback
            }
        }
        // Jika langsung angka (Integer) atau string angka
        if (value instanceof Number) {
            return value;
        }
        if (value instanceof String) {
            String str = ((String) value).trim();
            return str.isBlank() ? null : str;
        }
        return null;
    }

    private boolean hasGetCodeMethod(Object obj) {
        try {
            obj.getClass().getMethod("getCode");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    // Utility to convert camelCase to snake_case
    private String toSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z]+)", "$1$2").toLowerCase();
    }
}
