package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by xuan on 8/19/2016.
 */
public class CouponIssuedForm implements Parcelable {
    private int appUserSn;
    private String end;
    private String issueTime;
    private String lastUpdate;
    private String promotionName;
    private int promotionSn;
    private int sn;
    private String start;
    private int used;
    private int discount;
    private String usedTime;
    private String userNickname;
    private String code;
    private int rewardSn;
    private int couponSn;
    private int discountType;
    private int maxDiscount;// 0: No limit ,
    private int canUse;
    private CouponConditionForm couponConditionForm;
    private String couponMemo;

    public CouponIssuedForm() {
    }

    protected CouponIssuedForm(Parcel in) {
        appUserSn = in.readInt();
        end = in.readString();
        issueTime = in.readString();
        lastUpdate = in.readString();
        promotionName = in.readString();
        promotionSn = in.readInt();
        sn = in.readInt();
        start = in.readString();
        used = in.readInt();
        discount = in.readInt();
        usedTime = in.readString();
        userNickname = in.readString();
        code = in.readString();
        rewardSn = in.readInt();
        couponSn = in.readInt();
        discountType = in.readInt();
        maxDiscount = in.readInt();
        canUse = in.readInt();
        couponConditionForm = in.readParcelable(CouponConditionForm.class.getClassLoader());
        couponMemo = in.readString();
    }

    public static final Creator<CouponIssuedForm> CREATOR = new Creator<CouponIssuedForm>() {
        @Override
        public CouponIssuedForm createFromParcel(Parcel in) {
            return new CouponIssuedForm(in);
        }

        @Override
        public CouponIssuedForm[] newArray(int size) {
            return new CouponIssuedForm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(appUserSn);
        dest.writeString(end);
        dest.writeString(issueTime);
        dest.writeString(lastUpdate);
        dest.writeString(promotionName);
        dest.writeInt(promotionSn);
        dest.writeInt(sn);
        dest.writeString(start);
        dest.writeInt(used);
        dest.writeInt(discount);
        dest.writeString(usedTime);
        dest.writeString(userNickname);
        dest.writeString(code);
        dest.writeInt(rewardSn);
        dest.writeInt(couponSn);
        dest.writeInt(discountType);
        dest.writeInt(maxDiscount);
        dest.writeInt(canUse);
        dest.writeParcelable(couponConditionForm, flags);
        dest.writeString(couponMemo);
    }

    public int getAppUserSn() {
        return appUserSn;
    }

    public void setAppUserSn(int appUserSn) {
        this.appUserSn = appUserSn;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public int getPromotionSn() {
        return promotionSn;
    }

    public void setPromotionSn(int promotionSn) {
        this.promotionSn = promotionSn;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(String usedTime) {
        this.usedTime = usedTime;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRewardSn() {
        return rewardSn;
    }

    public void setRewardSn(int rewardSn) {
        this.rewardSn = rewardSn;
    }

    public int getCouponSn() {
        return couponSn;
    }

    public void setCouponSn(int couponSn) {
        this.couponSn = couponSn;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public int getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(int maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public int getCanUse() {
        return canUse;
    }

    public void setCanUse(int canUse) {
        this.canUse = canUse;
    }

    public CouponConditionForm getCouponConditionForm() {
        return couponConditionForm;
    }

    public void setCouponConditionForm(CouponConditionForm couponConditionForm) {
        this.couponConditionForm = couponConditionForm;
    }

    public String getCouponMemo() {
        if (TextUtils.isEmpty(couponMemo)){
            return "";
        }
        return couponMemo;
    }

    public void setCouponMemo(String couponMemo) {
        this.couponMemo = couponMemo;
    }
}