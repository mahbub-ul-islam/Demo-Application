package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@Data
@Builder
public class ErrorResponseDto {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private int statusCode;
    private String message;
    private String path;
    private String traceId;
    private String errorCode;
    private String details;
}
