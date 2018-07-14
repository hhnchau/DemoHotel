package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thanh on 7/25/2017.
 */

public class CommonInfoForm implements Parcelable{
    String lastUpdate;
    String privatePolicy;
    String serviceAgreement;

    protected CommonInfoForm(Parcel in) {
        lastUpdate = in.readString();
        privatePolicy = in.readString();
        serviceAgreement = in.readString();
    }

    public static final Creator<CommonInfoForm> CREATOR = new Creator<CommonInfoForm>() {
        @Override
        public CommonInfoForm createFromParcel(Parcel in) {
            return new CommonInfoForm(in);
        }

        @Override
        public CommonInfoForm[] newArray(int size) {
            return new CommonInfoForm[size];
        }
    };

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getPrivatePolicy() {
        return privatePolicy;
    }

    public void setPrivatePolicy(String privatePolicy) {
        this.privatePolicy = privatePolicy;
    }

    public String getServiceAgreement() {
        return serviceAgreement;
    }

    public void setServiceAgreement(String serviceAgreement) {
        this.serviceAgreement = serviceAgreement;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lastUpdate);
        dest.writeString(privatePolicy);
        dest.writeString(serviceAgreement);
    }
}
