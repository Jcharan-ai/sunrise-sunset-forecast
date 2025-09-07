package com.example.sunrisesunsetforecast.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sunriseSunsetForecastOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sunrise Sunset Forecast API")
                        .description("API for getting sunrise and sunset times with AI-powered descriptions")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Support")
                                .email("jammula.charan.ai@gmail.com")
                                .url("https://github.com/yourusername/sunrise-sunset-forecast"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
