package com.appromobile.hotel.model.request;

import java.io.Serializable;

/**
 * Created by xuan on 7/14/2016.
 */
public class UserLocationForm implements Serializable {
    private boolean alwaysRefresh;
    private String countryCode;
    private Long countrySn;
    private String districtCode;
    private Long districtSn;
    private Long hotelUserSn;
    private double latitude;
    private double longitude;
    private String mobileUserId;
    private String provinceCode;
    private Long provinceSn;
    private Long sn;
    private int limit;
    private int offset;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMobileUserId() {
        return mobileUserId;
    }

    public void setMobileUserId(String mobileUserId) {
        this.mobileUserId = mobileUserId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getCountrySn() {
        return countrySn;
    }

    public void setCountrySn(Long countrySn) {
        this.countrySn = countrySn;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public Long getDistrictSn() {
        return districtSn;
    }

    public void setDistrictSn(Long districtSn) {
        this.districtSn = districtSn;
    }

    public Long getHotelUserSn() {
        return hotelUserSn;
    }

    public void setHotelUserSn(Long hotelUserSn) {
        this.hotelUserSn = hotelUserSn;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Long getProvinceSn() {
        return provinceSn;
    }

    public void setProvinceSn(Long provinceSn) {
        this.provinceSn = provinceSn;
    }

    public Long getSn() {
        return sn;
    }

    public void setSn(Long sn) {
        this.sn = sn;
    }

    public boolean isAlwaysRefresh() {
        return alwaysRefresh;
    }

    public void setAlwaysRefresh(boolean alwaysRefresh) {
        this.alwaysRefresh = alwaysRefresh;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
