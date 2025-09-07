package com.example.sunrisesunsetforecast.service;

import com.example.sunrisesunsetforecast.dto.SunForecastResponse;

public interface SunForecastService {
    SunForecastResponse getSunForecast(String city);
}
