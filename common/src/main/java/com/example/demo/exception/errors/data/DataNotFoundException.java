package com.example.demo.exception.errors.data;


import com.example.demo.exception.errors.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class DataNotFoundException extends ApplicationException {
    private final String traceId;

    public DataNotFoundException(String message, String traceId) {
        super(message, "DATA_NOT_FOUND", HttpStatus.NOT_FOUND, null);
        this.traceId = traceId;
    }

    public DataNotFoundException(String message, String traceId, String details) {
        super(message, "DATA_NOT_FOUND", HttpStatus.NOT_FOUND, details);
        this.traceId = traceId;
    }
}
