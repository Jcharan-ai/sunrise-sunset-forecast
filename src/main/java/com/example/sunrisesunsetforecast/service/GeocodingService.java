package com.example.sunrisesunsetforecast.service;

import com.example.sunrisesunsetforecast.model.Coordinates;

public interface GeocodingService {
    Coordinates getCoordinates(String city);
}
