package com.appromobile.hotel.model.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by appro on 02/03/2018.
 */

public class ViewCrmNotificationDto implements Parcelable {
    private Long sn;
    private int sendType;
    private int deviceOs;

    public ViewCrmNotificationDto(Long sn, int sendType, int deviceOs) {
        this.sn = sn;
        this.sendType = sendType;
        this.deviceOs = deviceOs;
    }

    protected ViewCrmNotificationDto(Parcel in) {
        if (in.readByte() == 0) {
            sn = null;
        } else {
            sn = in.readLong();
        }
        sendType = in.readInt();
        deviceOs = in.readInt();
    }

    public static final Creator<ViewCrmNotificationDto> CREATOR = new Creator<ViewCrmNotificationDto>() {
        @Override
        public ViewCrmNotificationDto createFromParcel(Parcel in) {
            return new ViewCrmNotificationDto(in);
        }

        @Override
        public ViewCrmNotificationDto[] newArray(int size) {
            return new ViewCrmNotificationDto[size];
        }
    };

    public Long getSn() {
        return sn;
    }

    public int getSendType() {
        return sendType;
    }

    public int getDeviceOs() {
        return deviceOs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (sn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(sn);
        }
        dest.writeInt(sendType);
        dest.writeInt(deviceOs);
    }
}
