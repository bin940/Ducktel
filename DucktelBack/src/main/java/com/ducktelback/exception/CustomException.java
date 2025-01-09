package com.ducktelback.exception;

public class CustomException extends RuntimeException {

    private final String errorCode;
    private final String message;

    public CustomException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
