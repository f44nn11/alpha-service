package com.alpha.service.entity;

import java.io.Serializable;
import java.util.Objects;

public class ClientMasterPicId implements Serializable {
    private String clientCode;
    private String picType;

    // Constructor
    public ClientMasterPicId() {
    }

    public ClientMasterPicId(String clientCode, String picType) {
        this.clientCode = clientCode;
        this.picType = picType;
    }

    // Getters and Setters
    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getPicType() {
        return picType;
    }

    public void setPicType(String picType) {
        this.picType = picType;
    }

    // Override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientMasterPicId that = (ClientMasterPicId) o;
        return Objects.equals(clientCode, that.clientCode) && Objects.equals(picType, that.picType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientCode, picType);
    }
}
