package com.prince.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Data model for Booking Dates object.
 */
public class BookingDates {

    @JsonProperty("checkin")
    private String checkin;

    @JsonProperty("checkout")
    private String checkout;

    public BookingDates() {
    }

    public BookingDates(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDates that = (BookingDates) o;
        return Objects.equals(checkin, that.checkin) &&
               Objects.equals(checkout, that.checkout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkin, checkout);
    }

    @Override
    public String toString() {
        return "BookingDates{" +
                "checkin='" + checkin + '\'' +
                ", checkout='" + checkout + '\'' +
                '}';
    }
}
