package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by appro on 17/04/2018.
 */

public class UserSettingForm implements Parcelable {
    private String title;
    private Long sn;
    private Long appUserSn;
    private boolean notiAll;
    private boolean notiOff;
    private boolean notiBefore1;
    private boolean notiBefore2;
    private int language;

    public UserSettingForm() {
    }

    protected UserSettingForm(Parcel in) {
        title = in.readString();
        if (in.readByte() == 0) {
            sn = null;
        } else {
            sn = in.readLong();
        }
        if (in.readByte() == 0) {
            appUserSn = null;
        } else {
            appUserSn = in.readLong();
        }
        notiAll = in.readByte() != 0;
        notiOff = in.readByte() != 0;
        notiBefore1 = in.readByte() != 0;
        notiBefore2 = in.readByte() != 0;
        language = in.readInt();
    }

    public static final Creator<UserSettingForm> CREATOR = new Creator<UserSettingForm>() {
        @Override
        public UserSettingForm createFromParcel(Parcel in) {
            return new UserSettingForm(in);
        }

        @Override
        public UserSettingForm[] newArray(int size) {
            return new UserSettingForm[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public UserSettingForm setTitle(String title) {
        this.title = title;
        return this;
    }

    public Long getSn() {
        return sn;
    }

    public UserSettingForm setSn(Long sn) {
        this.sn = sn;
        return this;
    }

    public Long getAppUserSn() {
        return appUserSn;
    }

    public UserSettingForm setAppUserSn(Long appUserSn) {
        this.appUserSn = appUserSn;
        return this;
    }

    public boolean isNotiAll() {
        return notiAll;
    }

    public UserSettingForm setNotiAll(boolean notiAll) {
        this.notiAll = notiAll;
        return this;
    }

    public boolean isNotiOff() {
        return notiOff;
    }

    public UserSettingForm setNotiOff(boolean notiOff) {
        this.notiOff = notiOff;
        return this;
    }

    public boolean isNotiBefore1() {
        return notiBefore1;
    }

    public UserSettingForm setNotiBefore1(boolean notiBefore1) {
        this.notiBefore1 = notiBefore1;
        return this;
    }

    public boolean isNotiBefore2() {
        return notiBefore2;
    }

    public UserSettingForm setNotiBefore2(boolean notiBefore2) {
        this.notiBefore2 = notiBefore2;
        return this;
    }

    public int getLanguage() {
        return language;
    }

    public UserSettingForm setLanguage(int language) {
        this.language = language;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        if (sn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(sn);
        }
        if (appUserSn == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(appUserSn);
        }
        dest.writeByte((byte) (notiAll ? 1 : 0));
        dest.writeByte((byte) (notiOff ? 1 : 0));
        dest.writeByte((byte) (notiBefore1 ? 1 : 0));
        dest.writeByte((byte) (notiBefore2 ? 1 : 0));
        dest.writeInt(language);
    }
}
