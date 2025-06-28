package com.alpha.service.model;


import lombok.Data;

/*
 * Created by: fkusu
 * Date: 6/29/2025
 */
@Data
public class BookingReviewModel {
    private String bookCd;
    private String reviewDate;
    private String subject;
    private String subjectDtl;
    private String result;
    private String createdBy;
    private String actionType;
}
