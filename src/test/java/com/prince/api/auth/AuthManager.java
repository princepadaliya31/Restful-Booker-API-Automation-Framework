package com.prince.api.auth;

import com.prince.api.config.ConfigReader;
import com.prince.api.endpoints.Endpoints;
import com.prince.api.models.AuthRequest;
import com.prince.api.requests.RequestSpec;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * Authentication manager responsible for acquiring and caching authentication tokens.
 */
public class AuthManager {

    private static String cachedToken;

    private AuthManager() {
        // Prevent instantiation
    }

    public static synchronized String getAuthToken() {
        if (cachedToken == null || cachedToken.trim().isEmpty()) {
            cachedToken = generateNewToken(ConfigReader.getUsername(), ConfigReader.getPassword());
        }
        return cachedToken;
    }

    public static String generateNewToken(String username, String password) {
        AuthRequest authRequest = new AuthRequest(username, password);

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body(authRequest)
                .when()
                .post(Endpoints.AUTH)
                .then()
                .extract()
                .response();

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Authentication HTTP request failed with status code: " + response.getStatusCode()
                    + " Body: " + response.getBody().asString());
        }

        String token = response.jsonPath().getString("token");
        if (token == null || token.trim().isEmpty()) {
            String reason = response.jsonPath().getString("reason");
            throw new RuntimeException("Authentication failed to return token. API Reason: " + reason
                    + " Response: " + response.getBody().asString());
        }

        return token;
    }

    public static synchronized void clearCache() {
        cachedToken = null;
    }
}
