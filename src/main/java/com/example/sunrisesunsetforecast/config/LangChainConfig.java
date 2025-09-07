package com.example.sunrisesunsetforecast.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(openRouterApiKey)
                .modelName("openai/gpt-oss-20b:free")
                .temperature(0.7)
                .maxTokens(500)
                .topP(0.9)
                .presencePenalty(0.1)
                .frequencyPenalty(0.1)
                .timeout(java.time.Duration.ofSeconds(30))
                .logRequests(true)
                .logResponses(true)
                .baseUrl("https://openrouter.ai/api/v1")
                .build();
    }
}
