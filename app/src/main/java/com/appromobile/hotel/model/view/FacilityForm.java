package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 7/18/2016.
 */
public class FacilityForm implements Parcelable{
    private String contentType;
    private String createStaffName;
    private int createStaffSn;
    private String customizePath;
    private String lastUpdate;
    private String name;
    private String originalName;
    private int sn;
    private String imageKey;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCreateStaffName() {
        return createStaffName;
    }

    public void setCreateStaffName(String createStaffName) {
        this.createStaffName = createStaffName;
    }

    public int getCreateStaffSn() {
        return createStaffSn;
    }

    public void setCreateStaffSn(int createStaffSn) {
        this.createStaffSn = createStaffSn;
    }

    public String getCustomizePath() {
        return customizePath;
    }

    public void setCustomizePath(String customizePath) {
        this.customizePath = customizePath;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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

    public FacilityForm setImageKey(String imageKey) {
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
        dest.writeString(this.createStaffName);
        dest.writeInt(this.createStaffSn);
        dest.writeString(this.customizePath);
        dest.writeString(this.lastUpdate);
        dest.writeString(this.name);
        dest.writeString(this.originalName);
        dest.writeInt(this.sn);
        dest.writeString(this.imageKey);
    }

    public FacilityForm() {
    }

    protected FacilityForm(Parcel in) {
        this.contentType = in.readString();
        this.createStaffName = in.readString();
        this.createStaffSn = in.readInt();
        this.customizePath = in.readString();
        this.lastUpdate = in.readString();
        this.name = in.readString();
        this.originalName = in.readString();
        this.sn = in.readInt();
        this.imageKey = in.readString();
    }

    public static final Creator<FacilityForm> CREATOR = new Creator<FacilityForm>() {
        @Override
        public FacilityForm createFromParcel(Parcel source) {
            return new FacilityForm(source);
        }

        @Override
        public FacilityForm[] newArray(int size) {
            return new FacilityForm[size];
        }
    };
}
