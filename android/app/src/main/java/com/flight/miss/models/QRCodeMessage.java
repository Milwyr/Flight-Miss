package com.flight.miss.models;

import android.graphics.Bitmap;

/**
 * Created by Milton on 23/10/2016.
 */
public class QRCodeMessage extends ChatBotMessage {
    private Bitmap bitmap;
    private FlightInfoMessage flightInfoMessage;

    public QRCodeMessage(Bitmap bitmap, FlightInfoMessage flightInfoMessage, boolean isSentFromDevice) {
        super(isSentFromDevice);
        this.bitmap = bitmap;
        this.flightInfoMessage = flightInfoMessage;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    // Null if this is a voucher
    public FlightInfoMessage getFlightInfoMessage() {
        return this.flightInfoMessage;
    }
}