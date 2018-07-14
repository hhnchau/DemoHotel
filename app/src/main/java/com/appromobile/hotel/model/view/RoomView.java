package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by appro on 18/12/2017.
 */

public class RoomView implements Parcelable {
    private String name;
    private String nameEn;
    private  int sn;

    public RoomView() {
    }

    protected RoomView(Parcel in) {
        name = in.readString();
        nameEn = in.readString();
        sn = in.readInt();
    }

    public static final Creator<RoomView> CREATOR = new Creator<RoomView>() {
        @Override
        public RoomView createFromParcel(Parcel in) {
            return new RoomView(in);
        }

        @Override
        public RoomView[] newArray(int size) {
            return new RoomView[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(nameEn);
        parcel.writeInt(sn);
    }
}
