package com.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Модель данных о погоде, соответствующая требуемой JSON структуре
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {

    @JsonProperty("weather")
    private List<Weather> weather;

    @JsonProperty("main")
    private Main main;

    @JsonProperty("visibility")
    private Integer visibility;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("dt")
    private Long datetime;

    @JsonProperty("sys")
    private Sys sys;

    @JsonProperty("timezone")
    private Integer timezone;

    @JsonProperty("name")
    private String name;

    private long timestamp; // Внутреннее поле для кэширования

    // Конструкторы
    public WeatherData() {
        this.timestamp = System.currentTimeMillis();
    }

    // Геттеры и сеттеры
    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public void setTimezone(Integer timezone) {
        this.timezone = timezone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Вспомогательные методы для удобства
    public String getWeatherMain() {
        if (weather != null && !weather.isEmpty()) {
            return weather.get(0).getMain();
        }
        return null;
    }

    public String getWeatherDescription() {
        if (weather != null && !weather.isEmpty()) {
            return weather.get(0).getDescription();
        }
        return null;
    }

    public Double getTemp() {
        return main != null ? main.getTemp() : null;
    }

    public Double getFeelsLike() {
        return main != null ? main.getFeelsLike() : null;
    }

    public Double getWindSpeed() {
        return wind != null ? wind.getSpeed() : null;
    }

    public Long getSunrise() {
        return sys != null ? sys.getSunrise() : null;
    }

    public Long getSunset() {
        return sys != null ? sys.getSunset() : null;
    }

    // Вложенные классы для структуры JSON
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        @JsonProperty("main")
        private String main;

        @JsonProperty("description")
        private String description;

        // Геттеры и сеттеры
        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "Weather{main='" + main + "', description='" + description + "'}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        @JsonProperty("temp")
        private Double temp;

        @JsonProperty("feels_like")
        private Double feelsLike;

        // Геттеры и сеттеры
        public Double getTemp() {
            return temp;
        }

        public void setTemp(Double temp) {
            this.temp = temp;
        }

        public Double getFeelsLike() {
            return feelsLike;
        }

        public void setFeelsLike(Double feelsLike) {
            this.feelsLike = feelsLike;
        }

        @Override
        public String toString() {
            return "Main{temp=" + temp + ", feelsLike=" + feelsLike + "}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        @JsonProperty("speed")
        private Double speed;

        // Геттеры и сеттеры
        public Double getSpeed() {
            return speed;
        }

        public void setSpeed(Double speed) {
            this.speed = speed;
        }

        @Override
        public String toString() {
            return "Wind{speed=" + speed + "}";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        @JsonProperty("sunrise")
        private Long sunrise;

        @JsonProperty("sunset")
        private Long sunset;

        // Геттеры и сеттеры
        public Long getSunrise() {
            return sunrise;
        }

        public void setSunrise(Long sunrise) {
            this.sunrise = sunrise;
        }

        public Long getSunset() {
            return sunset;
        }

        public void setSunset(Long sunset) {
            this.sunset = sunset;
        }

        @Override
        public String toString() {
            return "Sys{sunrise=" + sunrise + ", sunset=" + sunset + "}";
        }
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData that = (WeatherData) o;
        return timestamp == that.timestamp &&
                Objects.equals(weather, that.weather) &&
                Objects.equals(main, that.main) &&
                Objects.equals(visibility, that.visibility) &&
                Objects.equals(wind, that.wind) &&
                Objects.equals(datetime, that.datetime) &&
                Objects.equals(sys, that.sys) &&
                Objects.equals(timezone, that.timezone) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weather, main, visibility, wind, datetime, sys, timezone, name, timestamp);
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "weather=" + weather +
                ", main=" + main +
                ", visibility=" + visibility +
                ", wind=" + wind +
                ", datetime=" + datetime +
                ", sys=" + sys +
                ", timezone=" + timezone +
                ", name='" + name + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}