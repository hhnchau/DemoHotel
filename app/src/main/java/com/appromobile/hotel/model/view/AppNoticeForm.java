package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 9/5/2016.
 */
public class AppNoticeForm implements Parcelable {
    private String content;
    private String createStaffName;
    private int createStaffSn;
    private String createTime;
    private String lastUpdate;
    private int sn;
    private String title;
    private int top;
    private int promotionSn;
    private boolean appliedPromotion;

    protected AppNoticeForm(Parcel in) {
        content = in.readString();
        createStaffName = in.readString();
        createStaffSn = in.readInt();
        createTime = in.readString();
        lastUpdate = in.readString();
        sn = in.readInt();
        title = in.readString();
        top = in.readInt();
        promotionSn = in.readInt();
        appliedPromotion = in.readByte() != 0;
    }

    public static final Creator<AppNoticeForm> CREATOR = new Creator<AppNoticeForm>() {
        @Override
        public AppNoticeForm createFromParcel(Parcel in) {
            return new AppNoticeForm(in);
        }

        @Override
        public AppNoticeForm[] newArray(int size) {
            return new AppNoticeForm[size];
        }
    };

    public String getContent() {
        if (content != null)
            return content;
        else
            return "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateStaffName() {
        if (createStaffName != null)
            return createStaffName;
        else
            return "";
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public String getTitle() {
        if (title != null)
            return title;
        else
            return "";

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public AppNoticeForm(){}

    public int getPromotionSn() {
        return promotionSn;
    }

    public void setPromotionSn(int promotionSn) {
        this.promotionSn = promotionSn;
    }

    public boolean isAppliedPromotion() {
        return appliedPromotion;
    }

    public void setAppliedPromotion(boolean appliedPromotion) {
        this.appliedPromotion = appliedPromotion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(createStaffName);
        dest.writeInt(createStaffSn);
        dest.writeString(createTime);
        dest.writeString(lastUpdate);
        dest.writeInt(sn);
        dest.writeString(title);
        dest.writeInt(top);
        dest.writeInt(promotionSn);
        dest.writeByte((byte) (appliedPromotion ? 1 : 0));
    }
}
