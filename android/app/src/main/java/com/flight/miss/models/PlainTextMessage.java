package com.flight.miss.models;

/**
 * Created by Milton on 22/10/2016.
 */
public class PlainTextMessage extends ChatBotMessage {
    private String text;

    public PlainTextMessage(String text, boolean isSentFromDevice) {
        super(isSentFromDevice);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}