package com.weather.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.exception.*;
import com.weather.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Клиент для работы с OpenWeatherMap API
 */
public class ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String apiKey;
    private final String baseUrl;

    public ApiClient(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        logger.info("ApiClient initialized with base URL: {}", baseUrl);
    }

    /**
     * Получает данные о погоде для указанного города
     */
    public WeatherData getWeatherData(String cityName) {
        logger.debug("Fetching weather data for city: {}", cityName);

        HttpURLConnection connection = null;
        try {
            String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
            String urlString = String.format("%s/weather?q=%s&appid=%s", baseUrl, encodedCity, apiKey);

            logger.debug("Making API request to: {}", urlString.replace(apiKey, "***"));

            URL url = URI.create(urlString).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(15000); // 15 seconds
            connection.setRequestProperty("User-Agent", "WeatherSDK/1.0");

            int responseCode = connection.getResponseCode();
            logger.debug("API response code: {}", responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String responseBody = readResponse(connection);
                logger.debug("API response body: {}", responseBody);

                WeatherData weatherData = objectMapper.readValue(responseBody, WeatherData.class);
                logger.info("Successfully fetched weather data for: {}", cityName);
                return weatherData;
            } else {
                handleErrorResponse(responseCode, cityName, connection);
                return null; // Этот код никогда не выполнится из-за исключений
            }

        } catch (WeatherSDKException e) {
            logger.error("Weather SDK error for city {}: {}", cityName, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error fetching weather data for city {}: {}", cityName, e.getMessage(), e);
            throw new WeatherSDKException("Failed to fetch weather data for city: " + cityName, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readResponse(HttpURLConnection connection) {
        try {
            Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8);
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
            // Если не удалось прочитать успешный ответ, пробуем прочитать ошибку
            String errorResponse = readErrorResponse(connection);
            throw new WeatherSDKException("Failed to read API response: " + errorResponse, e);
        }
    }

    private void handleErrorResponse(int responseCode, String cityName, HttpURLConnection connection) {
        String errorMessage = readErrorResponse(connection);
        logger.error("API error for city {}: HTTP {} - {}", cityName, responseCode, errorMessage);

        switch (responseCode) {
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new CityNotFoundException(cityName);
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throw new InvalidApiKeyException();
            case 429: // HTTP Too Many Requests
                throw new ApiCallLimitExceededException();
            case HttpURLConnection.HTTP_BAD_REQUEST:
                throw new WeatherSDKException("Bad request for city: " + cityName);
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                throw new WeatherSDKException("OpenWeatherMap API internal error");
            default:
                throw new WeatherSDKException("API error: HTTP " + responseCode + " for city: " + cityName);
        }
    }

    private String readErrorResponse(HttpURLConnection connection) {
        try {
            if (connection.getErrorStream() != null) {
                Scanner scanner = new Scanner(connection.getErrorStream(), StandardCharsets.UTF_8);
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "Unknown error (no error stream)";
            }
            return "Unknown error (no error stream available)";
        } catch (Exception e) {
            return "Failed to read error response: " + e.getMessage();
        }
    }
}
