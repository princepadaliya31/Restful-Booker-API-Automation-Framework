package com.prince.api.tests;

import com.prince.api.base.BaseTest;
import com.prince.api.endpoints.Endpoints;
import com.prince.api.models.Booking;
import com.prince.api.models.BookingResponse;
import com.prince.api.requests.RequestSpec;
import com.prince.api.utils.JsonUtils;
import com.prince.api.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Epic("Booking Management")
@Feature("Booking Creation (/booking)")
public class BookingCreateTest extends BaseTest {

    @Test(groups = {"smoke", "booking", "regression"}, description = "TC05 - Create booking using Java POJO with Builder pattern")
    @Story("Create Booking")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify POST /booking successfully creates a new booking using a POJO payload.")
    public void testTC05_CreateBookingUsingPojo() {
        Booking requestBooking = TestDataFactory.createValidBooking();

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body(requestBooking)
                .when()
                .post(Endpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();

        BookingResponse bookingResponse = response.as(BookingResponse.class);

        Assert.assertNotNull(bookingResponse.getBookingid(), "Booking ID should not be null.");
        Assert.assertTrue(bookingResponse.getBookingid() > 0, "Booking ID should be greater than 0.");
        Assert.assertEquals(bookingResponse.getBooking().getFirstname(), requestBooking.getFirstname());
        Assert.assertEquals(bookingResponse.getBooking().getLastname(), requestBooking.getLastname());
        Assert.assertEquals(bookingResponse.getBooking().getTotalprice(), requestBooking.getTotalprice());
        Assert.assertEquals(bookingResponse.getBooking().getDepositpaid(), requestBooking.getDepositpaid());
        Assert.assertEquals(bookingResponse.getBooking().getBookingdates().getCheckin(), requestBooking.getBookingdates().getCheckin());
        Assert.assertEquals(bookingResponse.getBooking().getBookingdates().getCheckout(), requestBooking.getBookingdates().getCheckout());
        Assert.assertEquals(bookingResponse.getBooking().getAdditionalneeds(), requestBooking.getAdditionalneeds());

        // Cleanup
        deleteBookingIfExists(bookingResponse.getBookingid());
    }

    @Test(groups = {"booking", "regression"}, description = "TC06 - Create booking using external JSON file payload")
    @Story("Create Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify POST /booking creates a booking when payload is loaded from external JSON resource file.")
    public void testTC06_CreateBookingUsingExternalJson() {
        Booking requestBooking = JsonUtils.readJsonFromClasspath("testdata/booking.json", Booking.class);

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body(requestBooking)
                .when()
                .post(Endpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();

        BookingResponse bookingResponse = response.as(BookingResponse.class);

        Assert.assertNotNull(bookingResponse.getBookingid(), "Booking ID should be returned.");
        Assert.assertEquals(bookingResponse.getBooking().getFirstname(), requestBooking.getFirstname());
        Assert.assertEquals(bookingResponse.getBooking().getLastname(), requestBooking.getLastname());

        deleteBookingIfExists(bookingResponse.getBookingid());
    }

    @DataProvider(name = "bookingDataProvider")
    public Object[][] getBookingData() {
        return new Object[][]{
                {"Alice", "Smith", 150, true, "2024-07-01", "2024-07-05", "Lunch"},
                {"Bob", "Johnson", 300, false, "2024-08-10", "2024-08-15", "Dinner"},
                {"Charlie", "Davis", 450, true, "2024-09-20", "2024-09-25", "Airport Shuttle"}
        };
    }

    @Test(groups = {"booking", "regression"}, dataProvider = "bookingDataProvider", description = "TC07 - Data-driven booking creation using TestNG DataProvider")
    @Story("Create Booking")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify POST /booking creates valid bookings across multiple datasets provided by DataProvider.")
    public void testTC07_CreateBookingDataDriven(String firstName, String lastName, int price, boolean depositPaid,
                                                  String checkin, String checkout, String additionalNeeds) {
        Booking requestBooking = TestDataFactory.createBooking(firstName, lastName, price, depositPaid, checkin, checkout, additionalNeeds);

        Response response = RestAssured.given()
                .spec(RequestSpec.getRequestSpec())
                .body(requestBooking)
                .when()
                .post(Endpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();

        BookingResponse bookingResponse = response.as(BookingResponse.class);

        Assert.assertNotNull(bookingResponse.getBookingid(), "Booking ID should be created.");
        Assert.assertEquals(bookingResponse.getBooking().getFirstname(), firstName);
        Assert.assertEquals(bookingResponse.getBooking().getLastname(), lastName);
        Assert.assertEquals(bookingResponse.getBooking().getTotalprice(), (Integer) price);
        Assert.assertEquals(bookingResponse.getBooking().getDepositpaid(), (Boolean) depositPaid);

        deleteBookingIfExists(bookingResponse.getBookingid());
    }
}
