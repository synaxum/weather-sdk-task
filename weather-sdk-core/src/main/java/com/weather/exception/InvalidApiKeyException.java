package com.weather.exception;

public class InvalidApiKeyException extends WeatherSDKException {

    public InvalidApiKeyException() {
        super("Invalid API key provided");
    }

    public InvalidApiKeyException(Throwable cause) {
        super("Invalid API key provided", cause);
    }
}