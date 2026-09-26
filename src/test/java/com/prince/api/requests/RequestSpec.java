package com.prince.api.requests;

import com.prince.api.config.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * Reusable REST Assured RequestSpecification factory.
 * Configured with exact header strings to satisfy Restful Booker strict header matching.
 */
public class RequestSpec {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private RequestSpec() {
        // Prevent instantiation
    }

    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", USER_AGENT)
                .addFilter(new AllureRestAssured())
                .build();
    }

    public static RequestSpecification getAuthRequestSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Cookie", "token=" + token)
                .addFilter(new AllureRestAssured())
                .build();
    }
}
