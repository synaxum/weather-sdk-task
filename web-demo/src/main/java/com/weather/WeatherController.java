package com.weather;

import com.weather.dto.WeatherResponse;
import com.weather.exception.*;
import com.weather.factory.WeatherProviderFactory;
import com.weather.service.WeatherProvider;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class WeatherController {
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherProvider weatherProvider;

    public WeatherController() {
        WeatherProviderFactory factory = WeatherProviderFactory.getInstance();
        this.weatherProvider = factory.getWeatherProvider();
        logger.info("WeatherController initialized with WeatherProvider");
    }

    public void getWeather(Context ctx) {
        String city = ctx.queryParam("city");

        if (city == null || city.trim().isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).json(createErrorResponse("City parameter is required"));
            return;
        }

        try {
            logger.info("Received weather request for city: {}", city);

            WeatherResponse weatherResponse = weatherProvider.getWeather(city);

            Map<String, Object> response = new HashMap<>();
            response.put("weather", weatherResponse.getWeather());
            response.put("temperature", weatherResponse.getTemperature());
            response.put("visibility", weatherResponse.getVisibility());
            response.put("wind", weatherResponse.getWind());
            response.put("datetime", weatherResponse.getDatetime());
            response.put("sys", weatherResponse.getSys());
            response.put("timezone", weatherResponse.getTimezone());
            response.put("name", weatherResponse.getName());
            response.put("cacheSize", weatherProvider.getCacheSize());

            logger.info("Successfully returned weather data for city: {}", city);
            ctx.json(response);

        } catch (CityNotFoundException e) {
            logger.warn("City not found: {}", city);
            ctx.status(HttpStatus.NOT_FOUND).json(createErrorResponse(e.getMessage()));
        } catch (InvalidApiKeyException e) {
            logger.error("Invalid API key");
            ctx.status(HttpStatus.UNAUTHORIZED).json(createErrorResponse(e.getMessage()));
        } catch (ApiCallLimitExceededException e) {
            logger.error("API call limit exceeded");
            ctx.status(HttpStatus.TOO_MANY_REQUESTS).json(createErrorResponse(e.getMessage()));
        } catch (WeatherSDKException e) {
            logger.error("Weather SDK error for city {}: {}", city, e.getMessage(), e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error for city {}: {}", city, e.getMessage(), e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(createErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    public void getCacheInfo(Context ctx) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("cacheSize", weatherProvider.getCacheSize());
            response.put("operatingMode", weatherProvider.getMode().name());
            response.put("apiKey", maskApiKey(weatherProvider.getApiKey()));

            ctx.json(response);
        } catch (Exception e) {
            logger.error("Error getting cache info: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(createErrorResponse("Error getting cache info"));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return errorResponse;
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}