package com.alpha.service.helper;

import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.model.SystemPropertiesModel;
import com.alpha.service.model.response.ResponseGlobalModel;
import com.alpha.service.service.SyspropGeodataService;
import jakarta.persistence.ParameterMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GeodataHelper {
    @Autowired
    private SyspropGeodataService syspropGeodataService;

    public ResponseGlobalModel<Object> getGeodata(SystemPropertiesModel systemPropertiesModel) {
        ResponseGlobalModel<Object> responseGlobalModel;
        String procedureName;
        List<ProcedureParamModel> params;
        List<String> resultColumns;
        try{
            switch (systemPropertiesModel.getActionType().toLowerCase()) {
                case "country":
                    procedureName = "SP_GEODATA_COUNTRY";
                    params = List.of(
                            new ProcedureParamModel("p_countryID", systemPropertiesModel.getCode(), String.class, ParameterMode.IN)
                    );
                    resultColumns = Arrays.asList("code", "descp");
                    break;

                case "province":
                    procedureName = "SP_GEODATA_PROV_COUNTRY";
                    params = List.of(
                            new ProcedureParamModel("p_countryID", systemPropertiesModel.getCode(), String.class, ParameterMode.IN)
                    );
                    resultColumns = Arrays.asList("code", "descp");
                    break;

                case "city":
                    procedureName = "SP_GEODATA_CITY_PROV";
                    params = Arrays.asList(
                            new ProcedureParamModel("p_countryID", systemPropertiesModel.getCode().split("-")[0], String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_provinceID", systemPropertiesModel.getCode().split("-")[1], String.class, ParameterMode.IN)
                    );
                    resultColumns = Arrays.asList("code", "descp");
                    break;
                case "state":
                    procedureName = "SP_GEODATA_STATES_CITY";
                    params = Arrays.asList(
                            new ProcedureParamModel("p_countryID", systemPropertiesModel.getCode().split("-")[0], String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_provinceID", systemPropertiesModel.getCode().split("-")[1], String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_cityID", systemPropertiesModel.getCode().split("-")[2], String.class, ParameterMode.IN)
                    );
                    resultColumns = Arrays.asList("code", "descp");
                    break;

                case "district":
                    procedureName = "SP_GEODATA_DIST_STATE";
                    params = Arrays.asList(
                            new ProcedureParamModel("p_countryID", systemPropertiesModel.getCode().split("-")[0], String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_provinceID", systemPropertiesModel.getCode().split("-")[1], String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_cityID", systemPropertiesModel.getCode().split("-")[2], String.class, ParameterMode.IN),
                            new ProcedureParamModel("p_stateID", systemPropertiesModel.getCode().split("-")[3], String.class, ParameterMode.IN)
                    );
                    resultColumns = Arrays.asList("code", "descp", "zipcode");
                    break;

                default:
                    throw new IllegalArgumentException("Invalid level: " + systemPropertiesModel.getCode());
            }
            responseGlobalModel =  syspropGeodataService.callSyspropGeodataProcedure(procedureName,params,resultColumns);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseGlobalModel;
    }
}
