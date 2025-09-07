package com.example.sunrisesunsetforecast.client;

import com.example.sunrisesunsetforecast.client.dto.OpenMeteoResponse;
import com.example.sunrisesunsetforecast.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import com.example.sunrisesunsetforecast.service.GeocodingService;
import com.example.sunrisesunsetforecast.model.Coordinates;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenMeteoClient {

    @Value("${openmeteo.api.base-url}")
    private String baseUrl;

    private final WebClient webClient;
    private final GeocodingService geocodingService;

    @Cacheable(value = "openMeteoForecast", key = "#city.toLowerCase()")
    public OpenMeteoResponse getSunForecast(String city) {
        log.info("Fetching coordinates for city: {}", city);
        
        try {
            // First, get coordinates for the city
            Coordinates coordinates = geocodingService.getCoordinates(city);
            if (coordinates == null) {
                throw new ExternalServiceException("Could not find coordinates for city: " + city);
            }

            // Build the URI with query parameters
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("latitude", coordinates.getLatitude())
                    .queryParam("longitude", coordinates.getLongitude())
                    .queryParam("daily", "sunrise,sunset,temperature_2m_max,weathercode")
                    .queryParam("timezone", "auto")
                    .queryParam("forecast_days", 1) // We only need tomorrow's forecast
                    .queryParam("temperature_unit", "celsius")
                    .queryParam("windspeed_unit", "kmh")
                    .queryParam("precipitation_unit", "mm")
                    .build()
                    .toUri();

            log.debug("Calling Open-Meteo API: {}", uri);
            
            // Make the API call
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(OpenMeteoResponse.class)
                    .onErrorResume(e -> {
                        log.error("Error calling Open-Meteo API for city: " + city, e);
                        return Mono.error(new ExternalServiceException("Error fetching weather data: " + e.getMessage()));
                    })
                    .block();
                    
        } catch (Exception e) {
            log.error("Error in getSunForecast for city: " + city, e);
            throw new ExternalServiceException("Error processing weather data: " + e.getMessage(), e);
        }
    }
}
