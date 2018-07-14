package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 7/18/2016.
 */
public class HotelImageForm implements Parcelable {
    private String contentType;
    private int hotelSn;
    private String originalName;
    private String customizeName;
    private String imageKey;
    private int roomTypeSn;
    private int sn;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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

    public String getCustomizeName() {
        return customizeName;
    }

    public void setCustomizeName(String customizeName) {
        this.customizeName = customizeName;
    }

    public String getImageKey() {
        return imageKey;
    }

    public HotelImageForm setImageKey(String imageKey) {
        this.imageKey = imageKey;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contentType);
        dest.writeInt(this.hotelSn);
        dest.writeString(this.originalName);
        dest.writeString(this.customizeName);
        dest.writeString(this.imageKey);
        dest.writeInt(this.roomTypeSn);
        dest.writeInt(this.sn);
    }

    public HotelImageForm() {
    }

    protected HotelImageForm(Parcel in) {
        this.contentType = in.readString();
        this.hotelSn = in.readInt();
        this.originalName = in.readString();
        this.customizeName = in.readString();
        this.imageKey = in.readString();
        this.roomTypeSn = in.readInt();
        this.sn = in.readInt();
    }

    public static final Creator<HotelImageForm> CREATOR = new Creator<HotelImageForm>() {
        @Override
        public HotelImageForm createFromParcel(Parcel source) {
            return new HotelImageForm(source);
        }

        @Override
        public HotelImageForm[] newArray(int size) {
            return new HotelImageForm[size];
        }
    };
}
