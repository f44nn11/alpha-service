package com.alpha.booking_account.model;

import lombok.Data;

/*
 * Created by: fkusu
 * Date: 12/4/2024
 */

@Data
public class ClientMasterParameterModel {
    private String clientCode;
    private String clientType;
    private String clientGroup;
    private String clientName;
    private String p_param1;
    private String p_param2;
    private String p_param3;
    private String actionType;
}
