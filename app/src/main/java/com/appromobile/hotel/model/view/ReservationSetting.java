package com.appromobile.hotel.model.view;

/**
 * Created by thanh on 3/24/2017.
 */

public class ReservationSetting {
//    checkOutOneday (integer): Time of checkout for one day. Unit is hour ,
//    endOvernight (integer): End time of overnight. Unit is hour ,
//    startOvernight (integer): Start time of overnight. Unit is hour

    private int checkOutOneday, endOvernight, startOvernight;

    public int getCheckOutOneday() {
        return checkOutOneday;
    }

    public void setCheckOutOneday(int checkOutOneday) {
        this.checkOutOneday = checkOutOneday;
    }

    public int getEndOvernight() {
        return endOvernight;
    }

    public void setEndOvernight(int endOvernight) {
        this.endOvernight = endOvernight;
    }

    public int getStartOvernight() {
        return startOvernight;
    }

    public void setStartOvernight(int startOvernight) {
        this.startOvernight = startOvernight;
    }
}
