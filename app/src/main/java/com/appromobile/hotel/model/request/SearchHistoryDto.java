package com.appromobile.hotel.model.request;

/**
 * Created by appro on 26/04/2018.
 */

public class SearchHistoryDto {
    private int hotelSn;
    private int sn;

    public SearchHistoryDto(int hotelSn, int sn) {
        this.hotelSn = hotelSn;
        this.sn = sn;
    }
}
