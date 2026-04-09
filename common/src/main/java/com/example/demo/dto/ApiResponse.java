package com.example.demo.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;


@Builder
public record ApiResponse<T>(
    LocalDateTime timestamp,
    HttpStatus status,
    int statusCode,
    String message,
    String path,
    String traceId,
    T data
) {

    public static <T> ApiResponse<T> of(T data, String message, String path, String traceId, HttpStatus status) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .statusCode(status.value())
                .message(message)
                .path(path)
                .traceId(traceId)
                .data(data)
                .build();
    }
}
