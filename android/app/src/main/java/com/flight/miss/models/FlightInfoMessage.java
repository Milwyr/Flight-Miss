package com.flight.miss.models;

import java.util.List;

/**
 * Created by Milton on 23/10/2016.
 */
public class FlightInfoMessage extends ChatBotMessage {
    private String title;
    private int[] images;
    private List<FlightInfoRow> flightInfoRows;

    public FlightInfoMessage(String title, int[] images,
                             List<FlightInfoRow> flightInfoRows, boolean isSentFromDevice) {
        super(isSentFromDevice);
        this.title = title;
        this.images = images;
        this.flightInfoRows = flightInfoRows;
    }

    public String getTitle() {
        return this.title;
    }

    public int[] getImages() {
        return this.images;
    }

    public List<FlightInfoRow> getFlightInfoRows() {
        return this.flightInfoRows;
    }
}