package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 7/29/2016.
 */
public class UserAreaFavoriteDto {
    private int districtSn;
    private boolean favorite;
//    private int provinceSn;

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

//    public int getProvinceSn() {
//        return provinceSn;
//    }
//
//    public void setProvinceSn(int provinceSn) {
//        this.provinceSn = provinceSn;
//    }
}
