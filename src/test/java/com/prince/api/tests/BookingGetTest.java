package com.prince.api.tests;

import com.prince.api.base.BaseTest;
import com.prince.api.endpoints.Endpoints;
import com.prince.api.models.Booking;
import com.prince.api.requests.RequestSpec;
import com.prince.api.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

@Epic("Booking Management")
@Feature("Retrieve Booking (/booking)")
public class BookingGetTest extends BaseTest {

    @Test(groups = {"smoke", "booking", "regression"}, description = "TC08 - GET all booking IDs")
    @Story("Retrieve Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify GET /booking returns HTTP 200 and a non-empty list of booking IDs.")
    public void testTC08_GetAllBookingIds() {
        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .when()
                .get(Endpoints.BOOKING)
                .then()
                .statusCode(200)
                .contentType(Matchers.containsString("application/json"))
                .extract()
                .response();

        List<Map<String, Object>> bookingIds = response.jsonPath().getList("$");
        Assert.assertNotNull(bookingIds, "Booking IDs list should not be null.");
        Assert.assertFalse(bookingIds.isEmpty(), "Booking IDs list should not be empty.");
    }

    @Test(groups = {"booking", "regression"}, description = "TC09 - GET booking by dynamically created ID")
    @Story("Retrieve Booking")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify GET /booking/{id} retrieves the correct booking details for a dynamically created ID.")
    public void testTC09_GetBookingByDynamicId() {
        Booking customBooking = TestDataFactory.createBooking("Dynamic", "Getter", 220, true, "2024-11-01", "2024-11-05", "Quiet Room");
        int bookingId = createDynamicBookingId(customBooking);

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
        Assert.assertEquals(retrievedBooking.getFirstname(), customBooking.getFirstname());
        Assert.assertEquals(retrievedBooking.getLastname(), customBooking.getLastname());
        Assert.assertEquals(retrievedBooking.getTotalprice(), customBooking.getTotalprice());
        Assert.assertEquals(retrievedBooking.getDepositpaid(), customBooking.getDepositpaid());

        deleteBookingIfExists(bookingId);
    }

    @Test(groups = {"booking", "regression"}, description = "TC10 - Filter bookings using query parameters")
    @Story("Retrieve Booking")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify GET /booking?firstname=X&lastname=Y filters booking list correctly.")
    public void testTC10_GetBookingsWithQueryParams() {
        String uniqueFirstName = "FilterFirstName" + System.currentTimeMillis();
        String uniqueLastName = "FilterLastName" + System.currentTimeMillis();

        Booking customBooking = TestDataFactory.createBooking(uniqueFirstName, uniqueLastName, 180, true, "2024-12-01", "2024-12-05", "None");
        int createdId = createDynamicBookingId(customBooking);

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .queryParam("firstname", uniqueFirstName)
                .queryParam("lastname", uniqueLastName)
                .when()
                .get(Endpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<Integer> ids = response.jsonPath().getList("bookingid");
        Assert.assertNotNull(ids, "Filtered booking ID list should not be null.");
        Assert.assertTrue(ids.contains(createdId), "Filtered booking IDs must contain created booking ID: " + createdId);

        deleteBookingIfExists(createdId);
    }

    @Test(groups = {"booking", "regression"}, description = "TC11 - JSON Schema validation for booking response")
    @Story("Retrieve Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify GET /booking/{id} response matches booking JSON schema contract.")
    public void testTC11_BookingJsonSchemaValidation() {
        int bookingId = createDynamicBookingId();

        RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .when()
                .get(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/booking-schema.json"));

        deleteBookingIfExists(bookingId);
    }

    @Test(groups = {"booking", "regression"}, description = "TC12 - Response time SLA validation for GET booking")
    @Story("Retrieve Booking")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify GET /booking/{id} responds within the configured SLA threshold.")
    public void testTC12_BookingResponseSlaValidation() {
        int bookingId = createDynamicBookingId();

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .when()
                .get(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .time(Matchers.lessThan(slaThresholdMs))
                .extract()
                .response();

        long responseTime = response.getTime();
        Assert.assertTrue(responseTime < slaThresholdMs, "Response time " + responseTime + "ms exceeded SLA limit of " + slaThresholdMs + "ms");

        deleteBookingIfExists(bookingId);
    }
}
