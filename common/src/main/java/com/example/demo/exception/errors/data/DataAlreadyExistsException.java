package com.example.demo.exception.errors.data;


import com.example.demo.exception.errors.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class DataAlreadyExistsException extends ApplicationException {
    private final String traceId;

    public DataAlreadyExistsException(String message, String traceId) {
        super(message, "DATA_ALREADY_EXISTS", HttpStatus.CONFLICT, null);
        this.traceId = traceId;
    }

    public DataAlreadyExistsException(String message, String traceId, String details) {
        super(message, "DATA_ALREADY_EXISTS", HttpStatus.CONFLICT, details);
        this.traceId = traceId;
    }
}
