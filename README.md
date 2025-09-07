# 🌅 Sunrise and Sunset Forecast Application

A high-performance Spring Boot application that provides accurate sunrise and sunset times for any city worldwide, enhanced with AI-generated weather descriptions using OpenRouter's AI models.

## ✨ Features

- **Accurate Solar Data**: Get precise sunrise and sunset times for any location
- **AI-Powered Descriptions**: Natural language weather summaries powered by OpenRouter's AI models
- **Intelligent Caching**: Optimized response times with configurable caching
- **Robust Error Handling**: Comprehensive error handling and fallback mechanisms
- **RESTful API**: Clean, well-documented endpoints following REST best practices
- **OpenAPI Documentation**: Interactive API documentation with Swagger UI
- **Asynchronous Processing**: Non-blocking I/O for improved throughput

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.8+ or Gradle 7.6+
- OpenRouter API key (for AI enhancements)
- Open-Meteo API (free, no key required)
- Internet connection (for external API access)

## 🚀 Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/Jcharan-ai/sunrise-sunset-forecast.git
   cd sunrise-sunset-forecast
   ```

2. **Configuration**
   Copy the example configuration and update with your details:
   ```bash
   cp src/main/resources/application.example.yml src/main/resources/application.yml
   ```
   
   Update the following in `application.yml`:
   - `geocoding.user-agent`: Your application name and contact email
   - `openrouter.api.key`: Your OpenRouter API key (or set as `OPENROUTER_API_KEY` environment variable)

3. **Build and Run**
   ```bash
   # Build the application
   mvn clean package
   
   # Run the application
   mvn spring-boot:run
   ```
   
   The application will be available at `http://localhost:8083`

4. **Verify Installation**
   Visit the Swagger UI at `http://localhost:8083/swagger-ui.html` to explore the API documentation.

## 📡 API Documentation

### Interactive Documentation
- **Swagger UI**: `http://localhost:8083/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8083/v3/api-docs`

### Available Endpoints

#### Get Sunrise/Sunset Forecast
```
GET /api/sun-forecast?city={cityName}
```

**Query Parameters:**
- `city` (required): Name of the city (e.g., "London", "New York")

**Example Request:**
```http
GET /api/sun-forecast?city=Tokyo
Accept: application/json
```

**Success Response (200 OK):**
```json
{
  "city": "Tokyo",
  "coordinates": {
    "latitude": 35.6828387,
    "longitude": 139.7594549
  },
  "sunrise": "2023-11-01T05:45:00+09:00",
  "sunset": "2023-11-01T16:30:00+09:00",
  "temperature": 18.5,
  "weatherCondition": "Partly Cloudy",
  "enhancedMessage": "Good morning! In Tokyo, the sun rose at 05:45 and will set at 16:30. With partly cloudy skies and a comfortable 18.5°C, it's a great day to explore the city!"
}
```

**Error Responses:**
- `400 Bad Request`: Invalid city name or missing parameters
- `404 Not Found`: City not found
- `500 Internal Server Error`: Service unavailable or error processing request

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run with test coverage report
mvn test jacoco:report
```

### Test Coverage
- Unit tests for all service layers
- Integration tests for API endpoints
- Mock external service dependencies
- Test coverage reports with JaCoCo

## 🛠️ Built With

- **Core Framework**: Spring Boot 3.1.5
- **Language**: Java 17
- **AI Integration**: OpenRouter with LangChain4J
- **APIs**:
  - Open-Meteo (Weather Data)
  - Nominatim (Geocoding)
  - OpenRouter (AI Text Generation)
- **Caching**: Caffeine
- **Documentation**: SpringDoc OpenAPI 2.2.0
- **Testing**: JUnit 5, Mockito, Spring Test

## 📚 Documentation

For detailed information about the application architecture and data flow, see:

- [Architecture Overview](docs/ARCHITECTURE.md)
- [Process Flow](docs/PROCESS_FLOW.md)

## 🤝 Contributing

Contributions are welcome! Please read our [contributing guidelines](CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- OpenMeteo for free weather API
- OpenStreetMap/Nominatim for geocoding
- OpenRouter for AI model access
