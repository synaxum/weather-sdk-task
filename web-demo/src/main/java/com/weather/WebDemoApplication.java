package com.weather;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Веб-демо приложение на Javalin
 */
public class WebDemoApplication {
    private static final Logger logger = LoggerFactory.getLogger(WebDemoApplication.class);

    private static final int PORT = 8080;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            logger.info("Starting Weather SDK Web Demo Application...");

            Javalin app = createAndStartServer();

            logger.info("Weather SDK Web Demo Application started at: http://localhost:{}", PORT);
            logger.info("Available endpoints:");
            logger.info("  GET http://localhost:{}/weather?city=London", PORT);
            logger.info("  GET http://localhost:{}/weather/cache/info", PORT);
            logger.info("Press CTRL+C to stop the application...");

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down Weather SDK Web Demo Application...");
                app.stop();
                logger.info("Application shutdown completed");
            }));

        } catch (Exception e) {
            logger.error("Failed to start Weather SDK Web Demo Application", e);
        }
    }

    public static Javalin createAndStartServer() {
        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper));
            config.plugins.enableDevLogging();
        });

        // Initialize controller
        WeatherController weatherController = new WeatherController();

        // Setup routes
        app.get("/weather", weatherController::getWeather);
        app.get("/weather/cache/info", weatherController::getCacheInfo);

        // Exception handling
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Unhandled exception", e);
            ctx.status(500).json(new ErrorResponse("Internal server error"));
        });

        app.start(PORT);
        return app;
    }

    // Helper class for error responses
    public static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}