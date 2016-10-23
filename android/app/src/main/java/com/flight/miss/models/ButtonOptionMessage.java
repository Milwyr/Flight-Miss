package com.flight.miss.models;

/**
 * Created by Me on 10/23/2016.
 */
public class ButtonOptionMessage extends PlainTextMessage {
    public String[] options;

    public ButtonOptionMessage(String message, String[] options) {
        super(message, false);
        this.options = options;
    }
}
