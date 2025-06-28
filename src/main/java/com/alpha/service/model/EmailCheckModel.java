package com.alpha.service.model;


/*
 * Created by: fkusu
 * Date: 6/3/2025
 */
public class EmailCheckModel {
    private String code;
    private String param1;
    private String param2;
    private String param3;
    private String actionType;

    public EmailCheckModel(String code, String param1, String param2, String param3, String actionType) {
        this.code = code;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.actionType = actionType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
