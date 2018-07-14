package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 9/14/2016.
 */
public class PromotionImageForm implements Parcelable {
    private String contentType;
    private String customizePath;
    private String lastUpdate;
    private int promotionSn;
    private int sn;
    private int typeDisplay;

    public int getTypeDisplay() {
        return typeDisplay;
    }

    public void setTypeDisplay(int typeDisplay) {
        this.typeDisplay = typeDisplay;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public int getPromotionSn() {
        return promotionSn;
    }

    public void setPromotionSn(int promotionSn) {
        this.promotionSn = promotionSn;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contentType);
        dest.writeString(this.customizePath);
        dest.writeString(this.lastUpdate);
        dest.writeInt(this.promotionSn);
        dest.writeInt(this.sn);
        dest.writeInt(this.typeDisplay);
    }

    public PromotionImageForm() {
    }

    protected PromotionImageForm(Parcel in) {
        this.contentType = in.readString();
        this.customizePath = in.readString();
        this.lastUpdate = in.readString();
        this.promotionSn = in.readInt();
        this.sn = in.readInt();
        this.typeDisplay = in.readInt();
    }

    public static final Creator<PromotionImageForm> CREATOR = new Creator<PromotionImageForm>() {
        @Override
        public PromotionImageForm createFromParcel(Parcel source) {
            return new PromotionImageForm(source);
        }

        @Override
        public PromotionImageForm[] newArray(int size) {
            return new PromotionImageForm[size];
        }
    };
}
