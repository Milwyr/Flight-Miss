package com.flight.miss.chatbotAPI.JsonObjects;

import com.flight.miss.models.FlightInfoRow;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Me on 10/23/2016.
 */
public class OptionParser {
    public boolean hasOptions = false;
    public boolean hasBookingOptions = false;
    public String message;
    public String[] options;
    public List<FlightInfoRow> bookingOptions;

    public OptionParser(String text) {
        if (!testNumberedList(text)) {
            if (!testParentheticList(text)) {
                if (!testQuestionMark(text)) {
                    hasOptions = false;
                    return;
                }
            }
        }

        testBookingOptions();
    }

    private boolean testNumberedList(String text) {
        String[] lines = text.split("\n");
        Pattern p = Pattern.compile("(   [\\d]+\\. )(.*)");

        List<String> foundOptions = new ArrayList<>();
        String message = "";
        int count = 0;
        for (String line : lines) {
            if (p.matcher(line).matches()) {
                count++;
                int dotIndex = line.indexOf('.');
                int digit = Integer.parseInt(line.substring(0, dotIndex).trim());
                String option = line.substring(dotIndex + 2);
                foundOptions.add(option);
                if (count != digit) return false;
            } else {
                message += line;
            }
        }

        if (count > 2) {
            hasOptions = true;
            this.message = message;
            options = foundOptions.toArray(new String[foundOptions.size()]);
            return true;
        }

        return false;
    }


    private boolean testParentheticList(String text) {
        if (text.matches("(.|\\n)*\\(1\\. (.*) or 2\\. (.*)\\)")) {
            hasOptions = true;
            int optionIndex = text.lastIndexOf("(1. ");
            message = text.substring(0, optionIndex);
            int splitterIndex = text.lastIndexOf(" or 2. ");
            options = new String[2];
            options[0] = text.substring(optionIndex + 4, splitterIndex);
            options[1] = text.substring(splitterIndex + 7, text.length() - 1);
            return true;
        }

        return false;
    }

    private boolean testQuestionMark(String text) {
        if (text.charAt(text.length() - 1) == '?') {
            hasOptions = true;
            message = text;
            options = new String[2];
            options[0] = "Yes";
            options[1] = "No";
            return true;
        }

        return false;
    }

    private boolean testBookingOptions() {
        List<FlightInfoRow> books = new ArrayList<>();
        for (String s : options) {
            String[] parts = s.split(" ");
            if (parts.length != 4) return false;
            if (!parts[2].contains(":00Z")) return false;
            if (!parts[3].contains(":00Z")) return false;

            books.add(new FlightInfoRow(s));
        }

        bookingOptions = books;

        return true;
    }
}
