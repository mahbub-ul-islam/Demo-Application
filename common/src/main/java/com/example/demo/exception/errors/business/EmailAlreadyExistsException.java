package com.example.demo.exception.errors.business;

import com.example.demo.exception.errors.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class EmailAlreadyExistsException extends ApplicationException {
    private final String traceId;

    public EmailAlreadyExistsException(String message, String traceId) {
        super(message, "EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT, null);
        this.traceId = traceId;
    }

    public EmailAlreadyExistsException(String message, String traceId, String details) {
        super(message, "EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT, details);
        this.traceId = traceId;
    }
}
