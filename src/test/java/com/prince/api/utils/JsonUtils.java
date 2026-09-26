package com.prince.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

/**
 * Utility class for JSON reading, serialization, and deserialization using Jackson.
 */
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtils() {
        // Prevent instantiation
    }

    public static <T> T readJsonFromClasspath(String resourcePath, Class<T> clazz) {
        try (InputStream inputStream = JsonUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource file not found on classpath: " + resourcePath);
            }
            return objectMapper.readValue(inputStream, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON resource [" + resourcePath + "] into class [" + clazz.getName() + "]", e);
        }
    }

    public static String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object into JSON string", e);
        }
    }

    public static <T> T deserialize(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON string into class [" + clazz.getName() + "]", e);
        }
    }
}
