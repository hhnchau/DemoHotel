package com.appromobile.hotel.model.view;

import java.util.List;

/**
 * Created by appro on 25/12/2017.
 */

public class UserStampForm {
    private int sn;

    private int hotelSn;

    private int appUserSn;

    private int numStampActive;

    private int numStampExpire;

    private int numStampUsed;

    private int totalRedeem;

    private String startJoinTime;

    private String hotelName;

    private String nickName;

    private String mobile;

    private int redeemValue;

    private int numToRedeem;

    private boolean redeemHourly;

    private boolean redeemOvernight;

    private boolean redeemDaily;

    private List<StampIssuedForm> stampIssuedFormList;

    private int numStampLocked;

    private int redeemType; //1:money  2: percent

    private int maxRedeem;

    private int numBeforeEnd;


    public UserStampForm() {
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
    }

    public int getAppUserSn() {
        return appUserSn;
    }

    public void setAppUserSn(int appUserSn) {
        this.appUserSn = appUserSn;
    }

    public int getNumStampActive() {
        return numStampActive;
    }

    public void setNumStampActive(int numStampActive) {
        this.numStampActive = numStampActive;
    }

    public int getNumStampExpire() {
        return numStampExpire;
    }

    public void setNumStampExpire(int numStampExpire) {
        this.numStampExpire = numStampExpire;
    }

    public int getNumStampUsed() {
        return numStampUsed;
    }

    public void setNumStampUsed(int numStampUsed) {
        this.numStampUsed = numStampUsed;
    }

    public int getTotalRedeem() {
        return totalRedeem;
    }

    public void setTotalRedeem(int totalRedeem) {
        this.totalRedeem = totalRedeem;
    }

    public String getStartJoinTime() {
        return startJoinTime;
    }

    public void setStartJoinTime(String startJoinTime) {
        this.startJoinTime = startJoinTime;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getRedeemValue() {
        return redeemValue;
    }

    public void setRedeemValue(int redeemValue) {
        this.redeemValue = redeemValue;
    }

    public int getNumToRedeem() {
        return numToRedeem;
    }

    public void setNumToRedeem(int numToRedeem) {
        this.numToRedeem = numToRedeem;
    }

    public boolean isRedeemHourly() {
        return redeemHourly;
    }

    public void setRedeemHourly(boolean redeemHourly) {
        this.redeemHourly = redeemHourly;
    }

    public boolean isRedeemOvernight() {
        return redeemOvernight;
    }

    public void setRedeemOvernight(boolean redeemOvernight) {
        this.redeemOvernight = redeemOvernight;
    }

    public boolean isRedeemDaily() {
        return redeemDaily;
    }

    public void setRedeemDaily(boolean redeemDaily) {
        this.redeemDaily = redeemDaily;
    }

    public List<StampIssuedForm> getStampIssuedFormList() {
        return stampIssuedFormList;
    }

    public void setStampIssuedFormList(List<StampIssuedForm> stampIssuedFormList) {
        this.stampIssuedFormList = stampIssuedFormList;
    }

    public int getNumStampLocked() {
        return numStampLocked;
    }

    public void setNumStampLocked(int numStampLocked) {
        this.numStampLocked = numStampLocked;
    }

    public int getRedeemType() {
        return redeemType;
    }

    public void setRedeemType(int redeemType) {
        this.redeemType = redeemType;
    }

    public int getMaxRedeem() {
        return maxRedeem;
    }

    public void setMaxRedeem(int maxRedeem) {
        this.maxRedeem = maxRedeem;
    }

    public int getNumBeforeEnd() {
        return numBeforeEnd;
    }

    public void setNumBeforeEnd(int numBeforeEnd) {
        this.numBeforeEnd = numBeforeEnd;
    }
}
