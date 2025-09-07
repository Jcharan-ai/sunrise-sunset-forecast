package com.example.sunrisesunsetforecast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SunriseSunsetForecastApplication {

    public static void main(String[] args) {
        SpringApplication.run(SunriseSunsetForecastApplication.class, args);
    }
}
