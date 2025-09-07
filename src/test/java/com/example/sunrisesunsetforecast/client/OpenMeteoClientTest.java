package com.example.sunrisesunsetforecast.client;

import com.example.sunrisesunsetforecast.client.dto.OpenMeteoResponse;
import com.example.sunrisesunsetforecast.model.Coordinates;
import com.example.sunrisesunsetforecast.service.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import java.net.URI;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenMeteoClientTest {

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private GeocodingService geocodingService;

    @Captor
    private ArgumentCaptor<URI> uriCaptor;

    @InjectMocks
    private OpenMeteoClient openMeteoClient;

    @BeforeEach
    void setUp() {
        try {
            java.lang.reflect.Field field = OpenMeteoClient.class.getDeclaredField("baseUrl");
            field.setAccessible(true);
            field.set(openMeteoClient, "https://api.open-meteo.com/v1/forecast");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set baseUrl field", e);
        }
        
        // Reset mocks before each test
        reset(webClient, requestHeadersUriSpec, requestHeadersSpec, responseSpec);
        
        // Set up the WebClient mock chain - only set up the basic chain here
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }
    
    @Test
    void getSunForecast_ShouldReturnResponse_WhenCityIsValid() {
        // Arrange
        String city = "London";
        Coordinates coordinates = new Coordinates(51.5074, -0.1278);
        
        // Create a properly formatted response with date and time
        String date = LocalDate.now().plusDays(1).toString();
        String sunriseTime = "09:00";
        String sunsetTime = "20:00";
        
        OpenMeteoResponse expectedResponse = new OpenMeteoResponse();
        OpenMeteoResponse.Daily daily = new OpenMeteoResponse.Daily();
        daily.setSunriseTimes(Collections.singletonList(date + "T" + sunriseTime));
        daily.setSunsetTimes(Collections.singletonList(date + "T" + sunsetTime));
        daily.setTemperature2mMax(Collections.singletonList(25.0));
        daily.setWeatherCode(Collections.singletonList(0));
        expectedResponse.setDaily(daily);
        
        // Mock the geocoding service to return coordinates
        when(geocodingService.getCoordinates(city)).thenReturn(coordinates);
        
        // Mock the response from the web client
        when(responseSpec.bodyToMono(OpenMeteoResponse.class)).thenReturn(Mono.just(expectedResponse));
        
        // Act
        OpenMeteoResponse response = openMeteoClient.getSunForecast(city);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getDaily());
        assertEquals(1, response.getDaily().getSunriseTimes().size());
        assertEquals(1, response.getDaily().getSunsetTimes().size());
        assertEquals(date + "T" + sunriseTime, response.getDaily().getSunriseTimes().get(0));
        assertEquals(date + "T" + sunsetTime, response.getDaily().getSunsetTimes().get(0));
        
        // Verify the WebClient was called with the correct parameters
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());
        
        String expectedUri = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=51.5074&longitude=-0.1278&daily=sunrise,sunset,temperature_2m_max,weathercode&timezone=auto";
        assertTrue(uriCaptor.getValue().toString().startsWith(expectedUri));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(OpenMeteoResponse.class);
    }
}
