package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 12/13/2016.
 */

public class CreateOrderPayment implements Parcelable {
    private String transactionId;
    private String merchantCode;
    private String bankCode;
    private String nickName;
    private String address;
    private String totalAmount;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getCreateOrderLink() {
        return createOrderLink;
    }

    public void setCreateOrderLink(String createOrderLink) {
        this.createOrderLink = createOrderLink;
    }

    private String gender;
    private String birthday;
    private String mobile;
    private String email;
    private String cancelUrl;
    private String redirectUrl;
    private String errorUrl;
    private String passcode;
    private String checksum;
    private String createOrderLink;

    public CreateOrderPayment() {
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.transactionId);
        dest.writeString(this.merchantCode);
        dest.writeString(this.bankCode);
        dest.writeString(this.nickName);
        dest.writeString(this.address);
        dest.writeString(this.totalAmount);
        dest.writeString(this.gender);
        dest.writeString(this.birthday);
        dest.writeString(this.mobile);
        dest.writeString(this.email);
        dest.writeString(this.cancelUrl);
        dest.writeString(this.redirectUrl);
        dest.writeString(this.errorUrl);
        dest.writeString(this.passcode);
        dest.writeString(this.checksum);
        dest.writeString(this.createOrderLink);
    }

    protected CreateOrderPayment(Parcel in) {
        this.transactionId = in.readString();
        this.merchantCode = in.readString();
        this.bankCode = in.readString();
        this.nickName = in.readString();
        this.address = in.readString();
        this.totalAmount = in.readString();
        this.gender = in.readString();
        this.birthday = in.readString();
        this.mobile = in.readString();
        this.email = in.readString();
        this.cancelUrl = in.readString();
        this.redirectUrl = in.readString();
        this.errorUrl = in.readString();
        this.passcode = in.readString();
        this.checksum = in.readString();
        this.createOrderLink = in.readString();
    }

    public static final Creator<CreateOrderPayment> CREATOR = new Creator<CreateOrderPayment>() {
        @Override
        public CreateOrderPayment createFromParcel(Parcel source) {
            return new CreateOrderPayment(source);
        }

        @Override
        public CreateOrderPayment[] newArray(int size) {
            return new CreateOrderPayment[size];
        }
    };
}
