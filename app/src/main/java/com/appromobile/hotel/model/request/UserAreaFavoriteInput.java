package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 7/26/2016.
 */
public class UserAreaFavoriteInput {
//    private String countryName ;
    private int countrySn;
//    private String districtName;
    private int districtSn;
    private boolean favorite;
//    private String provinceName;
    private int provinceSn;

    public int getCountrySn() {
        return countrySn;
    }

    public void setCountrySn(int countrySn) {
        this.countrySn = countrySn;
    }

    public int getDistrictSn() {
        return districtSn;
    }

    public void setDistrictSn(int districtSn) {
        this.districtSn = districtSn;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getProvinceSn() {
        return provinceSn;
    }

    public void setProvinceSn(int provinceSn) {
        this.provinceSn = provinceSn;
    }
//    private int totalHotel;
}