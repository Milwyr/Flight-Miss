package com.flight.miss.chatbotAPI.JsonObjects;

import com.flight.miss.models.FlightInfoRow;

/**
 * Created by Me on 10/23/2016.
 */
public class EntityParser {
    public boolean isEntity;
    public int entityType;
    public String message;

    public FlightInfoRow boardingInfo;

    public static int FOOD_VOUCHER = 1;
    public static int BOARDING_PASS = 2;

    public EntityParser(String text) {
        if (text.toLowerCase().contains("boarding pass")) {
            isEntity = true;
            entityType = BOARDING_PASS;
            int start = text.indexOf(": ");
            int end = text.indexOf("\n", start);

            boardingInfo = new FlightInfoRow(text.substring(start + 2, end));
            message = text.substring(0, start) + text.substring(end);
        } else if (text.toLowerCase().contains("voucher")) {
            isEntity = true;
            entityType = FOOD_VOUCHER;
            message = text;
        } else {
            isEntity = false;
        }
    }
}
