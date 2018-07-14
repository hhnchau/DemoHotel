package com.appromobile.hotel.model.view;

/**
 * Created by appro on 25/12/2017.
 */

public class StampIssuedForm {
    private int sn;

    private int hotelSn;

    private int appUserSn;

    private int userBookingSn;

    private int useForBookingSn;

    private String expireDate;

    private int status; //0:Expire, 1:Active, 2:Used

    public StampIssuedForm() {
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
    }

    public int getAppUserSn() {
        return appUserSn;
    }

    public void setAppUserSn(int appUserSn) {
        this.appUserSn = appUserSn;
    }

    public int getUserBookingSn() {
        return userBookingSn;
    }

    public void setUserBookingSn(int userBookingSn) {
        this.userBookingSn = userBookingSn;
    }

    public int getUseForBookingSn() {
        return useForBookingSn;
    }

    public void setUseForBookingSn(int useForBookingSn) {
        this.useForBookingSn = useForBookingSn;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
