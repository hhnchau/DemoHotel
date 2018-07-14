package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 8/16/2016.
 */
public class LogoutDto {
    private String mobileUserId;

    public LogoutDto(String mobileUserId){
        this.mobileUserId = mobileUserId;
    }

    public String getMobileUserId() {
        return mobileUserId;
    }

    public void setMobileUserId(String mobileUserId) {
        this.mobileUserId = mobileUserId;
    }
}
