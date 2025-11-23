package com.weather.exception;


public class WeatherSDKException extends RuntimeException {

    public WeatherSDKException(String message) {
        super(message);
    }

    public WeatherSDKException(String message, Throwable cause) {
        super(message, cause);
    }
}