package com.alpha.service.model;

import lombok.Data;

/*
 * Created by: fkusu
 * Date: 12/1/2024
 */

@Data
public class ClientMasterModel {
    //urutan harus sama dengan parameter SP untuk insert,update
    private String clientCode;
    private String clientType;
    private String clientGroup;
    private String clientName;
    private String building;
    private String street1;
    private String street2;
    private String street3;
    private String countryId;
    private String provinceId;
    private String cityId;
    private String stateId;
    private String districtId;
    private String zipCode;
    private String phone1;
    private String phone2;
    private String fax1;
    private String fax2;
    private String email;
    private String lobId;
    private String createBy;
    private String actionType;
}