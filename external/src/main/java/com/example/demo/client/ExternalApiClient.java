package com.example.demo.client;

import com.example.demo.dto.ExternalApiResponse;
import reactor.core.publisher.Mono;


public interface ExternalApiClient {


    Mono<ExternalApiResponse> get(String endpoint);

    String getClientName();
}
