package com.example.demo.exception.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by  : Mahbub
 * Date        : 11/24/2025
 * Project     : library
 * File        : ApplicationException
 */

@Getter
public abstract class ApplicationException extends RuntimeException{

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String details;


    public ApplicationException(String message, String errorCode) {
        this(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    public ApplicationException(String message, String errorCode, HttpStatus httpStatus) {
        this(message, errorCode, httpStatus, null);
    }

    public ApplicationException(String message, String errorCode, HttpStatus httpStatus, String details) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = details;
    }

}
