package com.example.sunrisesunsetforecast.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastDescriptionService {

    private final ChatLanguageModel chatLanguageModel;

    public String generateForecastDescription(String location, LocalDate date, 
                                           LocalTime sunrise, LocalTime sunset, 
                                           double temperature, String weatherCondition) {
        
        String prompt = String.format("""
            You are a friendly weather assistant. Generate a short, engaging weather forecast description.
            
            Location: %s
            Date: %s
            Sunrise: %s
            Sunset: %s
            Temperature: %.1f°C
            Conditions: %s
            
            Please provide a 2-3 sentence description that's:
            - Conversational and friendly
            - Includes an interesting fact or tip about the day
            - Mentions any notable weather conditions
            - Keeps it positive and engaging
            
            Format the response as plain text, no markdown or special formatting.
            """, location, date, sunrise, sunset, temperature, weatherCondition);
            
        try {
            return chatLanguageModel.generate(prompt).trim();
        } catch (Exception e) {
            log.error("Error generating forecast description with OpenRouter", e);
            return String.format("In %s, the sun will rise at %s and set at %s. " +
                              "Expect %s with temperatures around %.1f°C. Have a wonderful day!",
                    location, sunrise, sunset, weatherCondition.toLowerCase(), temperature);
        }
    }
}
