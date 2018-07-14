package com.appromobile.hotel.model.view;

/**
 * Created by xuan on 7/26/2016.
 */
public class UserAreaFavoriteForm {
    private String countryName;
    private int countrySn;
    private String districtName;
    private int districtSn;
    private boolean favorite;
    private String provinceName;
    private int provinceSn;
    private int sn;
    private int totalHotel;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getCountrySn() {
        return countrySn;
    }

    public void setCountrySn(int countrySn) {
        this.countrySn = countrySn;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
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

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceSn() {
        return provinceSn;
    }

    public void setProvinceSn(int provinceSn) {
        this.provinceSn = provinceSn;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getTotalHotel() {
        return totalHotel;
    }

    public void setTotalHotel(int totalHotel) {
        this.totalHotel = totalHotel;
    }

    public UserAreaFavoriteForm clone(){
        UserAreaFavoriteForm userAreaFavoriteForm = new UserAreaFavoriteForm();

        userAreaFavoriteForm.setSn(sn);
        userAreaFavoriteForm.setCountryName(countryName);
        userAreaFavoriteForm.setCountrySn(countrySn);
        userAreaFavoriteForm.setDistrictName(districtName);
        userAreaFavoriteForm.setDistrictSn(districtSn);
        userAreaFavoriteForm.setFavorite(favorite);
        userAreaFavoriteForm.setProvinceName(provinceName);
        userAreaFavoriteForm.setProvinceSn(provinceSn);
        userAreaFavoriteForm.setTotalHotel(totalHotel);
        return userAreaFavoriteForm;
    }
}
