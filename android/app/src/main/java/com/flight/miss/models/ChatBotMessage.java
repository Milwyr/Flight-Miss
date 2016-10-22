package com.flight.miss.models;

/**
 * Created by Milton on 22/10/2016.
 */

public abstract class ChatBotMessage {
    private boolean isSentFromDevice;

    public ChatBotMessage(boolean isSentFromDevice) {
        this.isSentFromDevice = isSentFromDevice;
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
