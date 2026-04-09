package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalApiResponse {
    

    private String provider;

    private String httpStatus;

    private int statusCode;

    private String body;

    private String headers;

    private LocalDateTime timestamp;

    private boolean success;

    private String errorMessage;

    private long responseTimeMs;
}
