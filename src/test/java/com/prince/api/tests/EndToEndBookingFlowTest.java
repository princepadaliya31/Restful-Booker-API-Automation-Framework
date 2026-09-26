package com.prince.api.tests;

import com.prince.api.auth.AuthManager;
import com.prince.api.base.BaseTest;
import com.prince.api.endpoints.Endpoints;
import com.prince.api.models.Booking;
import com.prince.api.models.BookingDates;
import com.prince.api.models.BookingResponse;
import com.prince.api.requests.RequestSpec;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("E2E Booking Lifecycle")
@Feature("Complete Stateful Booking Workflow")
public class EndToEndBookingFlowTest extends BaseTest {

    @Test(groups = {"e2e", "smoke", "regression"}, description = "TC19 - Complete End-to-End Booking Lifecycle")
    @Story("E2E Booking Flow")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Executes full lifecycle: Auth -> Create -> GET -> PUT -> GET (verify PUT) -> PATCH -> GET (verify PATCH) -> DELETE -> GET (verify 404).")
    public void testTC19_CompleteBookingLifecycle() {
        // Step 1: Authenticate and obtain token
        String token = stepAuthenticate();

        // Step 2: Create a new booking and capture ID
        Booking initialBooking = Booking.builder()
                .firstname("E2E_First")
                .lastname("E2E_Last")
                .totalprice(550)
                .depositpaid(true)
                .bookingdates(new BookingDates("2025-01-10", "2025-01-20"))
                .additionalneeds("Executive Suite")
                .build();
        int bookingId = stepCreateBooking(initialBooking);

        // Step 3: GET booking and verify creation
        stepGetBookingAndVerify(bookingId, "E2E_First", "E2E_Last");

        // Step 4: PUT booking (Full update)
        Booking putPayload = Booking.builder()
                .firstname("E2E_UpdatedFirst")
                .lastname("E2E_UpdatedLast")
                .totalprice(750)
                .depositpaid(false)
                .bookingdates(new BookingDates("2025-02-01", "2025-02-10"))
                .additionalneeds("Ocean View Suite")
                .build();
        stepPutBooking(bookingId, token, putPayload);

        // Step 5: GET booking and verify PUT persistence
        stepGetBookingAndVerify(bookingId, "E2E_UpdatedFirst", "E2E_UpdatedLast");

        // Step 6: PATCH booking (Partial update)
        Map<String, Object> patchPayload = new HashMap<>();
        patchPayload.put("firstname", "E2E_PatchedFirst");
        patchPayload.put("additionalneeds", "Presidential Suite");
        stepPatchBooking(bookingId, token, patchPayload);

        // Step 7: GET booking and verify PATCH persistence
        stepGetBookingAndVerify(bookingId, "E2E_PatchedFirst", "E2E_UpdatedLast");

        // Step 8: DELETE booking
        stepDeleteBooking(bookingId, token);

        // Step 9: GET deleted booking and verify 404 Not Found
        stepVerifyBookingNotFound(bookingId);
    }

    @Step("1. Authenticate and retrieve authentication token")
    private String stepAuthenticate() {
        String token = AuthManager.getAuthToken();
        Assert.assertNotNull(token, "Authentication token must be present.");
        return token;
    }

    @Step("2. Create a new booking dynamically")
    private int stepCreateBooking(Booking booking) {
        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body(booking)
                .when()
                .post(Endpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();

        BookingResponse bookingResponse = response.as(BookingResponse.class);
        Assert.assertNotNull(bookingResponse.getBookingid(), "Booking ID should be created.");
        return bookingResponse.getBookingid();
    }

    @Step("3. GET booking ID {bookingId} and verify first name = {expectedFirst} & last name = {expectedLast}")
    private void stepGetBookingAndVerify(int bookingId, String expectedFirst, String expectedLast) {
        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .when()
                .get(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Booking retrievedBooking = response.as(Booking.class);
        Assert.assertEquals(retrievedBooking.getFirstname(), expectedFirst, "First name should match expected.");
        Assert.assertEquals(retrievedBooking.getLastname(), expectedLast, "Last name should match expected.");
    }

    @Step("4. PUT full update for booking ID {bookingId}")
    private void stepPutBooking(int bookingId, String token, Booking putPayload) {
        RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", bookingId)
                .body(putPayload)
                .when()
                .put(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200);
    }

    @Step("6. PATCH partial update for booking ID {bookingId}")
    private void stepPatchBooking(int bookingId, String token, Map<String, Object> patchPayload) {
        RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", bookingId)
                .body(patchPayload)
                .when()
                .patch(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200);
    }

    @Step("8. DELETE booking ID {bookingId}")
    private void stepDeleteBooking(int bookingId, String token) {
        RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", bookingId)
                .when()
                .delete(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(201); // Restful Booker returns HTTP 201 Created on DELETE
    }

    @Step("9. Verify booking ID {bookingId} returns 404 Not Found after deletion")
    private void stepVerifyBookingNotFound(int bookingId) {
        RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .when()
                .get(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(404);
    }
}
