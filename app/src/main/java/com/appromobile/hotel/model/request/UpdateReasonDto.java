package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 12/16/2016.
 */

public class UpdateReasonDto {
    private int reasonNotCheckin;
    private int userBookingSn;

    public int getReasonNotCheckin() {
        return reasonNotCheckin;
    }

    public void setReasonNotCheckin(int reasonNotCheckin) {
        this.reasonNotCheckin = reasonNotCheckin;
    }

    public int getUserBookingSn() {
        return userBookingSn;
    }

    public void setUserBookingSn(int userBookingSn) {
        this.userBookingSn = userBookingSn;
    }
}
