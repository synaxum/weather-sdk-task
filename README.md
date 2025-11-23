# Weather SDK

A production-ready Java SDK for interacting with the OpenWeatherMap API. Provides a simple and efficient way to retrieve weather data with caching, error handling, and multiple operating modes.

## Features

- ✅ Get weather data for any city worldwide
- ✅ LRU caching for up to 10 cities
- ✅ Two operating modes: On-demand and Polling
- ✅ Comprehensive error handling and exceptions
- ✅ Thread-safe implementation
- ✅ Configuration via properties file
- ✅ Ready-to-use JSON response in required format
- ✅ Web demo application for testing

## Prerequisites

- Java 11 or higher
- Maven 3.6+ or Gradle
- OpenWeatherMap API key ([Get one here](https://openweathermap.org/api))

## Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.weather</groupId>
    <artifactId>weather-sdk-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

Manual Installation
```
Clone the repository:
 git clone https://github.com/your-username/weather-sdk.git
 cd weather-sdk
```

Create config.properties in your src/main/resources directory:
```
# OpenWeatherMap Configuration
openweather.api.key=your_actual_api_key_here
openweather.api.url=https://api.openweathermap.org/data/2.5

# SDK Configuration
sdk.operating.mode=ON_DEMAND  # ON_DEMAND or POLLING
sdk.cache.capacity=10
sdk.cache.ttl.minutes=10
sdk.polling.interval.minutes=10

# Logging Configuration
logging.output=CONSOLE  # CONSOLE or FILE
logging.level=INFO
```

# Basic Usage
```java
import com.weather.sdk.dto.WeatherResponse;
import com.weather.sdk.factory.WeatherProviderFactory;
import com.weather.sdk.service.WeatherProvider;

public class WeatherApp {
    public static void main(String[] args) {
        // Get provider instance from factory
        WeatherProviderFactory factory = WeatherProviderFactory.getInstance();
        WeatherProvider weatherProvider = factory.getWeatherProvider();
        
        // Get weather data
        WeatherResponse response = weatherProvider.getWeather("London");
        
        // Use the data
        System.out.println("City: " + response.getName());
        System.out.println("Temperature: " + response.getTemperature().getTemp() + "K");
        System.out.println("Weather: " + response.getWeather().getDescription());
        System.out.println("Humidity: " + response.getVisibility());
    }
}
```

