package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 12/16/2016.
 */

public class LastBookingForm implements Parcelable {
    private int bookingNo;
    private int bookingStatus;
    private String checkInDatePlan;
    private String checkInTime;
    private String createTime;
    private String hotelName;
    private int hotelSn;
    private int roomTypeSn;
    private int sn;
    private String imageKey;

    public int getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(int bookingNo) {
        this.bookingNo = bookingNo;
    }

    public int getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(int bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getCheckInDatePlan() {
        return checkInDatePlan;
    }

    public void setCheckInDatePlan(String checkInDatePlan) {
        this.checkInDatePlan = checkInDatePlan;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public int getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
    }

    public int getRoomTypeSn() {
        return roomTypeSn;
    }

    public void setRoomTypeSn(int roomTypeSn) {
        this.roomTypeSn = roomTypeSn;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public String getImageKey() {
        return imageKey;
    }

    public LastBookingForm setImageKey(String imageKey) {
        this.imageKey = imageKey;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bookingNo);
        dest.writeInt(this.bookingStatus);
        dest.writeString(this.checkInDatePlan);
        dest.writeString(this.checkInTime);
        dest.writeString(this.createTime);
        dest.writeString(this.hotelName);
        dest.writeInt(this.hotelSn);
        dest.writeInt(this.roomTypeSn);
        dest.writeInt(this.sn);
        dest.writeString(this.imageKey);
    }

    public LastBookingForm() {
    }

    protected LastBookingForm(Parcel in) {
        this.bookingNo = in.readInt();
        this.bookingStatus = in.readInt();
        this.checkInDatePlan = in.readString();
        this.checkInTime = in.readString();
        this.createTime = in.readString();
        this.hotelName = in.readString();
        this.hotelSn = in.readInt();
        this.roomTypeSn = in.readInt();
        this.sn = in.readInt();
        this.imageKey = in.readString();
    }

    public static final Parcelable.Creator<LastBookingForm> CREATOR = new Parcelable.Creator<LastBookingForm>() {
        @Override
        public LastBookingForm createFromParcel(Parcel source) {
            return new LastBookingForm(source);
        }

        @Override
        public LastBookingForm[] newArray(int size) {
            return new LastBookingForm[size];
        }
    };
}
