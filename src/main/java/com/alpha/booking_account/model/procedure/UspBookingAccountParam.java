package com.alpha.booking_account.model.procedure;


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
    private String expDate;
    private String premiumBudget;
    private String ip;
    private String op;
    private String dt;
    private String mt;
    private String gl;
    private String description;
    private String status;
    private String createdBy;
    private String actionType;
}
