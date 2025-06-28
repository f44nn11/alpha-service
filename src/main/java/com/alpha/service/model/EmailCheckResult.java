package com.alpha.service.model;


/*
 * Created by: fkusu
 * Date: 6/3/2025
 */
public class EmailCheckResult {
    private Integer hasEmail;
    private Integer resultCode;
    private String email;
    private String message;

    public EmailCheckResult(Integer hasEmail, Integer resultCode, String email, String message) {
        this.hasEmail = hasEmail;
        this.resultCode = resultCode;
        this.email = email;
        this.message = message;
    }

    public Integer getHasEmail() {
        return hasEmail;
    }

    public void setHasEmail(Integer hasEmail) {
        this.hasEmail = hasEmail;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
