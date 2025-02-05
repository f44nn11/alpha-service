package com.alpha.service.model.procedure;


import lombok.Data;

/*
 * Created by: fkusu
 * Date: 12/18/2024
 */
@Data
public class UspBookingAccountParam {
    private String bookCd;
    private String clientCode;
    private String mktId;
    private String bookDate;
    private String prevIns;
    private String issueDate;
    private String expDate;
    private String premiumBudget;
    private String totMembers;
    private String ip;
    private String op;
    private String dt;
    private String mt;
    private String gl;
    private String description;
    private String status;
    private String createdBy;
    private String insPlacing;
    private String actionType;

    @Data
    public static class DocType {
        private String code;
        private String revDoc;
        private String descp;
        private String urlPath;
    }

    @Data
    public static class InsPlacing {
        private int code;
        private String descp;
    }
    @Data
    public static class SysProp {
        private int code;
        private String descp;
    }
}
