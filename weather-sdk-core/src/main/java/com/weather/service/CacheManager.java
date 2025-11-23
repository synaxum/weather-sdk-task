package com.weather.service;

import com.weather.model.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Менеджер кэша с LRU политикой и TTL
 */
public class CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private final LinkedHashMap<String, WeatherData> cache;
    private final int capacity;
    private final long ttlMillis;
    private final ReentrantReadWriteLock lock;

    public CacheManager(int capacity, long ttlMinutes) {
        this.capacity = capacity;
        this.ttlMillis = ttlMinutes * 60 * 1000;
        this.lock = new ReentrantReadWriteLock();

        this.cache = new LinkedHashMap<String, WeatherData>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, WeatherData> eldest) {
                boolean shouldRemove = size() > capacity;
                if (shouldRemove) {
                    logger.debug("Removing eldest cache entry: {}", eldest.getKey());
                }
                return shouldRemove;
            }
        };

        logger.info("CacheManager initialized with capacity: {}, TTL: {} minutes", capacity, ttlMinutes);
    }


    public WeatherData get(String city) {
        lock.readLock().lock();
        try {
            WeatherData data = cache.get(city);
            if (data != null && isDataValid(data)) {
                logger.debug("Cache hit for city: {}", city);
                return data;
            } else if (data != null) {
                logger.debug("Cache data expired for city: {}", city);
                remove(city); // Remove expired data
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }


    public void put(String city, WeatherData data) {
        if (city == null || data == null) {
            logger.warn("Attempt to put null city or data into cache");
            return;
        }

        lock.writeLock().lock();
        try {
            cache.put(city, data);
            logger.debug("Added to cache: {}", city);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void remove(String city) {
        lock.writeLock().lock();
        try {
            cache.remove(city);
            logger.debug("Removed from cache: {}", city);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isDataValid(WeatherData data) {
        if (data == null) return false;

        long currentTime = System.currentTimeMillis();
        long dataAge = currentTime - data.getTimestamp();
        boolean isValid = dataAge < ttlMillis;

        logger.debug("Data age: {} ms, TTL: {} ms, Valid: {}", dataAge, ttlMillis, isValid);
        return isValid;
    }


    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
            logger.info("Cache cleared");
        } finally {
            lock.writeLock().unlock();
        }
    }


    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }


    public String[] getAllCities() {
        lock.readLock().lock();
        try {
            return cache.keySet().toArray(new String[0]);
        } finally {
            lock.readLock().unlock();
        }
    }
}