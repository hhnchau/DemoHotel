package com.appromobile.hotel.model.view;

/**
 * Created by appro on 26/03/2018.
 */

public class MileageHistoryForm {
    private int appUserSn;
    private int bookingNo;
    private int confirmed; // 0: Not Yet, 1: Confirmed, 2:Cancel ,
    private String expiredDate;
    private String hotelName;
    private int hotelSn;
    private String lastUpdate;
    private String mileageRewardName;
    private int mileageRewardSn;
    private int numOfPoint;
    private int sn;
    private int type;// 1: Get, 2:Use, 3:Expired ,
    private int userBookingSn;
    private int valueAmount;

    public MileageHistoryForm() {
    }

    public int getAppUserSn() {
        return appUserSn;
    }

    public MileageHistoryForm setAppUserSn(int appUserSn) {
        this.appUserSn = appUserSn;
        return this;
    }

    public int getBookingNo() {
        return bookingNo;
    }

    public MileageHistoryForm setBookingNo(int bookingNo) {
        this.bookingNo = bookingNo;
        return this;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public MileageHistoryForm setConfirmed(int confirmed) {
        this.confirmed = confirmed;
        return this;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public MileageHistoryForm setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
        return this;
    }

    public String getHotelName() {
        return hotelName;
    }

    public MileageHistoryForm setHotelName(String hotelName) {
        this.hotelName = hotelName;
        return this;
    }

    public int getHotelSn() {
        return hotelSn;
    }

    public MileageHistoryForm setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
        return this;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public MileageHistoryForm setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public String getMileageRewardName() {
        return mileageRewardName;
    }

    public MileageHistoryForm setMileageRewardName(String mileageRewardName) {
        this.mileageRewardName = mileageRewardName;
        return this;
    }

    public int getMileageRewardSn() {
        return mileageRewardSn;
    }

    public MileageHistoryForm setMileageRewardSn(int mileageRewardSn) {
        this.mileageRewardSn = mileageRewardSn;
        return this;
    }

    public int getNumOfPoint() {
        return numOfPoint;
    }

    public MileageHistoryForm setNumOfPoint(int numOfPoint) {
        this.numOfPoint = numOfPoint;
        return this;
    }

    public int getSn() {
        return sn;
    }

    public MileageHistoryForm setSn(int sn) {
        this.sn = sn;
        return this;
    }

    public int getType() {
        return type;
    }

    public MileageHistoryForm setType(int type) {
        this.type = type;
        return this;
    }

    public int getUserBookingSn() {
        return userBookingSn;
    }

    public MileageHistoryForm setUserBookingSn(int userBookingSn) {
        this.userBookingSn = userBookingSn;
        return this;
    }

    public int getValueAmount() {
        return valueAmount;
    }

    public MileageHistoryForm setValueAmount(int valueAmount) {
        this.valueAmount = valueAmount;
        return this;
    }
}
