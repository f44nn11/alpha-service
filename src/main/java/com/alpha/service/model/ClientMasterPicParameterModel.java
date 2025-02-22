package com.alpha.service.model;

import lombok.Data;

/*
 * Created by: fkusu
 * Date: 12/4/2024
 */

@Data
public class ClientMasterPicParameterModel {
    private String clientCode;
    private String picType;
    private String picName;
    private String p_param1;
    private String p_param2;
    private String p_param3;
    private String actionType;
}
