package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 9/23/2016.
 */

public class UserBookingDto {
    private String checkInDatePlan;
    private Long couponIssuedSn;
    private String endDate;
    private String endTime;
    private int roomTypeSn;
    private String startTime;
    private int type;
    private String clientip;
    private int redeemValue;
    private String mobile;
    private int paymentMethod; //1: Pay At Hotel, 2: Pay Via 123, 3:Pay Via Payoo ,

    public String getCheckInDatePlan() {
        return checkInDatePlan;
    }

    public void setGetCheckInDatePlan(String bookingDate) {
        this.checkInDatePlan = bookingDate;
    }

    public Long getCouponIssuedSn() {
        return couponIssuedSn;
    }

    public void setCouponIssuedSn(Long couponIssuedSn) {
        this.couponIssuedSn = couponIssuedSn;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getRoomTypeSn() {
        return roomTypeSn;
    }

    public void setRoomTypeSn(int roomTypeSn) {
        this.roomTypeSn = roomTypeSn;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClientip() {
        return clientip;
    }

    public void setClientip(String clientip) {
        this.clientip = clientip;
    }

    public void setCheckInDatePlan(String checkInDatePlan) {
        this.checkInDatePlan = checkInDatePlan;
    }

    public int getRedeemValue() {
        return redeemValue;
    }

    public void setRedeemValue(int redeemValue) {
        this.redeemValue = redeemValue;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
