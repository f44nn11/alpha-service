package com.alpha.booking_account.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
    private String clientCode;
    private String clientName;
    private String mktId;
    private String fullName;
    private String bookDate;
    private String prevIns;
    private String insName;
    private String expDate;
    private String premiumBudget;
    private String ip;
    private String op;
    private String dt;
    private String mt;
    private String gl;
    private String description;
    private String status;
    private List<DocType> docTypes;
    private String createdBy;
    private String actionType;

    @Data
    public static class DocType {
        private String code;
        private String revDoc;
        private String descp;
        private String urlPath;
    }
}
