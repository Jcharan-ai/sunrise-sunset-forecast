package com.example.sunrisesunsetforecast.service.impl;

import com.example.sunrisesunsetforecast.client.OpenMeteoClient;
import com.example.sunrisesunsetforecast.client.dto.OpenMeteoResponse;
import com.example.sunrisesunsetforecast.dto.SunForecastResponse;
import com.example.sunrisesunsetforecast.exception.ExternalServiceException;
import com.example.sunrisesunsetforecast.model.Coordinates;
import com.example.sunrisesunsetforecast.service.ForecastDescriptionService;
import com.example.sunrisesunsetforecast.service.GeocodingService;
import com.example.sunrisesunsetforecast.service.SunForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class SunForecastServiceImpl implements SunForecastService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private final OpenMeteoClient openMeteoClient;
    private final ForecastDescriptionService forecastDescriptionService;
    private final org.springframework.cache.CacheManager cacheManager;
    private final GeocodingService geocodingService;

    @Override
    @Cacheable(value = "sunForecast", key = "#city.toLowerCase()")
    public SunForecastResponse getSunForecast(String city) {
        log.info("Fetching sun forecast for city: {}", city);
        
        try {
            log.info("1. About to call OpenMeteoClient.getSunForecast for city: {}", city);
            
            // Debug: Log the openMeteoClient object
            log.info("1.1. OpenMeteoClient class: {}", openMeteoClient != null ? openMeteoClient.getClass().getName() : "NULL");
            
            // Get coordinates for the city
            Coordinates coords = geocodingService.getCoordinates(city);
            log.info("1.2. Got coordinates for {}: {},{}", city, 
                    coords != null ? coords.getLatitude() : "null", 
                    coords != null ? coords.getLongitude() : "null");
            
            com.example.sunrisesunsetforecast.client.dto.OpenMeteoResponse forecast = openMeteoClient.getSunForecast(city);
            
            log.info("2. Received forecast object: {}", forecast != null ? "not null" : "null");
            
            if (forecast == null) {
                log.error("3. Forecast is null - no data returned from client for city: {}", city);
                log.error("3.1. OpenMeteoClient instance: {}", openMeteoClient);
                log.error("3.2. Geocoding service returned: {}", coords);
                throw new ExternalServiceException("No forecast data available for the specified location");
            }
            
            log.info("4. Forecast class: {}", forecast.getClass().getName());
            log.info("5. Forecast toString(): {}", forecast);
            log.info("6. Forecast latitude: {}, longitude: {}", forecast.getLatitude(), forecast.getLongitude());
            
            OpenMeteoResponse.Daily daily = forecast.getDaily();
            log.info("7. Daily object: {}", daily != null ? "not null" : "null");
            
            if (daily == null) {
                log.error("8. Daily forecast is null - check mock response in test");
                log.debug("9. Forecast object fields: {}", Arrays.toString(forecast.getClass().getDeclaredFields()));
                throw new ExternalServiceException("No daily forecast data available");
            }
            
            // Log all fields of the Daily object
            log.info("10. Daily object class: {}", daily.getClass().getName());
            log.info("11. Daily object fields: {}", Arrays.toString(daily.getClass().getDeclaredFields()));
            
            // Log all available data from the Daily object
            try {
                log.info("12. Time list: {}", daily.getTime());
                log.info("13. Sunrise times: {}", daily.getSunriseTimes());
                log.info("14. Sunset times: {}", daily.getSunsetTimes());
                log.info("15. Temperature max: {}", daily.getTemperature2mMax());
                log.info("16. Weather codes: {}", daily.getWeatherCode());
            } catch (Exception e) {
                log.error("17. Error accessing Daily object fields: {}", e.getMessage(), e);
            }
            
            if (daily.getSunriseTimes() == null) {
                log.error("18. Sunrise times are null - check mock response in test");
                throw new ExternalServiceException("No sunrise times available");
            }
            
            if (daily.getSunriseTimes().isEmpty()) {
                log.error("19. Sunrise times list is empty - check mock response in test");
                log.debug("20. Daily object state: {}", daily);
                throw new ExternalServiceException("No sunrise times available");
            }
            
            // Get the first day's forecast (index 0 for tomorrow)
            String sunriseTime = forecast.getDaily().getSunriseTimes().get(0);
            String sunsetTime = forecast.getDaily().getSunsetTimes().get(0);
            
            // Parse the timestamps, handling both with and without timezone
            LocalDateTime sunriseLocal = parseDateTime(sunriseTime);
            LocalDateTime sunsetLocal = parseDateTime(sunsetTime);
            
            // Format times for the AI enhancement
            String formattedSunrise = sunriseLocal.format(TIME_FORMAT);
            String formattedSunset = sunsetLocal.format(TIME_FORMAT);
            
            // Get temperature and weather condition (if available)
            Double temperature = forecast.getDaily() != null && forecast.getDaily().getTemperature2mMax() != null && 
                              !forecast.getDaily().getTemperature2mMax().isEmpty() ?
                              forecast.getDaily().getTemperature2mMax().get(0) : null;
            
            String weatherCondition = "Clear"; // Default value if not available
            if (forecast.getDaily() != null && forecast.getDaily().getWeatherCode() != null && 
                !forecast.getDaily().getWeatherCode().isEmpty()) {
                weatherCondition = mapWeatherCode(forecast.getDaily().getWeatherCode().get(0));
            }
            
            // Convert to OffsetDateTime for the response
            OffsetDateTime sunrise = sunriseLocal.atZone(ZoneId.systemDefault()).toOffsetDateTime();
            OffsetDateTime sunset = sunsetLocal.atZone(ZoneId.systemDefault()).toOffsetDateTime();
            
            // Generate enhanced message using AI
            String enhancedMessage = forecastDescriptionService.generateForecastDescription(
                city,
                LocalDate.now().plusDays(1), // Tomorrow's date
                LocalTime.parse(formattedSunrise),
                LocalTime.parse(formattedSunset),
                temperature != null ? temperature : 20.0, // Default to 20°C if not available
                weatherCondition
            );
            
            return SunForecastResponse.builder()
                .city(city)
                .sunrise(sunrise)
                .sunset(sunset)
                .enhancedMessage(enhancedMessage)
                .temperature(temperature)
                .weatherCondition(weatherCondition)
                .build();
                
        } catch (Exception e) {
            log.error("Error fetching sun forecast for city: " + city, e);
            throw new ExternalServiceException("Error fetching sun forecast: " + e.getMessage(), e);
        }
    }
    
    /**
     * Maps OpenMeteo weather code to a human-readable weather condition.
     * Reference: https://open-meteo.com/en/docs#api_form
     */
    /**
     * Parses a date-time string that may or may not include timezone information.
     * Handles both ISO_OFFSET_DATE_TIME (with timezone) and ISO_LOCAL_DATE_TIME (without timezone) formats.
     * 
     * @param dateTimeStr the date-time string to parse
     * @return LocalDateTime in the system default timezone
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            // First try to parse with timezone (e.g., "2023-01-01T09:00:00Z")
            return OffsetDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                              .atZoneSameInstant(ZoneId.systemDefault())
                              .toLocalDateTime();
        } catch (DateTimeParseException e) {
            try {
                // If that fails, try without timezone (e.g., "2023-01-01T09:00:00")
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException ex) {
                throw new ExternalServiceException("Invalid date format: " + dateTimeStr, ex);
            }
        }
    }
    
    private String mapWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear";
            case 1, 2, 3 -> "Partly Cloudy";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Drizzle";
            case 56, 57 -> "Freezing Drizzle";
            case 61, 63, 65 -> "Rain";
            case 66, 67 -> "Freezing Rain";
            case 71, 73, 75 -> "Snow";
            case 77 -> "Snow Grains";
            case 80, 81, 82 -> "Rain Showers";
            case 85, 86 -> "Snow Showers";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Unknown";
        };
    }
}
