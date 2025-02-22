package com.alpha.service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@IdClass(ClientMasterId.class)
@Table(name = "BK_CLENT_MASTER")
public class ClientMaster {
    @Id
    @Column(name = "CLIENTCODE", length = 10, nullable = false)
    private String clientCode;

    @Column(name = "CLIENTTYPE", length = 2)
    private String clientType;

    @Column(name = "CLIENTNAME", length = 255)
    private String clientName;

    @Column(name = "BUILDING", length = 50)
    private String building;

    @Column(name = "STREET1", length = 50)
    private String street1;

    @Column(name = "STREET2", length = 50)
    private String street2;

    @Column(name = "STREET3", length = 50)
    private String street3;

    @Column(name = "COUNTRYID", length = 3)
    private String countryId;

    @Column(name = "PROVINCEID", length = 3)
    private String provinceId;

    @Column(name = "CITYID", length = 3)
    private String cityId;

    @Column(name = "STATEID", length = 3)
    private String stateId;

    @Column(name = "DISTRICTID", length = 3)
    private String districtId;

    @Column(name = "ZIPCODE", length = 10)
    private String zipCode;

    @Column(name = "PHONE1", length = 15)
    private String phone1;

    @Column(name = "PHONE2", length = 15)
    private String phone2;

    @Column(name = "FAX1", length = 15)
    private String fax1;

    @Column(name = "FAX2", length = 15)
    private String fax2;

    @Column(name = "EMAIL", length = 50)
    private String email;

    @Column(name = "LOBID", length = 3)
    private String lobId;

    @Column(name = "CLIENTGROUP", length = 10)
    private String clientGroup;

    @Column(name = "CREATEBY", length = 15)
    private String createBy;

    @Column(name = "CREATEDT")
    private LocalDateTime createDt;

    @Column(name = "EDITBY", length = 15)
    private String editBy;

    @Column(name = "EDITDT")
    private LocalDateTime editDt;

    
    public LocalDateTime getEditDt() {
        return editDt;
    }

    public void setEditDt(LocalDateTime editDt) {
        this.editDt = editDt;
    }

    public String getEditBy() {
        return editBy;
    }

    public void setEditBy(String editBy) {
        this.editBy = editBy;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax2() {
        return fax2;
    }

    public void setFax2(String fax2) {
        this.fax2 = fax2;
    }

    public String getFax1() {
        return fax1;
    }

    public void setFax1(String fax1) {
        this.fax1 = fax1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getStreet3() {
        return street3;
    }

    public void setStreet3(String street3) {
        this.street3 = street3;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }
}