package com.weather.service;

import com.weather.config.OperatingMode;
import com.weather.dto.WeatherResponse;
import com.weather.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WeatherProvider {
    private static final Logger logger = LoggerFactory.getLogger(WeatherProvider.class);

    private final String apiKey;
    private final OperatingMode mode;
    private final ApiClient apiClient;
    private final CacheManager cacheManager;
    private final PollingService pollingService;

    public WeatherProvider(String apiKey, OperatingMode mode,
                           ApiClient apiClient, CacheManager cacheManager) {
        this.apiKey = apiKey;
        this.mode = mode;
        this.apiClient = apiClient;
        this.cacheManager = cacheManager;

        if (mode == OperatingMode.POLLING) {
            this.pollingService = new PollingService(this, 10);
            this.pollingService.start();
            logger.info("WeatherProvider initialized in POLLING mode");
        } else {
            this.pollingService = null;
            logger.info("WeatherProvider initialized in ON_DEMAND mode");
        }
    }


    public WeatherResponse getWeather(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be null or empty");
        }

        String normalizedCity = cityName.trim();
        logger.debug("Getting weather for city: {}", normalizedCity);

        WeatherData weatherData;

        if (mode == OperatingMode.POLLING) {
            weatherData = cacheManager.get(normalizedCity);
            if (weatherData != null) {
                logger.debug("Returning cached data for city: {}", normalizedCity);
                return new WeatherResponse(weatherData);
            } else {
                logger.debug("No cached data found for city: {}, fetching from API", normalizedCity);
                weatherData = fetchAndCacheWeatherData(normalizedCity);
                return new WeatherResponse(weatherData);
            }
        }
        else {
            weatherData = cacheManager.get(normalizedCity);
            if (weatherData != null) {
                logger.debug("Returning valid cached data for city: {}", normalizedCity);
                return new WeatherResponse(weatherData);
            } else {
                logger.debug("Fetching fresh data from API for city: {}", normalizedCity);
                weatherData = fetchAndCacheWeatherData(normalizedCity);
                return new WeatherResponse(weatherData);
            }
        }
    }


    private WeatherData fetchAndCacheWeatherData(String cityName) {
        try {
            WeatherData weatherData = apiClient.getWeatherData(cityName);
            if (weatherData != null) {
                cacheManager.put(cityName, weatherData);
                logger.info("Successfully fetched and cached weather data for: {}", cityName);
            }
            return weatherData;
        } catch (Exception e) {
            logger.error("Failed to fetch weather data for city: {}", cityName, e);
            throw e;
        }
    }

    public void updateAllCachedData() {
        logger.debug("Updating all cached weather data");
        String[] cities = cacheManager.getAllCities();

        int successCount = 0;
        int failureCount = 0;

        for (String city : cities) {
            try {
                WeatherData freshData = apiClient.getWeatherData(city);
                if (freshData != null) {
                    cacheManager.put(city, freshData);
                    successCount++;
                    logger.debug("Successfully updated data for city: {}", city);
                }
            } catch (Exception e) {
                failureCount++;
                logger.warn("Failed to update data for city: {}, error: {}", city, e.getMessage());
            }
        }

        logger.info("Cache update completed: {} successful, {} failed", successCount, failureCount);
    }

    public void shutdown() {
        if (pollingService != null && pollingService.isRunning()) {
            pollingService.stop();
            logger.info("WeatherProvider shutdown completed");
        }
    }


    public String getApiKey() {
        return apiKey;
    }

    public OperatingMode getMode() {
        return mode;
    }

    public int getCacheSize() {
        return cacheManager.size();
    }
}