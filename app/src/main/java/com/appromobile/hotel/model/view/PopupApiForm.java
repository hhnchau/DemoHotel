package com.appromobile.hotel.model.view;

/**
 * Created by xuan on 12/16/2016.
 */

public class PopupApiForm {
    private LastBookingForm lastBooking;
    private LastBookingForm lastCheckin;
    private PopupForm popup;

    public LastBookingForm getLastBooking() {
        return lastBooking;
    }

    public void setLastBooking(LastBookingForm lastBooking) {
        this.lastBooking = lastBooking;
    }

    public LastBookingForm getLastCheckin() {
        return lastCheckin;
    }

    public void setLastCheckin(LastBookingForm lastCheckin) {
        this.lastCheckin = lastCheckin;
    }

    public PopupForm getPopup() {
        return popup;
    }

    public void setPopup(PopupForm popup) {
        this.popup = popup;
    }
}
