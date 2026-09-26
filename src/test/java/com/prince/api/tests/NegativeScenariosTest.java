package com.prince.api.tests;

import com.prince.api.auth.AuthManager;
import com.prince.api.base.BaseTest;
import com.prince.api.endpoints.Endpoints;
import com.prince.api.models.Booking;
import com.prince.api.requests.RequestSpec;
import com.prince.api.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

@Epic("Negative Testing & Boundary Scenarios")
@Feature("Error Handling & API Resilience")
public class NegativeScenariosTest extends BaseTest {

    @Test(groups = {"negative", "regression"}, description = "TC20 - GET non-existing booking ID")
    @Story("Negative Retrievals")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify GET /booking/999999999 returns HTTP 404 Not Found.")
    public void testTC20_GetNonExistingBooking() {
        RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", 999999999)
                .when()
                .get(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(404);
    }

    @Test(groups = {"negative", "regression"}, description = "TC21 - PUT update without authentication token")
    @Story("Unauthorized Mutations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify PUT /booking/{id} without auth headers returns HTTP 403 Forbidden.")
    public void testTC21_PutWithoutAuthentication() {
        int bookingId = createDynamicBookingId();
        Booking updatePayload = TestDataFactory.createUpdatedBooking();

        RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .body(updatePayload)
                .when()
                .put(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(403);

        deleteBookingIfExists(bookingId);
    }

    @Test(groups = {"negative", "regression"}, description = "TC22 - PUT update with invalid authentication token")
    @Story("Unauthorized Mutations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify PUT /booking/{id} with invalid token returns HTTP 403 Forbidden.")
    public void testTC22_PutWithInvalidAuthentication() {
        int bookingId = createDynamicBookingId();
        Booking updatePayload = TestDataFactory.createUpdatedBooking();

        RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec("invalid_auth_token_999"))
                .pathParam("id", bookingId)
                .body(updatePayload)
                .when()
                .put(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(403);

        deleteBookingIfExists(bookingId);
    }

    @Test(groups = {"negative", "regression"}, description = "TC23 - PATCH partial update without authentication token")
    @Story("Unauthorized Mutations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify PATCH /booking/{id} without auth headers returns HTTP 403 Forbidden.")
    public void testTC23_PatchWithoutAuthentication() {
        int bookingId = createDynamicBookingId();

        RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .body(TestDataFactory.createPartialUpdatePayload("Unauthorized", "Test"))
                .when()
                .patch(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(403);

        deleteBookingIfExists(bookingId);
    }

    @Test(groups = {"negative", "regression"}, description = "TC24 - DELETE booking without authentication token")
    @Story("Unauthorized Mutations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify DELETE /booking/{id} without auth headers returns HTTP 403 Forbidden.")
    public void testTC24_DeleteWithoutAuthentication() {
        int bookingId = createDynamicBookingId();

        RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .when()
                .delete(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(403);

        deleteBookingIfExists(bookingId);
    }

    @Test(groups = {"negative", "regression"}, description = "TC25 - Unsupported HTTP method on booking item endpoint")
    @Story("Invalid Endpoint Requests")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify POST to /booking/{id} returns HTTP 404 or 405. Restful Booker returns HTTP 404 for POST /booking/{id}.")
    public void testTC25_UnsupportedHttpMethod() {
        int bookingId = createDynamicBookingId();

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .body(TestDataFactory.createValidBooking())
                .when()
                .post(Endpoints.BOOKING_BY_ID)
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 404 || statusCode == 405, "Expected HTTP 404 or 405 for unsupported POST on item endpoint, got: " + statusCode);

        deleteBookingIfExists(bookingId);
    }

    @Test(groups = {"negative", "regression"}, description = "TC26 - Malformed JSON request body")
    @Story("Invalid Payloads")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify POST /booking with malformed JSON body returns client/server error code (400 or 500).")
    public void testTC26_MalformedJsonRequestBody() {
        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body("{\"firstname\": \"John\", \"lastname\": }")
                .when()
                .post(Endpoints.BOOKING)
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 400 || statusCode == 500, "Expected HTTP 400 or 500 for malformed JSON, got: " + statusCode);
    }

    @Test(groups = {"negative", "regression"}, description = "TC27 - Invalid path parameter (string booking ID)")
    @Story("Invalid Endpoint Requests")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify GET /booking/invalid_string_id returns HTTP 404 Not Found.")
    public void testTC27_InvalidPathBookingId() {
        RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", "invalid_string_id")
                .when()
                .get(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(404);
    }

    @Test(groups = {"negative", "regression"}, description = "TC28 - Missing required fields in booking creation payload")
    @Story("Invalid Payloads")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify POST /booking with empty JSON object returns HTTP 500 Internal Server Error (Restful Booker API quirk).")
    public void testTC28_MissingRequiredFieldsPayload() {
        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body(new HashMap<>())
                .when()
                .post(Endpoints.BOOKING)
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 500 || statusCode == 400, "Restful Booker returns HTTP 500 for missing required body attributes, got: " + statusCode);
    }

    @Test(groups = {"negative", "regression"}, description = "TC29 - DELETE non-existing booking ID")
    @Story("Negative Mutations")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify DELETE /booking/999999999 with valid token returns HTTP 405 Method Not Allowed (Restful Booker API quirk).")
    public void testTC29_DeleteNonExistingBooking() {
        String token = AuthManager.getAuthToken();

        Response response = RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", 999999999)
                .when()
                .delete(Endpoints.BOOKING_BY_ID)
                .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == 405 || statusCode == 404, "Restful Booker returns HTTP 405/404 when deleting non-existent ID, got: " + statusCode);
    }

    @Test(groups = {"smoke", "negative", "regression"}, description = "TC30 - Health check /ping endpoint")
    @Story("Health Check")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify GET /ping returns HTTP 201 Created (Restful Booker API quirk).")
    public void testTC30_PingHealthCheck() {
        RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .when()
                .get(Endpoints.PING)
                .then()
                .statusCode(201)
                .body(Matchers.equalTo("Created"));
    }
}
