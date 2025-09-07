# Process Flow

## Detailed Flow Description

### 1. Request Initiation
- Client sends a GET request to `/api/sun-forecast` with a city parameter
- Request is validated for:
  - Required city parameter
  - City name format (letters, spaces, hyphens only)
  - Length restrictions

### 2. Caching Layer
- System checks if a valid cached response exists for the city
- If cache hit:
  - Returns the cached response immediately
  - Reduces external API calls and improves response time
- If cache miss:
  - Proceeds with the full request flow

### 3. Geocoding Service
- City name is sent to Nominatim (OpenStreetMap) geocoding service
- Service returns:
  - Latitude and longitude coordinates
  - Formatted address
- Error handling:
  - Invalid city names
  - Service unavailability
  - Rate limiting

### 4. Weather Data Retrieval
- Coordinates are sent to Open-Meteo API
- Returns:
  - Sunrise and sunset times (ISO-8601 format)
  - Current weather conditions
  - Temperature and other meteorological data

### 5. AI Enhancement
- Weather data is formatted into a prompt
- Sent to OpenRouter's AI model (GPT-OSS-20B)
- Generates a friendly, contextual weather description
- Includes:
  - Time until sunrise/sunset
  - Weather condition context
  - Appropriate greetings based on time of day
  - Fun facts or tips when relevant

### 6. Response Assembly
- Combines data from all services
- Formats into a consistent JSON response
- Includes:
  - Original city name
  - Coordinates (for reference)
  - Sunrise/sunset times
  - Weather conditions
  - AI-generated description
  - Cache information

### 7. Caching
- Final response is cached with a 1-hour TTL
- Subsequent requests for the same city will be served from cache
- Cache automatically evicts old entries based on size and time

## Error Handling

### Input Validation
- Invalid city names
- Missing parameters
- Malformed requests

### Service Errors
- Geocoding service failures
- Weather API unavailability
- AI service rate limits
- Network timeouts

### Fallback Mechanisms
- Default responses for common cities
- Graceful degradation when services are unavailable
- Cached responses during outages

## Performance Considerations

### Caching Strategy
- Time-based invalidation (1 hour)
- Size-based eviction (1000 entries)
- Key-based on city name (case-insensitive)

### Async Processing
- Non-blocking I/O for external calls
- Parallel API calls where possible
- Connection pooling for HTTP clients

### Monitoring
- API response times
- Cache hit/miss ratios
- Error rates by service
- Rate limit tracking
