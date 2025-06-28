package com.alpha.service.model.procedure;


import lombok.Data;

/*
 * Created by: fkusu
 * Date: 2/3/2025
 */
@Data
public class UspPlacingAccountParam {
    private String placingCd;
    private String bookCd;
    private String description;
    private String placingDate;
    private String insPlacing;
    private String createdBy;
    private String actionType;  //1 = Insert, 2 = Update, 3 = Delete
}
