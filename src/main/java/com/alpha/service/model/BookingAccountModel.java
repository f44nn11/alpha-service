package com.alpha.service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 12/15/2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingAccountModel {
    private String bookCd;
    private SysPropString clientCode;
    private String clientName;
    private SysPropString mktId;
    private String fullName;
    private String bookDate;
    private SysProp prevIns;
    private String insName;
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
    private List<DocType> docTypes;
    private List<InsPlacing> insPlacing;
    private String createdBy;
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
    public static class SysPropString {
        private String code;
        private String descp;
    }

    @Data
    public static class SysProp {
        private int code;
        private String descp;
    }
}
