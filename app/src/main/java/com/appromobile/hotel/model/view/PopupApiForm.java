package com.appromobile.hotel.model.view;

import java.util.List;

/**
 * Created by xuan on 12/16/2016.
 */

public class PopupApiForm {
    private LastBookingForm lastBooking;
    private LastBookingForm lastCheckin;
    private PopupForm popup;
    private List<PopupForm> popupList;

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

    public List<PopupForm> getPopupList() {
        return popupList;
    }

    public void setPopupList(List<PopupForm> popupList) {
        this.popupList = popupList;
    }
}
