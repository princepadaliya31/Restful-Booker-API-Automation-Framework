package com.prince.api.tests;

import com.prince.api.auth.AuthManager;
import com.prince.api.base.BaseTest;
import com.prince.api.endpoints.Endpoints;
import com.prince.api.requests.RequestSpec;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Booking Management")
@Feature("Delete Booking (/booking)")
public class BookingDeleteTest extends BaseTest {

    @Test(groups = {"smoke", "booking", "regression"}, description = "TC17 - Delete dynamically created booking using authentication")
    @Story("Delete Booking")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify DELETE /booking/{id} with valid token deletes the resource. Restful Booker API quirk: returns HTTP 201 Created.")
    public void testTC17_DeleteBookingWithAuth() {
        int bookingId = createDynamicBookingId();
        String token = AuthManager.getAuthToken();

        Response response = RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", bookingId)
                .when()
                .delete(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(201)
                .extract()
                .response();

        Assert.assertEquals(response.getStatusCode(), 201, "Restful Booker returns HTTP 201 Created upon successful deletion.");
    }

    @Test(groups = {"booking", "regression"}, description = "TC18 - Verify deleted booking can no longer be retrieved")
    @Story("Delete Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify GET /booking/{id} returns HTTP 404 Not Found after deleting the booking.")
    public void testTC18_VerifyDeletedBookingNotFound() {
        int bookingId = createDynamicBookingId();
        String token = AuthManager.getAuthToken();

        // Delete booking
        RestAssured.given()
                .spec(RequestSpec.getAuthRequestSpec(token))
                .pathParam("id", bookingId)
                .when()
                .delete(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(201);

        // Verify GET returns 404
        RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .pathParam("id", bookingId)
                .when()
                .get(Endpoints.BOOKING_BY_ID)
                .then()
                .statusCode(404);
    }
}
