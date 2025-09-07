package com.example.sunrisesunsetforecast.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NominatimGeocodingServiceTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private NominatimGeocodingService geocodingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(geocodingService, "baseUrl", "https://nominatim.openstreetmap.org");
        ReflectionTestUtils.setField(geocodingService, "userAgent", "test-agent");
    }

    @Test
    void serviceInitialization_ShouldSucceed() {
        // Just verify that the service can be instantiated and injected with mocks
        assertNotNull(geocodingService);
        
        // Verify the fields were set correctly
        assertEquals("https://nominatim.openstreetmap.org", 
            ReflectionTestUtils.getField(geocodingService, "baseUrl"));
        assertEquals("test-agent", 
            ReflectionTestUtils.getField(geocodingService, "userAgent"));
    }
}
