package com.ducktel.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ducktel.dto.ResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDTO<?>> customException(CustomException e) {
        return ResponseEntity.ok(new ResponseDTO<>(e.getStatusCode(), e.getErrorCode(), e.getMessage(), null));
    }

    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<ResponseDTO<?>> customException(CustomJwtException e) {
        return ResponseEntity.ok(new ResponseDTO<>(e.getStatusCode(), e.getErrorCode(), e.getMessage(), null));
    }

    @ExceptionHandler(CustomExpiredJwtException.class)
    public ResponseEntity<ResponseDTO<?>> customException(CustomExpiredJwtException e) {
        return ResponseEntity.ok(new ResponseDTO<>(e.getStatusCode(), e.getErrorCode(), e.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<?>> handleGenericException(Exception e) {
        return ResponseEntity.ok(new ResponseDTO<>(500, "INTERNAL_SERVER_ERROR", e.getMessage(), null));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<?>> handleValidationExceptions(MethodArgumentNotValidException e) {
        String firstErrorMessage = e.getBindingResult().getFieldErrors()
                .get(0).getDefaultMessage();

        return ResponseEntity.ok(new ResponseDTO<>(400, "VALIDATION_FAILED", firstErrorMessage, null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.ok(new ResponseDTO<>(400, "INVALID_REQUEST", e.getMessage(), null));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity.ok(new ResponseDTO<>(400,"MISSING_PARAMETER", e.getParameterName() + " is required", null));
    }




}
