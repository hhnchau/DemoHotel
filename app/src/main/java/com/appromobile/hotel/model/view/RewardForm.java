package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by appro on 22/03/2017.
 */
public class RewardForm {

    private String applyEnd;
    private String applyStart;
    private String content;
    private String couponEnd;
    private String couponStart;
    private int discount;
    private String lastUpdate;
    private int memberId;
    private int numOfCoupon;
    private String originalName;
    private int sn;
    private int status;
    private String title;
    private int type;

    public RewardForm() {
    }


    public int getNumOfCoupon() {
        return numOfCoupon;
    }

    public void setNumOfCoupon(int numOfCoupon) {
        this.numOfCoupon = numOfCoupon;
    }

    public String getApplyEnd() {
        return applyEnd;
    }

    public void setApplyEnd(String applyEnd) {
        this.applyEnd = applyEnd;
    }

    public String getApplyStart() {
        return applyStart;
    }

    public void setApplyStart(String applyStart) {
        this.applyStart = applyStart;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCouponEnd() {
        return couponEnd;
    }

    public void setCouponEnd(String couponEnd) {
        this.couponEnd = couponEnd;
    }

    public String getCouponStart() {
        return couponStart;
    }

    public void setCouponStart(String couponStart) {
        this.couponStart = couponStart;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
