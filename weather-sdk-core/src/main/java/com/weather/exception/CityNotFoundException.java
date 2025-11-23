package com.weather.exception;

public class CityNotFoundException extends WeatherSDKException {

    public CityNotFoundException(String cityName) {
        super("City not found: " + cityName);
    }

    public CityNotFoundException(String cityName, Throwable cause) {
        super("City not found: " + cityName, cause);
    }
}