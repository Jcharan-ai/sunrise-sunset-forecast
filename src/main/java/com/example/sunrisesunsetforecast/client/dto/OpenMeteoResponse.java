package com.example.sunrisesunsetforecast.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO representing the response from the OpenMeteo API.
 * See: https://open-meteo.com/
 */
@Data
public class OpenMeteoResponse {
    private double latitude;
    private double longitude;
    private Daily daily;
    
    @Data
    public static class Daily {
        private List<String> time;
        
        @JsonProperty("sunrise")
        private List<String> sunriseTimes;
        
        @JsonProperty("sunset")
        private List<String> sunsetTimes;
        
        @JsonProperty("temperature_2m_max")
        private List<Double> temperature2mMax;
        
        @JsonProperty("weathercode")
        private List<Integer> weatherCode;
    }
}
