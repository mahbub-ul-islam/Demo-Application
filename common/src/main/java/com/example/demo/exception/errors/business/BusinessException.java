package com.example.demo.exception.errors.business;


import com.example.demo.exception.errors.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class BusinessException extends ApplicationException {
    private final String traceId;

    public BusinessException(String message, HttpStatus status, String traceId) {
        super(message, "BUSINESS_ERROR", status, null);
        this.traceId = traceId;
    }

    public BusinessException(String message, String errorCode, HttpStatus status, String traceId, String details) {
        super(message, errorCode, status, details);
        this.traceId = traceId;
    }
}
