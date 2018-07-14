package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 9/14/2016.
 */
public class PushNotificationDto {
    private String mobileUserId;
    private boolean push;

    public String getMobileUserId() {
        return mobileUserId;
    }

    public void setMobileUserId(String mobileUserId) {
        this.mobileUserId = mobileUserId;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }
}
