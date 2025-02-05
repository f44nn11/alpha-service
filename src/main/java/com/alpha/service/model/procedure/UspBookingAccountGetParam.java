package com.alpha.service.model.procedure;


import lombok.Data;

/*
 * Created by: fkusu
 * Date: 12/18/2024
 */
@Data
public class UspBookingAccountGetParam {
    private String bookCd;
    private String clientCode;
    private String mktId;
    private String clientName;
    private String p_param1;
    private String p_param2;
    private String p_param3;
    private String actionType;
}
