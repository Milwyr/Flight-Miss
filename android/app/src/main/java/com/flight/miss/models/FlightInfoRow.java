package com.flight.miss.models;

import org.joda.time.LocalTime;

/**
 * Created by Milton on 22/10/2016.
 */

public class FlightInfoRow {
    private String company;
    private String flightNumber;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    public FlightInfoRow(String company, String flightNumber,
                         LocalTime departureTime, LocalTime arrivalTime) {
        this.company = company;
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public String getCompany() {
        return company;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
}