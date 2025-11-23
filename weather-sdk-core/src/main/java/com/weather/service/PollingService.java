package com.weather.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PollingService {
    private static final Logger logger = LoggerFactory.getLogger(PollingService.class);

    private final ScheduledExecutorService scheduler;
    private final WeatherProvider weatherProvider;
    private final long intervalMinutes;
    private volatile boolean isRunning = false;

    public PollingService(WeatherProvider weatherProvider, long intervalMinutes) {
        this.weatherProvider = weatherProvider;
        this.intervalMinutes = intervalMinutes;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "WeatherPollingService");
            thread.setDaemon(true);
            return thread;
        });
    }


    public void start() {
        if (isRunning) {
            logger.warn("Polling service is already running");
            return;
        }

        logger.info("Starting polling service with interval: {} minutes", intervalMinutes);
        isRunning = true;

        scheduler.scheduleAtFixedRate(this::updateAllCachedData, 0, intervalMinutes, TimeUnit.MINUTES);
    }


    public void stop() {
        if (!isRunning) {
            logger.warn("Polling service is not running");
            return;
        }

        logger.info("Stopping polling service");
        isRunning = false;
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    private void updateAllCachedData() {
        try {
            logger.debug("Starting scheduled cache update");
            weatherProvider.updateAllCachedData();
            logger.debug("Completed scheduled cache update");
        } catch (Exception e) {
            logger.error("Error during scheduled cache update", e);
            // Не пробрасываем исключение, чтобы сервис продолжал работать
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}