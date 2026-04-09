package com.example.demo.service;


import com.example.demo.dto.ExternalApiResponse;
import com.example.demo.factory.ExternalApiFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiService {

    private final ExternalApiFactory externalApiFactory;


    public Mono<ExternalApiResponse> get(String endpoint) {
        log.info("Making external API GET request to: {}", endpoint);

        return Mono.fromCallable(() -> externalApiFactory.getClient())
                .flatMap(client -> client.get(endpoint))
                .doOnSuccess(response -> log.info("External API GET request completed successfully"))
                .doOnError(error -> log.error("External API GET request failed", error));
    }
}
