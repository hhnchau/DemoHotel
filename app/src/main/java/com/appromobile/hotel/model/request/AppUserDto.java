package com.appromobile.hotel.model.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 8/5/2016.
 */
public class AppUserDto implements Parcelable{
    private String address;
    private String birthday;
    private int gender;
    private String mobile;
    private String nickName;
    private String password;
    private String userId;
    private String mobileUserId;
    private String verifyCode;
    private String inviteCode;

    public AppUserDto(){}

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobileUserId() {
        return mobileUserId;
    }

    public void setMobileUserId(String mobileUserId) {
        this.mobileUserId = mobileUserId;
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
        dest.writeInt(this.gender);
        dest.writeString(this.mobile);
        dest.writeString(this.nickName);
        dest.writeString(this.password);
        dest.writeString(this.userId);
        dest.writeString(this.mobileUserId);
        dest.writeString(this.verifyCode);
        dest.writeString(this.inviteCode);
    }

    protected AppUserDto(Parcel in) {
        this.address = in.readString();
        this.birthday = in.readString();
        this.gender = in.readInt();
        this.mobile = in.readString();
        this.nickName = in.readString();
        this.password = in.readString();
        this.userId = in.readString();
        this.mobileUserId = in.readString();
        this.verifyCode = in.readString();
        this.inviteCode = in.readString();
    }

    public static final Creator<AppUserDto> CREATOR = new Creator<AppUserDto>() {
        @Override
        public AppUserDto createFromParcel(Parcel source) {
            return new AppUserDto(source);
        }

        @Override
        public AppUserDto[] newArray(int size) {
            return new AppUserDto[size];
        }
    };
}
