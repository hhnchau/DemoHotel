package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 7/27/2016.
 */
public class UserFavoriteInput {
    private boolean favorite;
    private int hotelSn;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
    }
}
