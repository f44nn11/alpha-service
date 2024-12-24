package com.alpha.booking_account.model.response;


public class CustomResponseModel<T> {
    private int result_code;
    private String message;
    private T error;

    public T getError() {
        return error;
    }

    public void setError(T error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

}
