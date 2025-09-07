# Architecture Overview

## System Architecture

```
┌───────────────────────────────────────────────────────────┐
│                    Client Applications                    │
└───────────────┬───────────────────────────┬───────────────┘
                │                           │
                ▼                           ▼
┌───────────────────────────────────────────────────────────┐
│                 API Gateway (Spring Boot)                 │
└───────────────┬───────────────────────────┬───────────────┘
                │                           │
                ▼                           ▼
┌─────────────────────────┐   ┌───────────────────────────┐
│   Geocoding Service     │   │    Weather Service        │
│   (Nominatim/OSM)       │   │    (Open-Meteo API)       │
└─────────────┬───────────┘   └────────────┬──────────────┘
              │                            │
              └───────────┬────────────────┘
                          ▼
┌───────────────────────────────────────────────────────────┐
│                 AI Enhancement Service                    │
│                 (OpenRouter + LangChain4J)                │
└───────────────────────────────────────────────────────────┘
```

## Component Diagram

```
┌───────────────────────────────────────────────────────────┐
│                 Sunrise-Sunset Forecast App               │
│                                                           │
│  ┌─────────────┐    ┌─────────────────┐    ┌───────────┐  │
│  │  Controller │    │  Service Layer  │    │   Cache   │  │
│  └──────┬──────┘    └────────┬────────┘    └─────┬─────┘  │
│         │                    │                   │        │
│  ┌──────▼──────┐    ┌────────▼────────┐          │        │
│  │  Validation│    │  Business Logic │          │        │
│  └─────────────┘    └────────┬────────┘          │        │
│                               │                   │        │
│  ┌───────────────────┐ ┌─────▼──────┐   ┌────────▼───────┐│
│  │  OpenRouter AI    │ │  OpenMeteo │   │   Nominatim    ││
│  │  Integration     │ │  Client    │   │   Geocoding    ││
│  └───────────────────┘ └────────────┘   └───────────────┘│
│                                                           │
└───────────────────────────────────────────────────────────┘
```

## Data Flow

1. **Request Handling**
   - Client sends a GET request to `/api/sun-forecast` with a city name
   - Request is validated for format and required parameters

2. **Geocoding**
   - City name is sent to Nominatim geocoding service
   - Returns latitude and longitude coordinates
   - Results are cached to reduce API calls

3. **Weather Data Retrieval**
   - Coordinates are sent to Open-Meteo API
   - Returns sunrise/sunset times and weather conditions

4. **AI Enhancement**
   - Weather data is sent to OpenRouter's AI model
   - Generates a friendly, natural language description

5. **Response**
   - All data is combined into a single response
   - Response is cached for future requests

## Technology Stack

- **Framework**: Spring Boot 3.1.5
- **Language**: Java 17
- **AI Integration**: OpenRouter with LangChain4J
- **APIs**:
  - Open-Meteo (Weather Data)
  - Nominatim (Geocoding)
  - OpenRouter (AI Text Generation)
- **Caching**: Caffeine
- **Documentation**: SpringDoc OpenAPI 2.2.0
- **Testing**: JUnit 5, Mockito, Spring Test

## Security Considerations

- API keys are managed through environment variables
- Input validation to prevent injection attacks
- Rate limiting (to be implemented)
- CORS configuration (to be implemented)

## Performance Considerations

- Response caching (1-hour TTL)
- Non-blocking I/O with WebClient
- Connection pooling for external API calls
- Async processing for AI enhancements
