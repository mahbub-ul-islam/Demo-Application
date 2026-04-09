package com.example.demo.exception.errors.business;

import com.example.demo.exception.errors.ApplicationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class DepartmentCodeAlreadyExistsException extends ApplicationException {
    private final String traceId;

    public DepartmentCodeAlreadyExistsException(String message, String traceId) {
        super(message, "DEPARTMENT_CODE_ALREADY_EXISTS", HttpStatus.CONFLICT, null);
        this.traceId = traceId;
    }

    public DepartmentCodeAlreadyExistsException(String message, String traceId, String details) {
        super(message, "DEPARTMENT_CODE_ALREADY_EXISTS", HttpStatus.CONFLICT, details);
        this.traceId = traceId;
    }
}
