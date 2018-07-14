package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thanh on 7/18/2017.
 */

public class CouponConditionForm implements Parcelable{

    private Long sn;

    private Long couponSn;

    private boolean weekday;

    private boolean weekend;

    private boolean hourly;

    private boolean overnight;

    private boolean daily;

    private String startTime;

    private String endTime;

    private boolean newUser;

    private boolean alreadyUser;

    private String couponMemo;

    private boolean sunday;

    private boolean monday;

    private boolean tuesday;

    private boolean wednesday;

    private boolean thursday;

    private boolean friday;

    private boolean saturday;

    private String paymentMethod;

    private int discountType;

    private int discount;

    private int cineDiscount;

    private int maxDiscount;

    private boolean afterDiscount;

    private int numHours;

    private int numDays;

    public CouponConditionForm() {
    }

    protected CouponConditionForm(Parcel in) {
        if (in.readByte() == 0) {
            sn = null;
        } else {
            sn = in.readLong();
        }
        if (in.readByte() == 0) {
            couponSn = null;
        } else {
            couponSn = in.readLong();
        }
        weekday = in.readByte() != 0;
        weekend = in.readByte() != 0;
        hourly = in.readByte() != 0;
        overnight = in.readByte() != 0;
        daily = in.readByte() != 0;
        startTime = in.readString();
        endTime = in.readString();
        newUser = in.readByte() != 0;
        alreadyUser = in.readByte() != 0;
        couponMemo = in.readString();
        sunday = in.readByte() != 0;
        monday = in.readByte() != 0;
        tuesday = in.readByte() != 0;
        wednesday = in.readByte() != 0;
        thursday = in.readByte() != 0;
        friday = in.readByte() != 0;
        saturday = in.readByte() != 0;
        paymentMethod = in.readString();
        discountType = in.readInt();
        discount = in.readInt();
        cineDiscount = in.readInt();
        maxDiscount = in.readInt();
        afterDiscount = in.readByte() != 0;
        numHours = in.readInt();
        numDays = in.readInt();
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

    public Long getSn() {
        return sn;
    }

    public CouponConditionForm setSn(Long sn) {
        this.sn = sn;
        return this;
    }

    public Long getCouponSn() {
        return couponSn;
    }

    public CouponConditionForm setCouponSn(Long couponSn) {
        this.couponSn = couponSn;
        return this;
    }

    public boolean isWeekday() {
        return weekday;
    }

    public CouponConditionForm setWeekday(boolean weekday) {
        this.weekday = weekday;
        return this;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public CouponConditionForm setWeekend(boolean weekend) {
        this.weekend = weekend;
        return this;
    }

    public boolean isHourly() {
        return hourly;
    }

    public CouponConditionForm setHourly(boolean hourly) {
        this.hourly = hourly;
        return this;
    }

    public boolean isOvernight() {
        return overnight;
    }

    public CouponConditionForm setOvernight(boolean overnight) {
        this.overnight = overnight;
        return this;
    }

    public boolean isDaily() {
        return daily;
    }

    public CouponConditionForm setDaily(boolean daily) {
        this.daily = daily;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public CouponConditionForm setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public CouponConditionForm setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public CouponConditionForm setNewUser(boolean newUser) {
        this.newUser = newUser;
        return this;
    }

    public boolean isAlreadyUser() {
        return alreadyUser;
    }

    public CouponConditionForm setAlreadyUser(boolean alreadyUser) {
        this.alreadyUser = alreadyUser;
        return this;
    }

    public String getCouponMemo() {
        return couponMemo;
    }

    public CouponConditionForm setCouponMemo(String couponMemo) {
        this.couponMemo = couponMemo;
        return this;
    }

    public boolean isSunday() {
        return sunday;
    }

    public CouponConditionForm setSunday(boolean sunday) {
        this.sunday = sunday;
        return this;
    }

    public boolean isMonday() {
        return monday;
    }

    public CouponConditionForm setMonday(boolean monday) {
        this.monday = monday;
        return this;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public CouponConditionForm setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
        return this;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public CouponConditionForm setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
        return this;
    }

    public boolean isThursday() {
        return thursday;
    }

    public CouponConditionForm setThursday(boolean thursday) {
        this.thursday = thursday;
        return this;
    }

    public boolean isFriday() {
        return friday;
    }

    public CouponConditionForm setFriday(boolean friday) {
        this.friday = friday;
        return this;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public CouponConditionForm setSaturday(boolean saturday) {
        this.saturday = saturday;
        return this;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public CouponConditionForm setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public int getDiscountType() {
        return discountType;
    }

    public CouponConditionForm setDiscountType(int discountType) {
        this.discountType = discountType;
        return this;
    }

    public int getDiscount() {
        return discount;
    }

    public CouponConditionForm setDiscount(int discount) {
        this.discount = discount;
        return this;
    }

    public int getCineDiscount() {
        return cineDiscount;
    }

    public CouponConditionForm setCineDiscount(int cineDiscount) {
        this.cineDiscount = cineDiscount;
        return this;
    }

    public int getMaxDiscount() {
        return maxDiscount;
    }

    public CouponConditionForm setMaxDiscount(int maxDiscount) {
        this.maxDiscount = maxDiscount;
        return this;
    }

    public boolean isAfterDiscount() {
        return afterDiscount;
    }

    public CouponConditionForm setAfterDiscount(boolean afterDiscount) {
        this.afterDiscount = afterDiscount;
        return this;
    }

    public int getNumHours() {
        return numHours;
    }

    public CouponConditionForm setNumHours(int numHours) {
        this.numHours = numHours;
        return this;
    }

    public int getNumDays() {
        return numDays;
    }

    public CouponConditionForm setNumDays(int numDays) {
        this.numDays = numDays;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (sn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(sn);
        }
        if (couponSn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(couponSn);
        }
        dest.writeByte((byte) (weekday ? 1 : 0));
        dest.writeByte((byte) (weekend ? 1 : 0));
        dest.writeByte((byte) (hourly ? 1 : 0));
        dest.writeByte((byte) (overnight ? 1 : 0));
        dest.writeByte((byte) (daily ? 1 : 0));
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeByte((byte) (newUser ? 1 : 0));
        dest.writeByte((byte) (alreadyUser ? 1 : 0));
        dest.writeString(couponMemo);
        dest.writeByte((byte) (sunday ? 1 : 0));
        dest.writeByte((byte) (monday ? 1 : 0));
        dest.writeByte((byte) (tuesday ? 1 : 0));
        dest.writeByte((byte) (wednesday ? 1 : 0));
        dest.writeByte((byte) (thursday ? 1 : 0));
        dest.writeByte((byte) (friday ? 1 : 0));
        dest.writeByte((byte) (saturday ? 1 : 0));
        dest.writeString(paymentMethod);
        dest.writeInt(discountType);
        dest.writeInt(discount);
        dest.writeInt(cineDiscount);
        dest.writeInt(maxDiscount);
        dest.writeByte((byte) (afterDiscount ? 1 : 0));
        dest.writeInt(numHours);
        dest.writeInt(numDays);
    }
}
