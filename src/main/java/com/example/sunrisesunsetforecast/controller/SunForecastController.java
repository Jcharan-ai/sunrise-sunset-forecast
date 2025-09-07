package com.example.sunrisesunsetforecast.controller;

import com.example.sunrisesunsetforecast.dto.SunForecastResponse;
import com.example.sunrisesunsetforecast.service.SunForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Sun Forecast", description = "APIs for getting sunrise and sunset information")
@RestController
@RequestMapping("/api/sun-forecast")
@RequiredArgsConstructor
@Validated
public class SunForecastController {

    private final SunForecastService sunForecastService;

    @Operation(
        summary = "Get sunrise and sunset forecast for a city",
        description = "Returns sunrise and sunset times along with an AI-generated description for the specified city"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved forecast",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SunForecastResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid city name provided",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "City not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
        )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SunForecastResponse> getSunForecast(
            @Parameter(
                name = "city",
                description = "Name of the city to get forecast for",
                required = true,
                example = "London"
            )
            @RequestParam("city") 
            @NotBlank(message = "City name is required") 
            @Pattern(regexp = "^[a-zA-Z\\s-]+", message = "City name must contain only letters, spaces, and hyphens")
            String city) {
        
        return ResponseEntity.ok(sunForecastService.getSunForecast(city));
    }
}
