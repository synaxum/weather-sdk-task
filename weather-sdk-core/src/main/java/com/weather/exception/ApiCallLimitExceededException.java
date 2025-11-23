package com.weather.exception;


public class ApiCallLimitExceededException extends WeatherSDKException {

    public ApiCallLimitExceededException() {
        super("API call limit exceeded");
    }

    public ApiCallLimitExceededException(Throwable cause) {
        super("API call limit exceeded", cause);
    }
}