package com.example.sunrisesunsetforecast.service.impl;

import com.example.sunrisesunsetforecast.client.OpenMeteoClient;
import com.example.sunrisesunsetforecast.client.dto.OpenMeteoResponse;
import com.example.sunrisesunsetforecast.dto.SunForecastResponse;
import com.example.sunrisesunsetforecast.exception.ExternalServiceException;
import com.example.sunrisesunsetforecast.model.Coordinates;
import com.example.sunrisesunsetforecast.service.ForecastDescriptionService;
import com.example.sunrisesunsetforecast.service.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SunForecastServiceImplTest {

    @Mock
    private OpenMeteoClient openMeteoClient;

    @Mock
    private ForecastDescriptionService forecastDescriptionService;

    @Mock
    private GeocodingService geocodingService;

    private SunForecastServiceImpl sunForecastService;
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        // Create a simple cache manager for testing
        cacheManager = new ConcurrentMapCacheManager("sunForecast", "openMeteoForecast", "cityCoordinates");
        
        // Initialize the service with all required dependencies
        sunForecastService = new SunForecastServiceImpl(
            openMeteoClient, 
            forecastDescriptionService, 
            cacheManager,
            geocodingService
        );
        
        // Clear cache before each test
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    
    private OpenMeteoResponse createMockResponse() {
        // Create a complete mock response with all required fields
        OpenMeteoResponse mockResponse = new OpenMeteoResponse();
        
        // Create a daily forecast with tomorrow's date
        OpenMeteoResponse.Daily daily = new OpenMeteoResponse.Daily();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String tomorrowStr = tomorrow.toString();
        
        // Set all required fields with valid data
        List<String> timeList = new ArrayList<>();
        timeList.add(tomorrowStr);
        daily.setTime(timeList);
        
        // Format times as "yyyy-MM-dd'T'HH:mm" (without timezone)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        
        List<String> sunriseTimes = new ArrayList<>();
        String sunriseTime = tomorrow.atTime(6, 0).format(formatter);
        sunriseTimes.add(sunriseTime);
        daily.setSunriseTimes(sunriseTimes);
        
        List<String> sunsetTimes = new ArrayList<>();
        String sunsetTime = tomorrow.atTime(18, 0).format(formatter);
        sunsetTimes.add(sunsetTime);
        daily.setSunsetTimes(sunsetTimes);
        
        List<Double> temperatures = new ArrayList<>();
        temperatures.add(25.0);
        daily.setTemperature2mMax(temperatures);
        
        List<Integer> weatherCodes = new ArrayList<>();
        weatherCodes.add(0);
        daily.setWeatherCode(weatherCodes);
        
        // Set the daily object in the response
        mockResponse.setDaily(daily);
        
        // Set required location fields
        mockResponse.setLatitude(51.5074);  // London latitude
        mockResponse.setLongitude(-0.1278); // London longitude
        
        return mockResponse;
    }

    @Test
    void getSunForecast_ShouldReturnValidResponse_WhenCityIsValid() {
        // Arrange
        String city = "London";
        
        // Create a mock response
        OpenMeteoResponse mockResponse = createMockResponse();
        
        // Mock the geocoding service
        Coordinates londonCoords = new Coordinates(51.5074, -0.1278);
        when(geocodingService.getCoordinates(city)).thenReturn(londonCoords);
        
        // Mock the client to return our test response
        when(openMeteoClient.getSunForecast(city)).thenReturn(mockResponse);
        
        // Mock the forecast description service
        when(forecastDescriptionService.generateForecastDescription(
            anyString(), any(LocalDate.class), any(LocalTime.class), 
            any(LocalTime.class), anyDouble(), anyString()))
            .thenReturn("Test description");
        
        // Act
        SunForecastResponse result = sunForecastService.getSunForecast(city);
        
        // Assert
        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getSunrise(), "Sunrise time should not be null");
        assertNotNull(result.getSunset(), "Sunset time should not be null");
        assertNotNull(result.getEnhancedMessage(), "Enhanced message should not be null");
        
        // Verify interactions
        verify(geocodingService, times(1)).getCoordinates(city);
        verify(openMeteoClient, times(1)).getSunForecast(city);
        verify(forecastDescriptionService, times(1)).generateForecastDescription(
            anyString(), any(LocalDate.class), any(LocalTime.class), 
            any(LocalTime.class), anyDouble(), anyString());
    }

    @Test
    void getSunForecast_ShouldThrowException_WhenResponseIsNull() {
        // Arrange
        String city = "InvalidCity";
        
        // Mock the geocoding service
        Coordinates coords = new Coordinates(51.5074, -0.1278);
        when(geocodingService.getCoordinates(city)).thenReturn(coords);
        
        // Mock the client to return null
        when(openMeteoClient.getSunForecast(city)).thenReturn(null);

        // Act & Assert
        assertThrows(ExternalServiceException.class, () -> 
            sunForecastService.getSunForecast(city)
        );
        
        // Verify interactions
        verify(geocodingService, times(1)).getCoordinates(city);
        verify(openMeteoClient, times(1)).getSunForecast(city);
    }

    @Test
    void getSunForecast_ShouldThrowException_WhenDailyDataIsNull() {
        // Arrange
        String city = "NoDataCity";
        
        // Mock the geocoding service
        Coordinates coords = new Coordinates(51.5074, -0.1278);
        when(geocodingService.getCoordinates(city)).thenReturn(coords);
        
        // Create a response with null daily data
        OpenMeteoResponse mockResponse = new OpenMeteoResponse();
        mockResponse.setLatitude(51.5074);
        mockResponse.setLongitude(-0.1278);
        when(openMeteoClient.getSunForecast(city)).thenReturn(mockResponse);

        // Act & Assert
        assertThrows(ExternalServiceException.class, () -> 
            sunForecastService.getSunForecast(city)
        );
        
        // Verify interactions
        verify(geocodingService, times(1)).getCoordinates(city);
        verify(openMeteoClient, times(1)).getSunForecast(city);
    }
}