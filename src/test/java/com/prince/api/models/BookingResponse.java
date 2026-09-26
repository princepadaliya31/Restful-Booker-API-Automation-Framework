package com.prince.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data model for Booking creation response payload.
 */
public class BookingResponse {

    @JsonProperty("bookingid")
    private Integer bookingid;

    @JsonProperty("booking")
    private Booking booking;

    public BookingResponse() {
    }

    public BookingResponse(Integer bookingid, Booking booking) {
        this.bookingid = bookingid;
        this.booking = booking;
    }

    public Integer getBookingid() {
        return bookingid;
    }

    public void setBookingid(Integer bookingid) {
        this.bookingid = bookingid;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public String toString() {
        return "BookingResponse{" +
                "bookingid=" + bookingid +
                ", booking=" + booking +
                '}';
    }
}
