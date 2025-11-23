package com.weather.factory;

import com.weather.config.OperatingMode;
import com.weather.config.SdkConfig;
import com.weather.service.ApiClient;
import com.weather.service.CacheManager;
import com.weather.service.WeatherProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class WeatherProviderFactory {
    private static final Logger logger = LoggerFactory.getLogger(WeatherProviderFactory.class);

    private static WeatherProviderFactory instance;
    private final ConcurrentHashMap<String, WeatherProvider> providers;

    private WeatherProviderFactory() {
        this.providers = new ConcurrentHashMap<>();
        logger.info("WeatherProviderFactory initialized");
    }

    public static synchronized WeatherProviderFactory getInstance() {
        if (instance == null) {
            instance = new WeatherProviderFactory();
        }
        return instance;
    }


    public WeatherProvider getWeatherProvider(String apiKey, OperatingMode mode) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }

        String normalizedApiKey = apiKey.trim();
        String providerKey = generateProviderKey(normalizedApiKey, mode);

        return providers.computeIfAbsent(providerKey, key -> {
            logger.info("Creating new WeatherProvider for API key: {} (mode: {})",
                    maskApiKey(normalizedApiKey), mode);

            SdkConfig config = new SdkConfig();
            ApiClient apiClient = new ApiClient(normalizedApiKey, config.getApiUrl());
            CacheManager cacheManager = new CacheManager(config.getCacheCapacity(), config.getCacheTtlMinutes());

            return new WeatherProvider(normalizedApiKey, mode, apiClient, cacheManager);
        });
    }


    public WeatherProvider getWeatherProvider() {
        SdkConfig config = new SdkConfig();
        return getWeatherProvider(config.getApiKey(), config.getOperatingMode());
    }


    public void removeWeatherProvider(String apiKey, OperatingMode mode) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }

        String normalizedApiKey = apiKey.trim();
        String providerKey = generateProviderKey(normalizedApiKey, mode);

        WeatherProvider provider = providers.remove(providerKey);
        if (provider != null) {
            provider.shutdown();
            logger.info("Removed and shutdown WeatherProvider for API key: {} (mode: {})",
                    maskApiKey(normalizedApiKey), mode);
        } else {
            logger.warn("WeatherProvider not found for removal: {} (mode: {})",
                    maskApiKey(normalizedApiKey), mode);
        }
    }

    public void shutdownAll() {
        logger.info("Shutting down all WeatherProvider instances");

        providers.values().forEach(WeatherProvider::shutdown);
        providers.clear();

        logger.info("All WeatherProvider instances shutdown completed");
    }


    private String generateProviderKey(String apiKey, OperatingMode mode) {
        return apiKey + "|" + mode.name();
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }

    public int getActiveProvidersCount() {
        return providers.size();
    }
}