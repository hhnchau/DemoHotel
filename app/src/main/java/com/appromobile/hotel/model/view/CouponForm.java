package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thanh on 7/11/2017.
 */

public class CouponForm implements Parcelable {

    private String code;
    private int discount;
    private int discountType;
    private String endDate;
    private int maxDiscount;
    private String startDate;
    private int numOfDonate;

    public CouponForm() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(int maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getNumOfDonate() {
        return numOfDonate;
    }

    public void setNumOfDonate(int numOfDonate) {
        this.numOfDonate = numOfDonate;
    }

    protected CouponForm(Parcel in) {
        code = in.readString();
        discount = in.readInt();
        discountType = in.readInt();
        endDate = in.readString();
        maxDiscount = in.readInt();
        startDate = in.readString();
        numOfDonate = in.readInt();
    }

    public static final Creator<CouponForm> CREATOR = new Creator<CouponForm>() {
        @Override
        public CouponForm createFromParcel(Parcel in) {
            return new CouponForm(in);
        }

        @Override
        public CouponForm[] newArray(int size) {
            return new CouponForm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeInt(discount);
        dest.writeInt(discountType);
        dest.writeString(endDate);
        dest.writeInt(maxDiscount);
        dest.writeString(startDate);
        dest.writeInt(numOfDonate);
    }
}