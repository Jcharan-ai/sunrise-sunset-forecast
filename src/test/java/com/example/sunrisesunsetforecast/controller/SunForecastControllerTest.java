package com.example.sunrisesunsetforecast.controller;

import com.example.sunrisesunsetforecast.dto.SunForecastResponse;
import com.example.sunrisesunsetforecast.service.SunForecastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SunForecastController.class)
class SunForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SunForecastService sunForecastService;

    private SunForecastResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = SunForecastResponse.builder()
                .city("Berlin")
                .sunrise(OffsetDateTime.parse("2023-11-01T07:12:00+05:30"))
                .sunset(OffsetDateTime.parse("2023-11-01T17:49:00+05:30"))
                .enhancedMessage("In Berlin, the sun will rise at 07:12 and set at 17:49. A perfect day to enjoy the outdoors!")
                .build();
    }

    @Test
    void getSunForecast_ValidCity_ReturnsOk() throws Exception {
        when(sunForecastService.getSunForecast(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/sun-forecast")
                        .param("city", "Berlin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Berlin"))
                .andExpect(jsonPath("$.sunrise").isNotEmpty())
                .andExpect(jsonPath("$.sunset").isNotEmpty())
                .andExpect(jsonPath("$.enhancedMessage").isNotEmpty());
    }

    @Test
    void getSunForecast_MissingCityParam_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/sun-forecast"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSunForecast_EmptyCityParam_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/sun-forecast")
                        .param("city", ""))
                .andExpect(status().isBadRequest());
    }
}
