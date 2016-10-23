package com.flight.miss.models;

import java.util.List;

/**
 * A message that consists of a title and a list of flight information.
 */
public class FlightInfoMessage extends ChatBotMessage {
    private String title;
    private List<FlightInfoRow> flightInfoRows;

    public FlightInfoMessage(String title, List<FlightInfoRow> flightInfoRows, boolean isSentFromDevice) {
        super(isSentFromDevice);
        this.title = title;
        this.flightInfoRows = flightInfoRows;
    }

    public String getTitle() {
        return this.title;
    }

    public List<FlightInfoRow> getFlightInfoRows() {
        return this.flightInfoRows;
    }
}