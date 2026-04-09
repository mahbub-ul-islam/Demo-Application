package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ExternalApiResponse;
import com.example.demo.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/external")
@RequiredArgsConstructor
@Slf4j
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    @GetMapping("/call")
    public Mono<ResponseEntity<ApiResponse<ExternalApiResponse>>> callExternalApi(
            @RequestParam(defaultValue = "/") String endpoint) {

        String traceId = getTraceId();
        log.info("External API call request for endpoint: {}", endpoint);

        return externalApiService.get(endpoint)
                .map(response -> ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponse.of(
                                response,
                                "External API call completed successfully",
                                "/api/v1/external/call?endpoint=" + endpoint,
                                traceId,
                                HttpStatus.OK)))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.<ExternalApiResponse>of(
                                null,
                                "External API call failed",
                                "/api/v1/external/call",
                                traceId,
                                HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return (traceId != null) ? traceId : UUID.randomUUID().toString();
    }
}
