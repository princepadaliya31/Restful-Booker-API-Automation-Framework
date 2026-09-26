package com.prince.api.endpoints;

/**
 * Centralized registry of REST API endpoints for Restful Booker service.
 */
public final class Endpoints {

    private Endpoints() {
        // Prevent instantiation
    }

    public static final String AUTH = "/auth";
    public static final String BOOKING = "/booking";
    public static final String BOOKING_BY_ID = "/booking/{id}";
    public static final String PING = "/ping";
}
