package com.appromobile.hotel.model.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 8/12/2016.
 */
public class AppUserSocialDto implements Parcelable {
    private String address;
    private String birthday;
    private String email;
    private int gender;
    private String mobile;
    private String mobileUserId;
    private String nickName;
    private String token;
    private int viaApp;
    private String verifyCode;
    private String inviteCode;

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobileUserId() {
        return mobileUserId;
    }

    public void setMobileUserId(String mobileUserId) {
        this.mobileUserId = mobileUserId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getViaApp() {
        return viaApp;
    }

    public void setViaApp(int viaApp) {
        this.viaApp = viaApp;
    }

    public AppUserSocialDto() {
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
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
        dest.writeString(this.mobile);
        dest.writeString(this.mobileUserId);
        dest.writeString(this.nickName);
        dest.writeString(this.token);
        dest.writeInt(this.viaApp);
        dest.writeString(this.verifyCode);
        dest.writeString(this.inviteCode);
    }

    protected AppUserSocialDto(Parcel in) {
        this.address = in.readString();
        this.birthday = in.readString();
        this.email = in.readString();
        this.gender = in.readInt();
        this.mobile = in.readString();
        this.mobileUserId = in.readString();
        this.nickName = in.readString();
        this.token = in.readString();
        this.viaApp = in.readInt();
        this.verifyCode = in.readString();
        this.inviteCode = in.readString();
    }

    public static final Creator<AppUserSocialDto> CREATOR = new Creator<AppUserSocialDto>() {
        @Override
        public AppUserSocialDto createFromParcel(Parcel source) {
            return new AppUserSocialDto(source);
        }

        @Override
        public AppUserSocialDto[] newArray(int size) {
            return new AppUserSocialDto[size];
        }
    };
}
