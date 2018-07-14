package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by appro on 10/04/2018.
 */

public class RoomApplyPromotion implements Parcelable {
    private boolean cinejoy;
    private boolean flashsale;
    private boolean normal;
    private long[] roomTypeSnList;

    public RoomApplyPromotion() {
    }

    protected RoomApplyPromotion(Parcel in) {
        cinejoy = in.readByte() != 0;
        flashsale = in.readByte() != 0;
        normal = in.readByte() != 0;
        roomTypeSnList = in.createLongArray();
    }

    public static final Creator<RoomApplyPromotion> CREATOR = new Creator<RoomApplyPromotion>() {
        @Override
        public RoomApplyPromotion createFromParcel(Parcel in) {
            return new RoomApplyPromotion(in);
        }

        @Override
        public RoomApplyPromotion[] newArray(int size) {
            return new RoomApplyPromotion[size];
        }
    };

    public boolean isCinejoy() {
        return cinejoy;
    }

    public RoomApplyPromotion setCinejoy(boolean cinejoy) {
        this.cinejoy = cinejoy;
        return this;
    }

    public boolean isFlashsale() {
        return flashsale;
    }

    public RoomApplyPromotion setFlashsale(boolean flashsale) {
        this.flashsale = flashsale;
        return this;
    }

    public boolean isNormal() {
        return normal;
    }

    public RoomApplyPromotion setNormal(boolean normal) {
        this.normal = normal;
        return this;
    }

    public long[] getRoomTypeSnList() {
        return roomTypeSnList;
    }

    public RoomApplyPromotion setRoomTypeSnList(long[] roomTypeSnList) {
        this.roomTypeSnList = roomTypeSnList;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (cinejoy ? 1 : 0));
        dest.writeByte((byte) (flashsale ? 1 : 0));
        dest.writeByte((byte) (normal ? 1 : 0));
        dest.writeLongArray(roomTypeSnList);
    }
}
