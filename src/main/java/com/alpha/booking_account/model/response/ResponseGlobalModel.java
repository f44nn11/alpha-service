package com.alpha.booking_account.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseGlobalModel<T> {
    private int resultCode;
    private String timestamp;
    private String message;
    private Map<String, String> error;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, String> getError() {
        return error;
    }

    public void setError(Map<String, String> error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public ResponseGlobalModel(int resultCode, String timestamp, String message, Map<String, String> error, T data) {
        this.resultCode = resultCode;
        this.timestamp = timestamp;
        this.message = message;
        this.error = error;
        this.data = data;
    }

    public ResponseGlobalModel() {

    }

    public static class ReponseGlobalModelBuilder<T> {
        private int resultCode;
        private String message;
        private Map<String, String> error;
        private T data;

        public ReponseGlobalModelBuilder<T> resultCode(int resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        public ReponseGlobalModelBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ReponseGlobalModelBuilder<T> error(Map<String, String> error) {
            this.error = error;
            return this;
        }
        public ReponseGlobalModelBuilder<T> error(String key, String errorMessage) {
            this.error = Map.of(key, errorMessage);
            return this;
        }

        public ReponseGlobalModelBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ResponseGlobalModel<T> build() {
            String timestamp = generateTimestamp();
            return new ResponseGlobalModel<>(resultCode, timestamp, message, error, data);
        }
        private String generateTimestamp() {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            return dateTimeFormat.format(new Date());
        }
    }
}