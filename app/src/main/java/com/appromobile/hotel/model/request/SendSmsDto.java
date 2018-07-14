package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 12/5/2016.
 */

public class SendSmsDto {
    private String mobile;
    private String mobileUserId;

    public String getMobileUserId() {
        return mobileUserId;
    }

    public void setMobileUserId(String mobileUserId) {
        this.mobileUserId = mobileUserId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
