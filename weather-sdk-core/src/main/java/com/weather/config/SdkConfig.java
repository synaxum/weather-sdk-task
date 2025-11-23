package com.weather.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SdkConfig {
    private static final Logger logger = LoggerFactory.getLogger(SdkConfig.class);
    private static final String CONFIG_FILE = "config.properties";

    private final Properties properties;

    public SdkConfig() {
        this.properties = loadProperties();
        setupLogging();
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
                logger.info("Configuration loaded successfully from {}", CONFIG_FILE);
            } else {
                logger.warn("Configuration file {} not found, using default values", CONFIG_FILE);
                setDefaultProperties(props);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file, using default values", e);
            setDefaultProperties(props);
        }
        return props;
    }

    private void setDefaultProperties(Properties props) {
        props.setProperty("openweather.api.key", "demo_key");
        props.setProperty("openweather.api.url", "https://api.openweathermap.org/data/2.5");
        props.setProperty("sdk.operating.mode", "ON_DEMAND");
        props.setProperty("sdk.cache.capacity", "10");
        props.setProperty("sdk.cache.ttl.minutes", "10");
        props.setProperty("sdk.polling.interval.minutes", "10");
        props.setProperty("logging.output", "CONSOLE");
        props.setProperty("logging.level", "INFO");
    }

    private void setupLogging() {
        String output = properties.getProperty("logging.output", "CONSOLE");
        logger.info("Logging output set to: {}", output);
    }

    public String getApiKey() {
        return properties.getProperty("openweather.api.key");
    }

    public String getApiUrl() {
        return properties.getProperty("openweather.api.url");
    }

    public OperatingMode getOperatingMode() {
        String mode = properties.getProperty("sdk.operating.mode", "ON_DEMAND");
        return OperatingMode.valueOf(mode.toUpperCase());
    }

    public int getCacheCapacity() {
        return Integer.parseInt(properties.getProperty("sdk.cache.capacity", "10"));
    }

    public long getCacheTtlMinutes() {
        return Long.parseLong(properties.getProperty("sdk.cache.ttl.minutes", "10"));
    }

    public long getPollingIntervalMinutes() {
        return Long.parseLong(properties.getProperty("sdk.polling.interval.minutes", "10"));
    }

    public String getLoggingOutput() {
        return properties.getProperty("logging.output", "CONSOLE");
    }

    public String getLoggingLevel() {
        return properties.getProperty("logging.level", "INFO");
    }
}