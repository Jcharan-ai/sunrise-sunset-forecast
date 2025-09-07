package com.example.sunrisesunsetforecast.config;

import com.example.sunrisesunsetforecast.client.dto.OpenMeteoResponse;
import com.example.sunrisesunsetforecast.model.Coordinates;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@TestConfiguration
public class TestWebClientConfig {

    @Bean
    public WebClient webClient() {
        // Mock WebClient that returns test responses
        return WebClient.builder()
                .exchangeFunction(clientRequest -> {
                    if (clientRequest.url().toString().contains("nominatim.openstreetmap.org")) {
                        // Mock response for geocoding service
                        return Mono.just(
                                org.springframework.web.reactive.function.client.ClientResponse
                                        .create(HttpStatus.OK)
                                        .header("Content-Type", "application/json")
                                        .body("[{\"lat\":51.5074,\"lon\":-0.1278,\"display_name\":\"London, UK\"}]")
                                        .build()
                                        .toEntity(String.class)
                                        .map(entity -> org.springframework.web.reactive.function.client.ClientResponse
                                                .create(HttpStatus.OK)
                                                .headers(headers -> headers.addAll(entity.getHeaders()))
                                                .body(entity.getBody())
                                                .build()
                                        )
                        ).flatMap(response -> response);
                    } else if (clientRequest.url().toString().contains("open-meteo.com")) {
                        // Mock response for Open-Meteo API
                        String mockResponse = "{\"latitude\":51.5074,\"longitude\":-0.1278,\"daily\":{\"time\":[\"2023-10-01\"],\"sunrise\":[\"2023-10-01T06:45:00\"],\"sunset\":[\"2023-10-01T18:30:00\"],\"temperature_2m_max\":[20.5],\"weathercode\":[1]}}";
                        return Mono.just(
                                org.springframework.web.reactive.function.client.ClientResponse
                                        .create(HttpStatus.OK)
                                        .header("Content-Type", "application/json")
                                        .body(mockResponse)
                                        .build()
                                        .toEntity(String.class)
                                        .map(entity -> org.springframework.web.reactive.function.client.ClientResponse
                                                .create(HttpStatus.OK)
                                                .headers(headers -> headers.addAll(entity.getHeaders()))
                                                .body(entity.getBody())
                                                .build()
                                        )
                        ).flatMap(response -> response);
                    }
                    return Mono.error(new WebClientResponseException(404, "Not Found", null, null, null));
                })
                .build();
    }
}
