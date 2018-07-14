package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 11/9/2016.
 */

public class NotificationData implements Parcelable {
    private String title;
    private String subTitle;
    private String iconUrl;
    private int sn;
    private int type;
    private int actionType;
    private int hotelSn;
    private String[] otherInfoList;
    private int targetSn;
    private int targetType;
    private int appNotificationSn;
    private boolean key;
    private String []titleLocArgs;
    private String []locArgs;

    public NotificationData() {
    }

    protected NotificationData(Parcel in) {
        title = in.readString();
        subTitle = in.readString();
        iconUrl = in.readString();
        sn = in.readInt();
        type = in.readInt();
        actionType = in.readInt();
        hotelSn = in.readInt();
        otherInfoList = in.createStringArray();
        targetSn = in.readInt();
        targetType = in.readInt();
        appNotificationSn = in.readInt();
        key = in.readByte() != 0;
        titleLocArgs = in.createStringArray();
        locArgs = in.createStringArray();
    }

    public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
    }

    public String[] getOtherInfoList() {
        return otherInfoList;
    }

    public void setOtherInfoList(String[] otherInfoList) {
        this.otherInfoList = otherInfoList;
    }

    public int getTargetSn() {
        return targetSn;
    }

    public void setTargetSn(int targetSn) {
        this.targetSn = targetSn;
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public int getAppNotificationSn() {
        return appNotificationSn;
    }

    public void setAppNotificationSn(int appNotificationSn) {
        this.appNotificationSn = appNotificationSn;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public String[] getTitleLocArgs() {
        return titleLocArgs;
    }

    public void setTitleLocArgs(String[] titleLocArgs) {
        this.titleLocArgs = titleLocArgs;
    }

    public String[] getLocArgs() {
        return locArgs;
    }

    public void setLocArgs(String[] locArgs) {
        this.locArgs = locArgs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(subTitle);
        parcel.writeString(iconUrl);
        parcel.writeInt(sn);
        parcel.writeInt(type);
        parcel.writeInt(actionType);
        parcel.writeInt(hotelSn);
        parcel.writeStringArray(otherInfoList);
        parcel.writeInt(targetSn);
        parcel.writeInt(targetType);
        parcel.writeInt(appNotificationSn);
        parcel.writeByte((byte) (key ? 1 : 0));
        parcel.writeStringArray(titleLocArgs);
        parcel.writeStringArray(locArgs);
    }
}
