package com.alpha.booking_account.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int resultCode;
    private final Object data;

    public CustomException(int resultCode, Object data) {
        super();
        this.resultCode = resultCode;
        this.data = data;
    }

    public CustomException(String message, int resultCode, Object data) {
        super(message);
        this.resultCode = resultCode;
        this.data = data;
    }

    public CustomException(String message, Throwable cause, int resultCode, Object data) {
        super(message, cause);
        this.resultCode = resultCode;
        this.data = data;
    }

    public CustomException(Throwable cause, int resultCode, Object data) {
        super(cause);
        this.resultCode = resultCode;
        this.data = data;
    }

}
