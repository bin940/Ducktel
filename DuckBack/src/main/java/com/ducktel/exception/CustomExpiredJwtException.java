package com.ducktel.exception;

public class CustomExpiredJwtException extends RuntimeException {
    public CustomExpiredJwtException(String message) {
        super(message);
    }

    public CustomExpiredJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}

