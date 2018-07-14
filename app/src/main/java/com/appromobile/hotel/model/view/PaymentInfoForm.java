package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 12/15/2016.
 */

public class PaymentInfoForm implements Parcelable {
    private String address;
    private String bankCode;
    private String birthday;
    private String cancelUrl;
    private String checksum;
    private String createOrderLink;
    private String email;
    private String errorUrl;
    private String gender;
    private String merchantCode;
    private String mobile;
    private String nickName;
    private String passcode;
    private String redirectUrl;
    private String totalAmount;
    private String transactionId;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
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

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeString(this.bankCode);
        dest.writeString(this.birthday);
        dest.writeString(this.cancelUrl);
        dest.writeString(this.checksum);
        dest.writeString(this.createOrderLink);
        dest.writeString(this.email);
        dest.writeString(this.errorUrl);
        dest.writeString(this.gender);
        dest.writeString(this.merchantCode);
        dest.writeString(this.mobile);
        dest.writeString(this.nickName);
        dest.writeString(this.passcode);
        dest.writeString(this.redirectUrl);
        dest.writeString(this.totalAmount);
        dest.writeString(this.transactionId);
    }

    public PaymentInfoForm() {
    }

    protected PaymentInfoForm(Parcel in) {
        this.address = in.readString();
        this.bankCode = in.readString();
        this.birthday = in.readString();
        this.cancelUrl = in.readString();
        this.checksum = in.readString();
        this.createOrderLink = in.readString();
        this.email = in.readString();
        this.errorUrl = in.readString();
        this.gender = in.readString();
        this.merchantCode = in.readString();
        this.mobile = in.readString();
        this.nickName = in.readString();
        this.passcode = in.readString();
        this.redirectUrl = in.readString();
        this.totalAmount = in.readString();
        this.transactionId = in.readString();
    }

    public static final Parcelable.Creator<PaymentInfoForm> CREATOR = new Parcelable.Creator<PaymentInfoForm>() {
        @Override
        public PaymentInfoForm createFromParcel(Parcel source) {
            return new PaymentInfoForm(source);
        }

        @Override
        public PaymentInfoForm[] newArray(int size) {
            return new PaymentInfoForm[size];
        }
    };
}
