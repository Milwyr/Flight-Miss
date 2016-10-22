package com.flight.miss;

/**
 * Created by Milton on 22/10/2016.
 */
public class ChatBotMessage {
    private String text;
    private boolean isSentFromDevice;

    public ChatBotMessage(String text, boolean isSentFromDevice) {
        this.text = text;
        this.isSentFromDevice = isSentFromDevice;
    }

    public String getText() {
        return this.text;
    }

    /**
     * A flag that indicates whether this text is sent from device.
     *
     * @return True if this text is sent from device, else it is sent from server
     */
    public boolean getIsSentFromDevice() {
        return this.isSentFromDevice;
    }
}