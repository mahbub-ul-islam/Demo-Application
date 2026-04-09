package com.example.demo.factory;

import com.example.demo.client.ExternalApiClient;
import com.example.demo.client.JsonPlaceholderApiClient;
import com.example.demo.client.MockApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class ExternalApiFactory {

    private final List<ExternalApiClient> clients;
    private final String activeClient;

    public ExternalApiFactory(List<ExternalApiClient> clients,
                             @Value("${external.api.client:mock}") String activeClient) {
        this.clients = clients;
        this.activeClient = activeClient.toLowerCase();
        log.info("External API Factory initialized with {} clients, active: {}",
                clients.size(), this.activeClient);
    }


    public ExternalApiClient getClient() {
        return clients.stream()
                .filter(this::isClientActive)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No active external API client found for: " + activeClient));
    }


    private boolean isClientActive(ExternalApiClient client) {
        String clientName = client.getClientName().toLowerCase();

        switch (activeClient) {
            case "jsonplaceholder":
                return client instanceof JsonPlaceholderApiClient;
            case "mock":
                return client instanceof MockApiClient;
            default:
                log.warn("Unknown client type: {}, defaulting to mock", activeClient);
                return client instanceof MockApiClient;
        }
    }
}
