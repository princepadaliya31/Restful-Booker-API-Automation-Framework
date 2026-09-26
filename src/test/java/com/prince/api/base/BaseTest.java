package com.prince.api.base;

import com.prince.api.auth.AuthManager;
import com.prince.api.config.ConfigReader;
import com.prince.api.endpoints.Endpoints;
import com.prince.api.models.Booking;
import com.prince.api.models.BookingResponse;
import com.prince.api.requests.RequestSpec;
import com.prince.api.utils.TestDataFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeSuite;

/**
 * Base test class establishing common execution context, configuration, and shared API helpers.
 */
public abstract class BaseTest {

    protected static long slaThresholdMs;

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        slaThresholdMs = ConfigReader.getSlaResponseTime();
    }

    /**
     * Helper method to dynamically create a booking and return the created booking ID.
     */
    protected int createDynamicBookingId() {
        Booking booking = TestDataFactory.createValidBooking();
        return createDynamicBookingId(booking);
    }

    /**
     * Helper method to dynamically create a booking from a custom payload and return booking ID.
     */
    protected int createDynamicBookingId(Booking booking) {
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
        return bookingResponse.getBookingid();
    }

    /**
     * Helper method to delete a booking by ID.
     */
    protected void deleteBookingIfExists(int bookingId) {
        try {
            String token = AuthManager.getAuthToken();
            RestAssured.given()
                    .spec(RequestSpec.getAuthRequestSpec(token))
                    .pathParam("id", bookingId)
                    .when()
                    .delete(Endpoints.BOOKING_BY_ID);
        } catch (Exception ignored) {
            // Cleanup helper shouldn't fail test execution if record was already deleted
        }
    }
}
