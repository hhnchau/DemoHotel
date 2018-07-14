package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by appro on 03/10/2017.
 */
public class PayooInfoForm implements Parcelable {
    private String orderNo;

    private String orderInfo;

    private String checksum;

    private String merchantId;

    private String merchantSecrectKey;

    private boolean developmentMode;

    protected PayooInfoForm(Parcel in) {
        orderNo = in.readString();
        orderInfo = in.readString();
        checksum = in.readString();
        merchantId = in.readString();
        merchantSecrectKey = in.readString();
        developmentMode = in.readByte() != 0;
    }

    public static final Creator<PayooInfoForm> CREATOR = new Creator<PayooInfoForm>() {
        @Override
        public PayooInfoForm createFromParcel(Parcel in) {
            return new PayooInfoForm(in);
        }

        @Override
        public PayooInfoForm[] newArray(int size) {
            return new PayooInfoForm[size];
        }
    };

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantSecrectKey() {
        return merchantSecrectKey;
    }

    public void setMerchantSecrectKey(String merchantSecrectKey) {
        this.merchantSecrectKey = merchantSecrectKey;
    }

    public boolean isDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderNo);
        dest.writeString(orderInfo);
        dest.writeString(checksum);
        dest.writeString(merchantId);
        dest.writeString(merchantSecrectKey);
        dest.writeByte((byte) (developmentMode ? 1 : 0));
    }
}
