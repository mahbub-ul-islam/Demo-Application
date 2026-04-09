package com.example.demo.client;

import com.example.demo.dto.ExternalApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Service
@Slf4j
public class JsonPlaceholderApiClient implements ExternalApiClient {

    private final WebClient webClient;
    private final String baseUrl;

    public JsonPlaceholderApiClient(WebClient.Builder webClientBuilder,
                                    @Value("${external.api.jsonplaceholder.base-url:https://jsonplaceholder.typicode.com}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        this.baseUrl = baseUrl;
    }

    @Override
    @CircuitBreaker(name = "jsonplaceholderApiV2", fallbackMethod = "fallbackResponse")
    @Retry(name = "jsonplaceholderApiV2")
    @Bulkhead(name = "jsonplaceholderApiV2", type = Bulkhead.Type.SEMAPHORE)
    public Mono<ExternalApiResponse> get(String endpoint) {
        long startTime = System.currentTimeMillis();

        log.info("Making GET request to JSONPlaceholder API: {}", endpoint);

        return webClient.get()
                .uri(endpoint)
                .retrieve()
                .toEntity(String.class)
                .map(response -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    log.info("JSONPlaceholder API response received in {}ms", responseTime);

                    return ExternalApiResponse.builder()
                            .provider("JSONPlaceholder")
                            .statusCode(response.getStatusCode().value())
                            .body(response.getBody())
                            .headers(response.getHeaders().toString())
                            .timestamp(LocalDateTime.now())
                            .success(response.getStatusCode().is2xxSuccessful())
                            .responseTimeMs(responseTime)
                            .build();
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    log.error("JSONPlaceholder API error: {} - {}", ex.getStatusCode(), ex.getMessage());

                    return Mono.just(ExternalApiResponse.builder()
                            .provider("JSONPlaceholder")
                            .statusCode(ex.getStatusCode().value())
                            .body(ex.getResponseBodyAsString())
                            .timestamp(LocalDateTime.now())
                            .success(false)
                            .errorMessage(ex.getMessage())
                            .responseTimeMs(responseTime)
                            .build());
                })
                .onErrorResume(Exception.class, ex -> {
                    long responseTime = System.currentTimeMillis() - startTime;
                    log.error("Unexpected error in JSONPlaceholder API call", ex);

                    return Mono.just(ExternalApiResponse.builder()
                            .provider("JSONPlaceholder")
                            .statusCode(500)
                            .timestamp(LocalDateTime.now())
                            .success(false)
                            .errorMessage("Unexpected error: " + ex.getMessage())
                            .responseTimeMs(responseTime)
                            .build());
                });
    }

    @Override
    public String getClientName() {
        return "JsonPlaceholderApiClient";
    }

    public Mono<ExternalApiResponse> fallbackResponse(String endpoint, Exception ex) {
        log.warn("Circuit breaker activated for JSONPlaceholder API, returning fallback response");

        return Mono.just(ExternalApiResponse.builder()
                .provider("JSONPlaceholder")
                .statusCode(503)
                .timestamp(LocalDateTime.now())
                .success(false)
                .errorMessage("Service temporarily unavailable - circuit breaker activated")
                .responseTimeMs(0)
                .build());
    }
}
