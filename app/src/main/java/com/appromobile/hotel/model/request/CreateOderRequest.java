package com.appromobile.hotel.model.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuan on 12/13/2016.
 */

public class CreateOderRequest {
    public String getmTransactionID() {
        return mTransactionID;
    }

    public void setmTransactionID(String mTransactionID) {
        this.mTransactionID = mTransactionID;
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

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustGender() {
        return custGender;
    }

    public void setCustGender(String custGender) {
        this.custGender = custGender;
    }

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public String getCustDOB() {
        return custDOB;
    }

    public void setCustDOB(String custDOB) {
        this.custDOB = custDOB;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    public String getCustMail() {
        return custMail;
    }

    public void setCustMail(String custMail) {
        this.custMail = custMail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCancelURL() {
        return cancelURL;
    }

    public void setCancelURL(String cancelURL) {
        this.cancelURL = cancelURL;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getErrorURL() {
        return errorURL;
    }

    public void setErrorURL(String errorURL) {
        this.errorURL = errorURL;
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

    private String mTransactionID;
    private String merchantCode;
    private String bankCode;
    private String totalAmount;
    private String clientIP;
    private String custName;
    private String custGender;
    private String custAddress;
    private String custDOB;
    private String custPhone;
    private String custMail;
    private String description;
    private String cancelURL;
    private String redirectURL;
    private String errorURL;
    private String passcode;
    private String checksum;

    public Map<String, String> getMapValues() {
        Map<String, String> params = new HashMap<>();
        params.put("mTransactionID", mTransactionID);
        params.put("merchantCode", merchantCode);
        params.put("bankCode", bankCode);
        params.put("totalAmount", totalAmount);
        params.put("clientIP", clientIP);
        params.put("custName", custName);
        params.put("custGender", custGender);
        params.put("custAddress", custAddress);
        params.put("custDOB", custDOB);
        params.put("custPhone", custPhone);
        params.put("custMail", custMail);
//        params.put("description", description);
        params.put("passcode", passcode);
        params.put("checksum", checksum);
        params.put("errorURL", errorURL);
        params.put("cancelURL", cancelURL);
        params.put("redirectURL", redirectURL);
        return params;
    }
}
