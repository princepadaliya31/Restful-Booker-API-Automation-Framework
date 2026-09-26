package com.prince.api.utils;

import com.prince.api.models.Booking;
import com.prince.api.models.BookingDates;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class generating reusable test data payloads for API tests.
 */
public class TestDataFactory {

    private TestDataFactory() {
        // Prevent instantiation
    }

    public static Booking createValidBooking() {
        return Booking.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(new BookingDates("2024-05-01", "2024-05-10"))
                .additionalneeds("Breakfast")
                .build();
    }

    public static Booking createBooking(String firstName, String lastName, int price, boolean depositPaid,
                                        String checkin, String checkout, String additionalNeeds) {
        return Booking.builder()
                .firstname(firstName)
                .lastname(lastName)
                .totalprice(price)
                .depositpaid(depositPaid)
                .bookingdates(new BookingDates(checkin, checkout))
                .additionalneeds(additionalNeeds)
                .build();
    }

    public static Booking createUpdatedBooking() {
        return Booking.builder()
                .firstname("James")
                .lastname("Bond")
                .totalprice(500)
                .depositpaid(false)
                .bookingdates(new BookingDates("2024-06-01", "2024-06-15"))
                .additionalneeds("Late Checkout & Extra Towels")
                .build();
    }

    public static Map<String, Object> createPartialUpdatePayload(String firstName, String additionalNeeds) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("firstname", firstName);
        payload.put("additionalneeds", additionalNeeds);
        return payload;
    }

    public static Map<String, Object> createPartialUpdatePricePayload(int price, boolean depositPaid) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("totalprice", price);
        payload.put("depositpaid", depositPaid);
        return payload;
    }
}
