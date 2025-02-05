package com.alpha.service.model.procedure;


import lombok.Data;

/*
 * Created by: fkusu
 * Date: 2/3/2025
 */
@Data
public class UspPlacingAccountDtlParam {
    private String placingCd;
    private int insCd;
    private String docType;
    private String description;
    private String createdBy;
    private String actionType;
}
