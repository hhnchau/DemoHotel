package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 8/15/2016.
 */
public class AppUserForm implements Parcelable{
    private String address;
    private String birthday;
    private String email;
    private int gender;
    private String lastUpdate;
    private String mobile;
    private String nickName;
    private String session;
    private int sn;
    private String userId;
    private int viaApp;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getViaApp() {
        return viaApp;
    }

    public void setViaApp(int viaApp) {
        this.viaApp = viaApp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeString(this.birthday);
        dest.writeString(this.email);
        dest.writeInt(this.gender);
        dest.writeString(this.lastUpdate);
        dest.writeString(this.mobile);
        dest.writeString(this.nickName);
        dest.writeString(this.session);
        dest.writeInt(this.sn);
        dest.writeString(this.userId);
        dest.writeInt(this.viaApp);
    }

    public AppUserForm() {
    }

    protected AppUserForm(Parcel in) {
        this.address = in.readString();
        this.birthday = in.readString();
        this.email = in.readString();
        this.gender = in.readInt();
        this.lastUpdate = in.readString();
        this.mobile = in.readString();
        this.nickName = in.readString();
        this.session = in.readString();
        this.sn = in.readInt();
        this.userId = in.readString();
        this.viaApp = in.readInt();
    }

    public static final Creator<AppUserForm> CREATOR = new Creator<AppUserForm>() {
        @Override
        public AppUserForm createFromParcel(Parcel source) {
            return new AppUserForm(source);
        }

        @Override
        public AppUserForm[] newArray(int size) {
            return new AppUserForm[size];
        }
    };
}
