package com.example.demo.exception.errors.application;

import com.example.demo.exception.errors.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class InternalServerErrorException extends ApplicationException {
    private final String traceId;

    public InternalServerErrorException(String message, String traceId) {
        super(message, "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, null);
        this.traceId = traceId;
    }

    public InternalServerErrorException(String message, String traceId, String details) {
        super(message, "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, details);
        this.traceId = traceId;
    }
}
