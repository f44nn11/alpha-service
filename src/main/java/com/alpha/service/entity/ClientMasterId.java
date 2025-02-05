package com.alpha.service.entity;

import java.io.Serializable;
import java.util.Objects;

public class ClientMasterId implements Serializable {
    private String clientCode;
    // Constructor
    public ClientMasterId() {
    }

    public ClientMasterId(String clientCode) {
        this.clientCode = clientCode;
    }

    // Getters and Setters
    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    // Override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientMasterId that = (ClientMasterId) o;
        return Objects.equals(clientCode, that.clientCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientCode);
    }
}
