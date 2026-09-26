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

import java.util.Map;

@Epic("Booking Management")
@Feature("Patch Booking (/booking)")
public class BookingPatchTest extends BaseTest {

    @Test(groups = {"smoke", "booking", "regression"}, description = "TC15 - Partial update firstname and additionalneeds using PATCH")
    @Story("Patch Booking")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify PATCH /booking/{id} partially updates firstname and additionalneeds while preserving unrelated fields.")
    public void testTC15_PartialUpdateFirstnameAndNeeds() {
        Booking initialBooking = TestDataFactory.createValidBooking();
        int bookingId = createDynamicBookingId(initialBooking);
        String token = AuthManager.getAuthToken();

        Map<String, Object> patchPayload = TestDataFactory.createPartialUpdatePayload("PatchedFirst", "Spa Access");

        Response response = RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", bookingId)
                .body(patchPayload)
                .when()
                .patch(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Booking patchedBooking = response.as(Booking.class);
        Assert.assertEquals(patchedBooking.getFirstname(), "PatchedFirst", "Firstname should be updated.");
        Assert.assertEquals(patchedBooking.getAdditionalneeds(), "Spa Access", "Additional needs should be updated.");
        Assert.assertEquals(patchedBooking.getLastname(), initialBooking.getLastname(), "Lastname should remain unchanged.");
        Assert.assertEquals(patchedBooking.getTotalprice(), initialBooking.getTotalprice(), "Total price should remain unchanged.");

        deleteBookingIfExists(bookingId);
    }

    @Test(groups = {"booking", "regression"}, description = "TC16 - Partial update totalprice and depositpaid using PATCH")
    @Story("Patch Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify PATCH /booking/{id} partially updates totalprice and depositpaid while preserving original names and dates.")
    public void testTC16_PartialUpdatePriceAndDeposit() {
        Booking initialBooking = TestDataFactory.createValidBooking();
        int bookingId = createDynamicBookingId(initialBooking);
        String token = AuthManager.getAuthToken();

        Map<String, Object> patchPayload = TestDataFactory.createPartialUpdatePricePayload(888, false);

        Response response = RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", bookingId)
                .body(patchPayload)
                .when()
                .patch(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Booking patchedBooking = response.as(Booking.class);
        Assert.assertEquals(patchedBooking.getTotalprice(), (Integer) 888, "Total price should be updated.");
        Assert.assertEquals(patchedBooking.getDepositpaid(), Boolean.FALSE, "Deposit paid should be updated.");
        Assert.assertEquals(patchedBooking.getFirstname(), initialBooking.getFirstname(), "Firstname should remain unchanged.");
        Assert.assertEquals(patchedBooking.getLastname(), initialBooking.getLastname(), "Lastname should remain unchanged.");

        deleteBookingIfExists(bookingId);
    }
}
