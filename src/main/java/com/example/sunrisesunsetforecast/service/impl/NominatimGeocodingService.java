package com.example.sunrisesunsetforecast.service.impl;

import com.example.sunrisesunsetforecast.exception.ExternalServiceException;
import com.example.sunrisesunsetforecast.model.Coordinates;
import com.example.sunrisesunsetforecast.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NominatimGeocodingService implements GeocodingService {

    @Value("${geocoding.api.base-url:https://nominatim.openstreetmap.org}")
    private String baseUrl;

    @Value("${geocoding.user-agent:SunriseSunsetForecast/1.0}")
    private String userAgent;

    private final WebClient webClient;

    @Override
    @Cacheable(value = "cityCoordinates", key = "#city.toLowerCase()")
    public Coordinates getCoordinates(String city) {
        log.info("Looking up coordinates for city: {}", city);
        
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("nominatim.openstreetmap.org")
                            .path("/search")
                            .queryParam("q", city)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToMono(List.class)
                    .flatMap(response -> {
                        if (response == null || response.isEmpty()) {
                            return Mono.error(new ExternalServiceException("No coordinates found for city: " + city));
                        }
                        
                        try {
                            Map<String, Object> location = (Map<String, Object>) response.get(0);
                            double lat = Double.parseDouble(location.get("lat").toString());
                            double lon = Double.parseDouble(location.get("lon").toString());
                            return Mono.just(new Coordinates(lat, lon));
                        } catch (Exception e) {
                            log.error("Error parsing geocoding response for city: " + city, e);
                            return Mono.error(new ExternalServiceException("Error parsing geocoding response"));
                        }
                    })
                    .onErrorResume(e -> {
                        log.error("Error in geocoding service for city: " + city, e);
                        return Mono.error(new ExternalServiceException("Error getting coordinates: " + e.getMessage()));
                    })
                    .block();
                    
        } catch (Exception e) {
            log.error("Unexpected error in getCoordinates for city: " + city, e);
            throw new ExternalServiceException("Failed to get coordinates: " + e.getMessage(), e);
        }
    }
}
