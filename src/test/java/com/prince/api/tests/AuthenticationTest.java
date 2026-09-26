package com.prince.api.tests;

import com.prince.api.base.BaseTest;
import com.prince.api.config.ConfigReader;
import com.prince.api.endpoints.Endpoints;
import com.prince.api.models.AuthRequest;
import com.prince.api.requests.RequestSpec;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Authentication Management")
@Feature("Auth Endpoint (/auth)")
public class AuthenticationTest extends BaseTest {

    @Test(groups = {"smoke", "auth", "regression"}, description = "TC01 - Validate successful token creation with valid credentials")
    @Story("User Authentication")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify POST /auth with valid username and password returns HTTP 200 and a valid token.")
    public void testTC01_ValidAuthentication() {
        AuthRequest authRequest = new AuthRequest(ConfigReader.getUsername(), ConfigReader.getPassword());

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body(authRequest)
                .when()
                .post(Endpoints.AUTH)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Authentication token should not be null.");
        Assert.assertFalse(token.trim().isEmpty(), "Authentication token should not be empty.");
    }

    @Test(groups = {"auth", "negative", "regression"}, description = "TC02 - Validate authentication failure with invalid password")
    @Story("User Authentication")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify POST /auth with valid username and invalid password. Restful Booker API quirk: returns HTTP 200 with reason 'Bad credentials'.")
    public void testTC02_InvalidPasswordAuthentication() {
        AuthRequest authRequest = new AuthRequest(ConfigReader.getUsername(), "wrong_password_123");

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body(authRequest)
                .when()
                .post(Endpoints.AUTH)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = response.jsonPath().getString("token");
        String reason = response.jsonPath().getString("reason");

        Assert.assertNull(token, "Token should be null for invalid authentication.");
        Assert.assertEquals(reason, "Bad credentials", "API should return reason 'Bad credentials'.");
    }

    @Test(groups = {"auth", "negative", "regression"}, description = "TC03 - Validate authentication failure with invalid username")
    @Story("User Authentication")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify POST /auth with invalid username and valid password. Restful Booker API quirk: returns HTTP 200 with reason 'Bad credentials'.")
    public void testTC03_InvalidUsernameAuthentication() {
        AuthRequest authRequest = new AuthRequest("invalid_user_xyz", ConfigReader.getPassword());

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body(authRequest)
                .when()
                .post(Endpoints.AUTH)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = response.jsonPath().getString("token");
        String reason = response.jsonPath().getString("reason");

        Assert.assertNull(token, "Token should be null for invalid username.");
        Assert.assertEquals(reason, "Bad credentials", "API should return reason 'Bad credentials'.");
    }

    @Test(groups = {"auth", "negative", "regression"}, description = "TC04 - Validate authentication failure with empty payload")
    @Story("User Authentication")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify POST /auth with an empty body {}. Restful Booker API quirk: returns HTTP 200 with reason 'Bad credentials'.")
    public void testTC04_EmptyPayloadAuthentication() {
        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body("{}")
                .when()
                .post(Endpoints.AUTH)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = response.jsonPath().getString("token");
        String reason = response.jsonPath().getString("reason");

        Assert.assertNull(token, "Token should be null for empty payload.");
        Assert.assertEquals(reason, "Bad credentials", "API should return reason 'Bad credentials'.");
    }
}
