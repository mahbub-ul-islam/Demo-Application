package com.example.demo.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;


@Configuration
@Slf4j
public class ResilienceConfig {
    

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024));
    }
    

    @Bean
    public CircuitBreaker jsonplaceholderApiV2CircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(65)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(15)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .minimumNumberOfCalls(4)
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordException(ex -> !(ex instanceof IllegalArgumentException))
                .build();
        
        CircuitBreaker circuitBreaker = CircuitBreaker.of("jsonplaceholderApiV2", config);
        
        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                    log.info("JSONPlaceholder V2 CircuitBreaker state transition: {} -> {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onError(event ->
                    log.error("JSONPlaceholder V2 CircuitBreaker error: {}", event.getThrowable().getMessage()))
                .onSuccess(event ->
                    log.debug("JSONPlaceholder V2 CircuitBreaker success"));
        
        return circuitBreaker;
    }
    

    @Bean
    public Retry jsonplaceholderApiV2Retry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofSeconds(3))
                .retryExceptions(java.net.SocketTimeoutException.class, java.io.IOException.class)
                .ignoreExceptions(IllegalArgumentException.class, org.springframework.web.client.HttpClientErrorException.class)
                .build();

        Retry retry = Retry.of("jsonplaceholderApiV2", config);

        retry.getEventPublisher()
                .onRetry(event ->
                    log.warn("JSONPlaceholder V2 Retry attempt {}", event.getNumberOfRetryAttempts()))
                .onSuccess(event ->
                    log.info("JSONPlaceholder V2 Retry succeeded"))
                .onError(event ->
                    log.error("JSONPlaceholder V2 Retry failed after {} attempts",
                        event.getNumberOfRetryAttempts()));

        return retry;
    }
    

    @Bean
    public io.github.resilience4j.bulkhead.Bulkhead jsonplaceholderApiV2Bulkhead() {
        io.github.resilience4j.bulkhead.BulkheadConfig config = io.github.resilience4j.bulkhead.BulkheadConfig.custom()
                .maxConcurrentCalls(8)
                .maxWaitDuration(Duration.ofMillis(500))
                .build();

        io.github.resilience4j.bulkhead.Bulkhead bulkhead = io.github.resilience4j.bulkhead.Bulkhead.of("jsonplaceholderApiV2", config);

        bulkhead.getEventPublisher()
                .onCallPermitted(event ->
                    log.debug("JSONPlaceholder V2 Bulkhead call permitted"))
                .onCallRejected(event ->
                    log.warn("JSONPlaceholder V2 Bulkhead call rejected"))
                .onCallFinished(event ->
                    log.debug("JSONPlaceholder V2 Bulkhead call finished"));

        return bulkhead;
    }
}
