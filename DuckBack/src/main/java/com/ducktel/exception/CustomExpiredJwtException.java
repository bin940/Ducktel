package com.ducktel.exception;

public class CustomExpiredJwtException extends RuntimeException {
    private final int statusCode;
    private final String errorCode;

    public CustomExpiredJwtException(int statusCode, String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public CustomExpiredJwtException(int statusCode, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

