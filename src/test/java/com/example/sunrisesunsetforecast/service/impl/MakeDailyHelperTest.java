package com.example.sunrisesunsetforecast.service.impl;

import com.example.sunrisesunsetforecast.client.dto.OpenMeteoResponse;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MakeDailyHelperTest extends SunForecastServiceImplTest {

    private OpenMeteoResponse.Daily makeDaily(int daysAhead) {
        OpenMeteoResponse.Daily daily = new OpenMeteoResponse.Daily();
        
        // Set the date
        String date = LocalDate.now().plusDays(daysAhead).toString();
        daily.setTime(Collections.singletonList(date));
        
        // Set sunrise time (format: "yyyy-MM-dd'T'HH:mm")
        String sunriseTime = date + "T06:00";
        daily.setSunriseTimes(Collections.singletonList(sunriseTime));
        
        // Set sunset time (format: "yyyy-MM-dd'T'HH:mm")
        String sunsetTime = date + "T18:00";
        daily.setSunsetTimes(Collections.singletonList(sunsetTime));
        
        // Set temperature
        daily.setTemperature2mMax(Collections.singletonList(25.0));
        
        // Set weather code (0 = clear sky)
        daily.setWeatherCode(Collections.singletonList(0));
        
        return daily;
    }

    @Test
    void makeDaily_ShouldCreateValidDailyObject() {
        // Arrange
        int daysAhead = 1;
        String expectedDate = LocalDate.now().plusDays(daysAhead).toString();
        
        // Act
        OpenMeteoResponse.Daily daily = makeDaily(daysAhead);
        
        // Assert
        assertNotNull(daily, "Daily object should not be null");
        assertNotNull(daily.getTime(), "Time list should not be null");
        assertFalse(daily.getTime().isEmpty(), "Time list should not be empty");
        assertEquals(expectedDate, daily.getTime().get(0), "Time should match expected date");
        
        assertNotNull(daily.getSunriseTimes(), "Sunrise times should not be null");
        assertFalse(daily.getSunriseTimes().isEmpty(), "Sunrise times should not be empty");
        assertTrue(daily.getSunriseTimes().get(0).startsWith(expectedDate + "T"), 
            "Sunrise time should start with the expected date");
        
        assertNotNull(daily.getSunsetTimes(), "Sunset times should not be null");
        assertFalse(daily.getSunsetTimes().isEmpty(), "Sunset times should not be empty");
        assertTrue(daily.getSunsetTimes().get(0).startsWith(expectedDate + "T"),
            "Sunset time should start with the expected date");
        
        assertNotNull(daily.getTemperature2mMax(), "Temperature list should not be null");
        assertFalse(daily.getTemperature2mMax().isEmpty(), "Temperature list should not be empty");
        assertEquals(25.0, daily.getTemperature2mMax().get(0), "Temperature should be 25.0");
        
        assertNotNull(daily.getWeatherCode(), "Weather code list should not be null");
        assertFalse(daily.getWeatherCode().isEmpty(), "Weather code list should not be empty");
        assertEquals(0, daily.getWeatherCode().get(0), "Weather code should be 0");
    }
}
