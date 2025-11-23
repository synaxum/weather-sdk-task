package com.weather.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.weather.model.WeatherData;

import java.util.Objects;

@JsonPropertyOrder({
        "weather",
        "temperature",
        "visibility",
        "wind",
        "datetime",
        "sys",
        "timezone",
        "name"
})
public class WeatherResponse {

    @JsonProperty("weather")
    private WeatherInfo weather;

    @JsonProperty("temperature")
    private TemperatureInfo temperature;

    private Integer visibility;

    @JsonProperty("wind")
    private WindInfo wind;

    private Long datetime;

    @JsonProperty("sys")
    private SysInfo sys;

    private Integer timezone;

    private String name;

    public WeatherResponse(WeatherData weatherData) {
        if (weatherData == null) {
            throw new IllegalArgumentException("WeatherData cannot be null");
        }

        // Weather
        this.weather = new WeatherInfo();
        if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
            var firstWeather = weatherData.getWeather().get(0);
            this.weather.setMain(firstWeather.getMain());
            this.weather.setDescription(firstWeather.getDescription());
        }

        // Temperature
        this.temperature = new TemperatureInfo();
        if (weatherData.getMain() != null) {
            this.temperature.setTemp(weatherData.getMain().getTemp());
            this.temperature.setFeelsLike(weatherData.getMain().getFeelsLike());
        }

        // Other fields
        this.visibility = weatherData.getVisibility();

        // Wind
        this.wind = new WindInfo();
        if (weatherData.getWind() != null) {
            this.wind.setSpeed(weatherData.getWind().getSpeed());
        }

        this.datetime = weatherData.getDatetime();

        // Sys
        this.sys = new SysInfo();
        if (weatherData.getSys() != null) {
            this.sys.setSunrise(weatherData.getSys().getSunrise());
            this.sys.setSunset(weatherData.getSys().getSunset());
        }

        this.timezone = weatherData.getTimezone();
        this.name = weatherData.getName();
    }

    @JsonPropertyOrder({"main", "description"})
    public static class WeatherInfo {
        private String main;
        private String description;

        public String getMain() { return main; }
        public void setMain(String main) { this.main = main; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WeatherInfo that = (WeatherInfo) o;
            return Objects.equals(main, that.main) && Objects.equals(description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(main, description);
        }
    }

    @JsonPropertyOrder({"temp", "feels_like"})
    public static class TemperatureInfo {
        private Double temp;
        private Double feelsLike;

        public Double getTemp() { return temp; }
        public void setTemp(Double temp) { this.temp = temp; }
        public Double getFeelsLike() { return feelsLike; }
        public void setFeelsLike(Double feelsLike) { this.feelsLike = feelsLike; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TemperatureInfo that = (TemperatureInfo) o;
            return Objects.equals(temp, that.temp) && Objects.equals(feelsLike, that.feelsLike);
        }

        @Override
        public int hashCode() {
            return Objects.hash(temp, feelsLike);
        }
    }

    @JsonPropertyOrder({"speed"})
    public static class WindInfo {
        private Double speed;

        public Double getSpeed() { return speed; }
        public void setSpeed(Double speed) { this.speed = speed; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WindInfo that = (WindInfo) o;
            return Objects.equals(speed, that.speed);
        }

        @Override
        public int hashCode() {
            return Objects.hash(speed);
        }
    }

    @JsonPropertyOrder({"sunrise", "sunset"})
    public static class SysInfo {
        private Long sunrise;
        private Long sunset;

        public Long getSunrise() { return sunrise; }
        public void setSunrise(Long sunrise) { this.sunrise = sunrise; }
        public Long getSunset() { return sunset; }
        public void setSunset(Long sunset) { this.sunset = sunset; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SysInfo that = (SysInfo) o;
            return Objects.equals(sunrise, that.sunrise) && Objects.equals(sunset, that.sunset);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sunrise, sunset);
        }
    }

    public WeatherInfo getWeather() { return weather; }
    public TemperatureInfo getTemperature() { return temperature; }
    public Integer getVisibility() { return visibility; }
    public WindInfo getWind() { return wind; }
    public Long getDatetime() { return datetime; }
    public SysInfo getSys() { return sys; }
    public Integer getTimezone() { return timezone; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherResponse that = (WeatherResponse) o;
        return Objects.equals(weather, that.weather) &&
                Objects.equals(temperature, that.temperature) &&
                Objects.equals(visibility, that.visibility) &&
                Objects.equals(wind, that.wind) &&
                Objects.equals(datetime, that.datetime) &&
                Objects.equals(sys, that.sys) &&
                Objects.equals(timezone, that.timezone) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weather, temperature, visibility, wind, datetime, sys, timezone, name);
    }

    @Override
    public String toString() {
        return "WeatherResponse{" +
                "weather=" + weather +
                ", temperature=" + temperature +
                ", visibility=" + visibility +
                ", wind=" + wind +
                ", datetime=" + datetime +
                ", sys=" + sys +
                ", timezone=" + timezone +
                ", name='" + name + '\'' +
                '}';
    }
}