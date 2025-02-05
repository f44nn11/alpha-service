package com.alpha.service.model;

import jakarta.persistence.ParameterMode;
import lombok.Data;

@Data
public class ProcedureParamModel {

    private String name;
    private Object value;
    private Class<?> type;
    private ParameterMode mode;


    public ProcedureParamModel(String name, Object value, Class<?> type, ParameterMode mode) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.mode = mode;
    }
}
