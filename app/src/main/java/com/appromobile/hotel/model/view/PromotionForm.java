package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by xuan on 9/14/2016.
 */
public class PromotionForm implements Parcelable {

    public final static int TYPE_PROMOTION = 1;
    public final static int TYPE_EVENT = 2;
    public final static int TYPE_INVITE = 3;


    private boolean apply;
    private String applyEnd;
    private String applyStart;
    private String applyTime;
    private int approDiscount;
    private boolean banner;
    private int bannerSn;
    private String content;
    private String couponEnd;
    private String couponStart;
    private int discount;
    private int hotelDiscount;
    private String lastUpdate;
    private boolean newItem;
    private boolean popup;
    private int sn;
    private int sponsorDiscount;
    private String sponsorName;
    private int sponsorSn;
    private int status;
    private String title;
    private int totalApproDiscount;
    private int totalConsumedCoupon;
    private int totalCouponIssued;
    private int totalDiscount;
    private int totalHotelApplied;
    private int totalHotelDiscount;
    private int totalSponsorDiscount;
    private int totalUserApplied;
    private int type;
    private boolean winner;
    private List<PromotionImageForm> promotionImageFormList;
    private int applyType;
    private boolean canApply;
    private String code;
    private String memo;
    private int numActiveDay;

    public boolean isPopup() {
        return popup;
    }

    public void setPopup(boolean popup) {
        this.popup = popup;
    }

    public boolean isApply() {
        return apply;
    }

    public void setApply(boolean apply) {
        this.apply = apply;
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

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public int getApproDiscount() {
        return approDiscount;
    }

    public void setApproDiscount(int approDiscount) {
        this.approDiscount = approDiscount;
    }

    public boolean isBanner() {
        return banner;
    }

    public void setBanner(boolean banner) {
        this.banner = banner;
    }

    public int getBannerSn() {
        return bannerSn;
    }

    public void setBannerSn(int bannerSn) {
        this.bannerSn = bannerSn;
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

    public int getHotelDiscount() {
        return hotelDiscount;
    }

    public void setHotelDiscount(int hotelDiscount) {
        this.hotelDiscount = hotelDiscount;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isNewItem() {
        return newItem;
    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getSponsorDiscount() {
        return sponsorDiscount;
    }

    public void setSponsorDiscount(int sponsorDiscount) {
        this.sponsorDiscount = sponsorDiscount;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public int getSponsorSn() {
        return sponsorSn;
    }

    public void setSponsorSn(int sponsorSn) {
        this.sponsorSn = sponsorSn;
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

    public int getTotalApproDiscount() {
        return totalApproDiscount;
    }

    public void setTotalApproDiscount(int totalApproDiscount) {
        this.totalApproDiscount = totalApproDiscount;
    }

    public int getTotalConsumedCoupon() {
        return totalConsumedCoupon;
    }

    public void setTotalConsumedCoupon(int totalConsumedCoupon) {
        this.totalConsumedCoupon = totalConsumedCoupon;
    }

    public int getTotalCouponIssued() {
        return totalCouponIssued;
    }

    public void setTotalCouponIssued(int totalCouponIssued) {
        this.totalCouponIssued = totalCouponIssued;
    }

    public int getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(int totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public int getTotalHotelApplied() {
        return totalHotelApplied;
    }

    public void setTotalHotelApplied(int totalHotelApplied) {
        this.totalHotelApplied = totalHotelApplied;
    }

    public int getTotalHotelDiscount() {
        return totalHotelDiscount;
    }

    public void setTotalHotelDiscount(int totalHotelDiscount) {
        this.totalHotelDiscount = totalHotelDiscount;
    }

    public int getTotalSponsorDiscount() {
        return totalSponsorDiscount;
    }

    public void setTotalSponsorDiscount(int totalSponsorDiscount) {
        this.totalSponsorDiscount = totalSponsorDiscount;
    }

    public int getTotalUserApplied() {
        return totalUserApplied;
    }

    public void setTotalUserApplied(int totalUserApplied) {
        this.totalUserApplied = totalUserApplied;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public PromotionForm() {
    }

    public List<PromotionImageForm> getPromotionImageFormList() {
        return promotionImageFormList;
    }

    public void setPromotionImageFormList(List<PromotionImageForm> promotionImageFormList) {
        this.promotionImageFormList = promotionImageFormList;
    }

    public int getApplyType() {
        return applyType;
    }

    public void setApplyType(int applyType) {
        this.applyType = applyType;
    }

    public boolean isCanApply() {
        return canApply;
    }

    public void setCanApply(boolean canApply) {
        this.canApply = canApply;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getNumActiveDay() {
        return numActiveDay;
    }

    public void setNumActiveDay(int numActiveDay) {
        this.numActiveDay = numActiveDay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.apply ? (byte) 1 : (byte) 0);
        dest.writeString(this.applyEnd);
        dest.writeString(this.applyStart);
        dest.writeString(this.applyTime);
        dest.writeInt(this.approDiscount);
        dest.writeByte(this.banner ? (byte) 1 : (byte) 0);
        dest.writeInt(this.bannerSn);
        dest.writeString(this.content);
        dest.writeString(this.couponEnd);
        dest.writeString(this.couponStart);
        dest.writeInt(this.discount);
        dest.writeInt(this.hotelDiscount);
        dest.writeString(this.lastUpdate);
        dest.writeInt(this.newItem ? (byte) 1 : (byte) 0);
        dest.writeByte(this.popup ? (byte) 1 : (byte) 0);
        dest.writeInt(this.sn);
        dest.writeInt(this.sponsorDiscount);
        dest.writeString(this.sponsorName);
        dest.writeInt(this.sponsorSn);
        dest.writeInt(this.status);
        dest.writeString(this.title);
        dest.writeInt(this.totalApproDiscount);
        dest.writeInt(this.totalConsumedCoupon);
        dest.writeInt(this.totalCouponIssued);
        dest.writeInt(this.totalDiscount);
        dest.writeInt(this.totalHotelApplied);
        dest.writeInt(this.totalHotelDiscount);
        dest.writeInt(this.totalSponsorDiscount);
        dest.writeInt(this.totalUserApplied);
        dest.writeInt(this.type);
        dest.writeByte(this.winner ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.promotionImageFormList);
        dest.writeInt(this.applyType);
        dest.writeByte(this.canApply ? (byte) 1 : (byte) 0);
        dest.writeString(this.code);
        dest.writeString(this.memo);
        dest.writeInt(this.numActiveDay);
    }

    protected PromotionForm(Parcel in) {
        this.apply = in.readByte() != 0;
        this.applyEnd = in.readString();
        this.applyStart = in.readString();
        this.applyTime = in.readString();
        this.approDiscount = in.readInt();
        this.banner = in.readByte() != 0;
        this.bannerSn = in.readInt();
        this.content = in.readString();
        this.couponEnd = in.readString();
        this.couponStart = in.readString();
        this.discount = in.readInt();
        this.hotelDiscount = in.readInt();
        this.lastUpdate = in.readString();
        this.newItem = in.readByte() != 0;
        this.popup = in.readByte() != 0;
        this.sn = in.readInt();
        this.sponsorDiscount = in.readInt();
        this.sponsorName = in.readString();
        this.sponsorSn = in.readInt();
        this.status = in.readInt();
        this.title = in.readString();
        this.totalApproDiscount = in.readInt();
        this.totalConsumedCoupon = in.readInt();
        this.totalCouponIssued = in.readInt();
        this.totalDiscount = in.readInt();
        this.totalHotelApplied = in.readInt();
        this.totalHotelDiscount = in.readInt();
        this.totalSponsorDiscount = in.readInt();
        this.totalUserApplied = in.readInt();
        this.type = in.readInt();
        this.winner = in.readByte() != 0;
        this.promotionImageFormList = in.createTypedArrayList(PromotionImageForm.CREATOR);
        this.applyType = in.readInt();
        this.canApply = in.readByte() != 0;
        this.code = in.readString();
        this.memo = in.readString();
        this.numActiveDay = in.readInt();

    }

    public static final Creator<PromotionForm> CREATOR = new Creator<PromotionForm>() {
        @Override
        public PromotionForm createFromParcel(Parcel source) {
            return new PromotionForm(source);
        }

        @Override
        public PromotionForm[] newArray(int size) {
            return new PromotionForm[size];
        }
    };
}
