package com.alpha.service.helper;


import com.alpha.service.exception.CustomException;
import com.alpha.service.mapper.BookingAccountMapper;
import com.alpha.service.model.BookingAccountModel;
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
                    "BOOKCD", "CLIENTCODE", "CLIENTNAME", "MKTID","FULLNAME", "BOOKDT", "PREVINS",
                    "INSNAME","ISSUEDT", "EXPDT", "PREMIUMBGT","TOTMEMBERS",
                    "IP", "OP", "DT", "MT", "GL", "DESCRIPTION",
                    "STATUS", "CREATEDT"
            );

            responseGlobalModel = bookingAccountDataService.callBookingAccountProcedure(procedureName, params, resultColumns);
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseGlobalModel<Object> doProcessBookingIUD(BookingAccountModel bookingAccountModel,String bookCd, String actionType) {
        ResponseGlobalModel<Object> responseGlobalModel;
        ResponseGlobalModel<Object> resp = new ResponseGlobalModel<>();
        String HeaderProcedureName;
        String DetailProcedureName;

        try {
            if (actionType.equalsIgnoreCase("1")){
                // 1. Mapping ke UspBookingAccountParam
                HeaderProcedureName = "USP_BOOKING_ACCOUNT";
                UspBookingAccountParam uspParam = BookingAccountMapper.INSTANCE.toUspBookingAccountParam(bookingAccountModel);

                String insPlacingJson = new Gson().toJson(bookingAccountModel.getInsPlacing());
                uspParam.setInsPlacing(insPlacingJson);

                List<ProcedureParamModel> headerParams = buildParamProcedure(uspParam);
                resp = bookingAccountSIUDService.bookingAccountProcedure(HeaderProcedureName, headerParams);

                if (resp.getResultCode() != 200) {
                    throw new CustomException(resp.getMessage(), resp.getResultCode(),resp.getData() == null ? resp.getError() : resp.getData());
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> headerData = (Map<String, Object>) resp.getData();
                bookCd = (String) headerData.get("bookCd");
                String clientCode = (String) headerData.get("clientCode");
                String marketingCode = (String) headerData.get("marketingCode");

                logger.info("Header Response Data: bookCd={}, clientCode={}, marketingCode={}", bookCd, clientCode, marketingCode);

            }

            if (actionType.equalsIgnoreCase("2")){
                // 2. Mapping DocType ke UspBookingAccountDtlParam
                DetailProcedureName = "USP_BOOKING_ACCOUNT_DTL";
                String finalBookCd = bookCd;
                List<UspBookingAccountDtlParam> detailParams = bookingAccountModel.getDocTypes()
                        .stream()
                        .map(docType -> BookingAccountMapper.INSTANCE
                                .toUspBookingAccountDtlParam(finalBookCd, docType,bookingAccountModel.getCreatedBy(),bookingAccountModel.getActionType()))
                        .toList();

                // Panggil procedure untuk setiap detail
                for (UspBookingAccountDtlParam detailParam : detailParams) {
                    List<ProcedureParamModel> dtlParams = buildParamProcedure(detailParam);

                    resp = bookingAccountSIUDService.bookingAccountProcedure(DetailProcedureName, dtlParams);

                    if (resp.getResultCode() != 200) {
                        throw new RuntimeException("Detail processing failed: " + resp.getMessage());
                    }
                }
            }

            if (actionType.equalsIgnoreCase("3")){
                // 2. Mapping DocType ke UspBookingAccountDtlParam
                DetailProcedureName = "USP_BOOKING_ACCOUNT";
                String finalBookCd = bookCd;
                List<UspBookingAccountDtlParam> detailParams = bookingAccountModel.getDocTypes()
                        .stream()
                        .map(docType -> BookingAccountMapper.INSTANCE
                                .toUspBookingAccountDtlParam(finalBookCd, docType,bookingAccountModel.getCreatedBy(),bookingAccountModel.getActionType()))
                        .toList();

                // Panggil procedure untuk setiap detail
                for (UspBookingAccountDtlParam detailParam : detailParams) {
                    List<ProcedureParamModel> dtlParams = buildParamProcedure(detailParam);

                    resp = bookingAccountSIUDService.bookingAccountProcedure(DetailProcedureName, dtlParams);

                    if (resp.getResultCode() != 200) {
                        throw new RuntimeException("Detail processing failed: " + resp.getMessage());
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
        }
        return responseGlobalModel;
    }

    private List<ProcedureParamModel> buildBookingAccountParamsFromModel(UspBookingAccountGetParam model) {
        List<ProcedureParamModel> params = new ArrayList<>();
        Field[] fields = model.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true); // Access private fields
                Object value = field.get(model);
                if (value != null) { // Skip null fields
                    params.add(new ProcedureParamModel(
                            "p_" + toSnakeCase(field.getName()), // Convert field name to procedure param name
                            value,
                            field.getType(),
                            ParameterMode.IN
                    ));
                } else {
                    // Tambahkan nilai default jika null
                    params.add(new ProcedureParamModel(
                            "p_" + toSnakeCase(field.getName()),
                            "",
                            String.class,
                            ParameterMode.IN
                    ));
                }
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
        List<ProcedureParamModel> params = new ArrayList<>();
        Field[] fields = model.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true); // Mengakses field private
                Object value = field.get(model);
                if (value != null) {
                    params.add(new ProcedureParamModel(
                            "p_" + toSnakeCase(field.getName()), // Ubah nama field ke format snake_case
                            value,
                            field.getType(),
                            ParameterMode.IN
                    ));
                } else {
                    params.add(new ProcedureParamModel(
                            "p_" + toSnakeCase(field.getName()),
                            "",
                            String.class,
                            ParameterMode.IN
                    ));
                }
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

    // Utility to convert camelCase to snake_case
    private String toSnakeCase(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
