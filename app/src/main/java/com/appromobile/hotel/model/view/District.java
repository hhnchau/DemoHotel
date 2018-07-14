package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 7/25/2016.
 */
public class District implements Parcelable {
    private String lastUpdate;
    private String name;
    private String nameCode;
    private int provinceSn;
    private int sn;
    private int totalHotel;
    private int totalContracted;

    public District() {
    }

    protected District(Parcel in) {
        lastUpdate = in.readString();
        name = in.readString();
        nameCode = in.readString();
        provinceSn = in.readInt();
        sn = in.readInt();
        totalHotel = in.readInt();
        totalContracted = in.readInt();
    }

    public static final Creator<District> CREATOR = new Creator<District>() {
        @Override
        public District createFromParcel(Parcel in) {
            return new District(in);
        }

        @Override
        public District[] newArray(int size) {
            return new District[size];
        }
    };

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

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public int getProvinceSn() {
        return provinceSn;
    }

    public void setProvinceSn(int provinceSn) {
        this.provinceSn = provinceSn;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getTotalHotel() {
        return totalHotel;
    }

    public void setTotalHotel(int totalHotel) {
        this.totalHotel = totalHotel;
    }

    public int getTotalContracted() {
        return totalContracted;
    }

    public void setTotalContracted(int totalContracted) {
        this.totalContracted = totalContracted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lastUpdate);
        dest.writeString(name);
        dest.writeString(nameCode);
        dest.writeInt(provinceSn);
        dest.writeInt(sn);
        dest.writeInt(totalHotel);
        dest.writeInt(totalContracted);
    }
}
