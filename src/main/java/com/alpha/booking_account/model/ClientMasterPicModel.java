package com.alpha.booking_account.model;

import lombok.Data;

/*
 * Created by: fkusu
 * Date: 12/1/2024
 */
@Data
public class ClientMasterPicModel {
    private String clientCode;
    private String picType;
    private String picName;
    private String picTitle;
    private String sameAddr;
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
    private String dob;
    private String subGroup;
    private String createBy;
    private String actionType;
}
