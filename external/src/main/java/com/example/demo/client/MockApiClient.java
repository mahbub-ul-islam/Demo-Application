package com.example.demo.client;

import com.example.demo.dto.ExternalApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class MockApiClient implements ExternalApiClient {

    private final Map<String, String> mockResponses;

    public MockApiClient() {
        this.mockResponses = new HashMap<>();
        initializeMockResponses();
    }

    private void initializeMockResponses() {
        mockResponses.put("/", "<html><head><title>Mock Google</title></head><body><h1>Welcome to Mock Google</h1></body></html>");
        mockResponses.put("/search", "<html><head><title>Mock Search Results</title></head><body><div>Mock search results for testing</div></body></html>");
        mockResponses.put("/api", "{\"message\": \"Mock API response\", \"status\": \"success\", \"timestamp\": \"" + LocalDateTime.now() + "\"}");
    }

    @Override
    public Mono<ExternalApiResponse> get(String endpoint) {
        log.info("Mock API GET request to: {}", endpoint);

        return Mono.fromCallable(() -> {
            Thread.sleep(100);

            String responseBody = mockResponses.getOrDefault(endpoint, "Mock response for: " + endpoint);

            return ExternalApiResponse.builder()
                    .provider("Mock")
                    .statusCode(200)
                    .body(responseBody)
                    .headers("Content-Type: text/html; charset=UTF-8")
                    .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                    .success(true)
                    .responseTimeMs(100)
                    .build();
        });
    }

    @Override
    public String getClientName() {
        return "MockApiClient";
    }
}
