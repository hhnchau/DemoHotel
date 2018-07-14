package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thanh on 7/18/2017.
 */

public class CouponConditionForm implements Parcelable{
    private boolean alreadyUser;
    private int couponSn;
    private boolean daily;
    private String endTime;
    private boolean hourly;
    private boolean newUser;
    private boolean overnight;
    private boolean payAtHotel;
    private boolean payInAdvance;
    private int sn;
    private String startTime;
    private boolean weekday;
    private boolean weekend;

    protected CouponConditionForm(Parcel in) {
        alreadyUser = in.readByte() != 0;
        couponSn = in.readInt();
        daily = in.readByte() != 0;
        endTime = in.readString();
        hourly = in.readByte() != 0;
        newUser = in.readByte() != 0;
        overnight = in.readByte() != 0;
        payAtHotel = in.readByte() != 0;
        payInAdvance = in.readByte() != 0;
        sn = in.readInt();
        startTime = in.readString();
        weekday = in.readByte() != 0;
        weekend = in.readByte() != 0;
    }

    public static final Creator<CouponConditionForm> CREATOR = new Creator<CouponConditionForm>() {
        @Override
        public CouponConditionForm createFromParcel(Parcel in) {
            return new CouponConditionForm(in);
        }

        @Override
        public CouponConditionForm[] newArray(int size) {
            return new CouponConditionForm[size];
        }
    };

    public boolean isAlreadyUser() {
        return alreadyUser;
    }

    public void setAlreadyUser(boolean alreadyUser) {
        this.alreadyUser = alreadyUser;
    }

    public int getCouponSn() {
        return couponSn;
    }

    public void setCouponSn(int couponSn) {
        this.couponSn = couponSn;
    }

    public boolean isDaily() {
        return daily;
    }

    public void setDaily(boolean daily) {
        this.daily = daily;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isHourly() {
        return hourly;
    }

    public void setHourly(boolean hourly) {
        this.hourly = hourly;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public boolean isOvernight() {
        return overnight;
    }

    public void setOvernight(boolean overnight) {
        this.overnight = overnight;
    }

    public boolean isPayAtHotel() {
        return payAtHotel;
    }

    public void setPayAtHotel(boolean payAtHotel) {
        this.payAtHotel = payAtHotel;
    }

    public boolean isPayInAdvance() {
        return payInAdvance;
    }

    public void setPayInAdvance(boolean payInAdvance) {
        this.payInAdvance = payInAdvance;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isWeekday() {
        return weekday;
    }

    public void setWeekday(boolean weekday) {
        this.weekday = weekday;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (alreadyUser ? 1 : 0));
        dest.writeInt(couponSn);
        dest.writeByte((byte) (daily ? 1 : 0));
        dest.writeString(endTime);
        dest.writeByte((byte) (hourly ? 1 : 0));
        dest.writeByte((byte) (newUser ? 1 : 0));
        dest.writeByte((byte) (overnight ? 1 : 0));
        dest.writeByte((byte) (payAtHotel ? 1 : 0));
        dest.writeByte((byte) (payInAdvance ? 1 : 0));
        dest.writeInt(sn);
        dest.writeString(startTime);
        dest.writeByte((byte) (weekday ? 1 : 0));
        dest.writeByte((byte) (weekend ? 1 : 0));
    }
}
