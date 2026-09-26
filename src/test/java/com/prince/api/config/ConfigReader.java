package com.prince.api.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Thread-safe configuration manager loading properties from classpath config.properties
 * and supporting System property and Environment variable overrides.
 */
public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties in the classpath.");
            }
            properties.load(input);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load configuration properties file.", ex);
        }
    }

    private ConfigReader() {
        // Private constructor to enforce utility design pattern
    }

    public static String getProperty(String key) {
        // 1. System Property override (e.g. -Dbase.url=...)
        String systemProp = System.getProperty(key);
        if (systemProp != null && !systemProp.trim().isEmpty()) {
            return systemProp.trim();
        }

        // 2. Prefixed Environment Variable override (e.g. API_USERNAME, API_PASSWORD)
        String apiEnvProp = System.getenv("API_" + key.toUpperCase().replace('.', '_'));
        if (apiEnvProp != null && !apiEnvProp.trim().isEmpty()) {
            return apiEnvProp.trim();
        }

        // 3. Classpath config.properties file
        String fileProp = properties.getProperty(key);
        if (fileProp != null && !fileProp.trim().isEmpty()) {
            return fileProp.trim();
        }

        // 4. Fallback to generic Environment Variable override (if not empty)
        String envProp = System.getenv(key.toUpperCase().replace('.', '_'));
        if (envProp != null && !envProp.trim().isEmpty()) {
            return envProp.trim();
        }

        throw new RuntimeException("Mandatory configuration property missing: " + key);
    }

    public static String getProperty(String key, String defaultValue) {
        try {
            return getProperty(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    public static String getBaseUrl() {
        return getProperty("base.url");
    }

    public static String getUsername() {
        return getProperty("username");
    }

    public static String getPassword() {
        return getProperty("password");
    }

    public static long getSlaResponseTime() {
        return Long.parseLong(getProperty("sla.response.time", "5000"));
    }
}
