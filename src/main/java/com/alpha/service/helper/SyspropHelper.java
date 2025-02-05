package com.alpha.service.helper;

/*
 * Created by: fkusu
 * Date: 12/1/2024
 */

import com.alpha.service.model.*;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.service.ClientMasterDataService;
import com.alpha.service.service.SyspropInsUpdDelService;
import com.google.gson.Gson;
import jakarta.persistence.ParameterMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class SyspropHelper {

    @Autowired
    private SyspropInsUpdDelService syspropInsUpdDelService;

    @Autowired
    private ClientMasterDataService clientMasterDataService;

    public ResponseGlobalModel<Object> doProcessClient(ClientMasterModel clientMasterModel) {
        ResponseGlobalModel<Object> responseGlobalModel;
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_CLIENT_MASTER";
            System.out.println("clientMasterModel===>" + new Gson().toJson(clientMasterModel));
            params = buildParamsFromModel(clientMasterModel);
            responseGlobalModel = syspropInsUpdDelService.syspropInsUpdProcedure(procedureName, params);
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessClientPic(ClientMasterPicModel clientMasterPicModel) {
        ResponseGlobalModel<Object> responseGlobalModel;
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_CLIENT_MASTER_PIC";
            System.out.println("clientMasterModel===>" + new Gson().toJson(clientMasterPicModel));
//            clientMasterPicModel = new ClientMasterPicModel();
//            clientMasterPicModel.setSameAddr(clientMasterPicModel.getSameAddr() != null && clientMasterPicModel.getSameAddr().equalsIgnoreCase("Y") ? "1" : "0");
            params = buildParamsFromPicModel(clientMasterPicModel);
            responseGlobalModel = syspropInsUpdDelService.syspropInsUpdProcedure(procedureName, params);
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessMasterClient(ClientMasterParameterModel cmp) {
        ResponseGlobalModel<Object> responseGlobalModel;
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_CLIENT_MASTER_GET";
            System.out.println("clientMasterModel===>" + new Gson().toJson(cmp));
            params = buildMasterParamsFromModel(cmp);
            List<String> resultColumns = Arrays.asList(
                    "CLIENTCODE", "CLIENTTYPE", "CLIENTNAME", "BUILDING",
                    "STREET1", "STREET2", "STREET3",
                    "COUNTRYID", "PROVINCEID", "CITYID", "STATEID", "DISTRICTID", "ZIPCODE",
                    "PHONE1", "PHONE2", "FAX1", "FAX2", "EMAIL", "LOBID", "CLIENTGROUP",
                    "CREATEBY", "CREATEDT", "EDITBY", "EDITDT"
            );

            responseGlobalModel = clientMasterDataService.callMasterClientProcedure(procedureName, params, resultColumns);
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    public ResponseGlobalModel<Object> doProcessMasterClientPic(ClientMasterPicParameterModel cmp) {
        ResponseGlobalModel<Object> responseGlobalModel;
        String procedureName;
        List<ProcedureParamModel> params;
        try {
            procedureName = "USP_CLIENT_MASTER_PIC_GET";
            System.out.println("clientMasterModel===>" + new Gson().toJson(cmp));
            params = buildMasterPicParamsFromModel(cmp);
            List<String> resultColumns = Arrays.asList(
                    "CLIENTCODE", "PICTYPE", "PICNAME","PICTITLE","SAMEADDR", "BUILDING",
                    "STREET1", "STREET2", "STREET3",
                    "COUNTRYID", "PROVINCEID", "CITYID", "STATEID", "DISTRICTID", "ZIPCODE",
                    "PHONE1", "PHONE2", "FAX1", "FAX2", "EMAIL", "DOB", "SUBGROUP",
                    "CREATEBY", "CREATEDT", "EDITBY", "EDITDT"
            );

            responseGlobalModel = clientMasterDataService.callMasterClientPicProcedure(procedureName, params, resultColumns);
        } catch (Exception e) {
            responseGlobalModel = new ResponseGlobalModel<>();
            responseGlobalModel.setResultCode(500);
            responseGlobalModel.setMessage("Internal Server Error");
            responseGlobalModel.setError(Collections.singletonMap("exception", e.getMessage()));
        }
        return responseGlobalModel;
    }

    private List<ProcedureParamModel> buildParamsFromModel(ClientMasterModel model) {
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

    private List<ProcedureParamModel> buildParamsFromPicModel(ClientMasterPicModel model) {
        List<ProcedureParamModel> params = new ArrayList<>();

//        String sameAddrValue = model.getSameAddr() != null && model.getSameAddr().equalsIgnoreCase("Y") ? "1" : "0";
//        params.add(new ProcedureParamModel("p_same_addr", sameAddrValue, String.class, ParameterMode.IN));

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

    private List<ProcedureParamModel> buildMasterParamsFromModel(ClientMasterParameterModel model) {
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
    private List<ProcedureParamModel> buildMasterPicParamsFromModel(ClientMasterPicParameterModel model) {
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
    // Utility to convert camelCase to snake_case
    private String toSnakeCase(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
