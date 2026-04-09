package com.example.demo.exception.handler;

import com.example.demo.dto.ErrorResponseDto;
import com.example.demo.exception.errors.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;



@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return (traceId != null) ? traceId : UUID.randomUUID().toString();
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponseDto> handleApplicationException(ApplicationException ex) {
        String traceId = getTraceId();
        log.error("[APPLICATION_ERROR] TraceId: {}, Message: {}", traceId, ex.getMessage(), ex);

        ErrorResponseDto response = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus())
                .statusCode(ex.getHttpStatus().value())
                .message(ex.getMessage())
                .path("/api/error")
                .traceId(traceId)
                .errorCode(ex.getErrorCode())
                .details(ex.getDetails() != null ? ex.getDetails() : "Error code: " + ex.getErrorCode() + ". Please contact support if this issue persists.")
                .build();

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        String traceId = getTraceId();
        log.error("[SYSTEM_ERROR] TraceId: {}, Message: {}", traceId, ex.getMessage(), ex);

        ErrorResponseDto response = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred. Please contact support.")
                .path("/api/error")
                .traceId(traceId)
                .errorCode("INTERNAL_SERVER_ERROR")
                .details("An unexpected error occurred. Please try again later.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
