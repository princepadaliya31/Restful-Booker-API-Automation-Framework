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
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Booking Management")
@Feature("Update Booking (/booking)")
public class BookingUpdateTest extends BaseTest {

    @Test(groups = {"smoke", "booking", "regression"}, description = "TC13 - Full booking update using PUT")
    @Story("Update Booking")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify PUT /booking/{id} updates all fields of an existing booking with valid authentication.")
    public void testTC13_FullBookingUpdatePut() {
        int bookingId = createDynamicBookingId();
        String token = AuthManager.getAuthToken();
        Booking updatePayload = TestDataFactory.createUpdatedBooking();

        Response response = RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", bookingId)
                .body(updatePayload)
                .when()
                .put(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Booking updatedBooking = response.as(Booking.class);
        Assert.assertEquals(updatedBooking.getFirstname(), updatePayload.getFirstname());
        Assert.assertEquals(updatedBooking.getLastname(), updatePayload.getLastname());
        Assert.assertEquals(updatedBooking.getTotalprice(), updatePayload.getTotalprice());
        Assert.assertEquals(updatedBooking.getDepositpaid(), updatePayload.getDepositpaid());
        Assert.assertEquals(updatedBooking.getAdditionalneeds(), updatePayload.getAdditionalneeds());

        deleteBookingIfExists(bookingId);
    }

    @Test(groups = {"booking", "regression"}, description = "TC14 - Verify PUT changes persisted using subsequent GET")
    @Story("Update Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that changes submitted via PUT are persisted when subsequently queried via GET /booking/{id}.")
    public void testTC14_VerifyPutChangesPersisted() {
        int bookingId = createDynamicBookingId();
        String token = AuthManager.getAuthToken();
        Booking updatePayload = TestDataFactory.createUpdatedBooking();

        // Perform PUT
        RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", bookingId)
                .body(updatePayload)
                .when()
                .put(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200);

        // Perform GET to verify persistence
        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .when()
                .get(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Booking persistedBooking = response.as(Booking.class);
        Assert.assertEquals(persistedBooking.getFirstname(), updatePayload.getFirstname());
        Assert.assertEquals(persistedBooking.getLastname(), updatePayload.getLastname());
        Assert.assertEquals(persistedBooking.getTotalprice(), updatePayload.getTotalprice());
        Assert.assertEquals(persistedBooking.getDepositpaid(), updatePayload.getDepositpaid());

        deleteBookingIfExists(bookingId);
    }
}
