package com.flight.miss.chatbotAPI.JsonObjects;

/**
 * Created by Me on 10/23/2016.
 */
public class Message {
    public String id;
    public String text;
    public String from;

    public Message(String text) {
        this.text = text;
    }
}
