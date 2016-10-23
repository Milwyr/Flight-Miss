package com.flight.miss.models;

import org.joda.time.LocalTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Milton on 22/10/2016.
 */

public class FlightInfoRow {
    private String company;
    private String flightNumber;
    private String departureTime;
    private String arrivalTime;

    public FlightInfoRow(String company, String flightNumber,
                         String departureTime, String arrivalTime) {
        this.company = company;
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public FlightInfoRow(String text) {
        String[] fields = text.split(" ");
        company = fields[0];
        flightNumber = fields[1];
        departureTime = parseDate(fields[2]);
        arrivalTime = parseDate(fields[3]);
    }

    public static String parseDate(String date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD'T'HH:MM:SSXXX", Locale.US);
            Date converted = simpleDateFormat.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);
            String time = formatter.format(converted);

            Calendar c = Calendar.getInstance();
            c.setTime(converted);
            int departureDate = c.get(Calendar.DATE);
            c = Calendar.getInstance();
            int currentDate = c.get(Calendar.DATE);
            if (departureDate != currentDate) time += " (+1)";

            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "ERROR";
    }

    @Override
    public String toString() {
        return company + " " + flightNumber + " " + departureTime + " " + arrivalTime;
    }

    public String getCompany() {
        return company;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }
}