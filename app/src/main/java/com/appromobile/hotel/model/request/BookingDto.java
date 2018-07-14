package com.appromobile.hotel.model.request;

/**
 * Created by appro on 10/03/2017.
 */
public class BookingDto {
    private int hotelSn;
    private int userBookingSn;

    public int getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
    }

    public int getUserBookingSn() {
        return userBookingSn;
    }

    public void setUserBookingSn(int userBookingSn) {
        this.userBookingSn = userBookingSn;
    }
}
