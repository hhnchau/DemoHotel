package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 8/19/2016.
 */
public class UserBookingForm implements Parcelable {

    private int additionalHours;
    private int amountAfterCommission;
    private int amountFromUser;
    private String appUserNickName;
    private int appUserSn;
    private int bookingNo;
    private int bookingStatus;
    private String checkInDatePlan;
    private String checkInTime;
    private int commissionAmount;
    private int confirmed;
    private int couponIssuedSn;
    private String couponName;
    private String createTime;
    private int discount;
    private String endDate;
    private String endTime;
    private int firstHours;
    private String hotelAddress;
    private String hotelName;
    private int hotelSn;
    private int hotelStatus;
    private boolean inPast;
    private String lastUpdate;
    private int memberId;
    private String mobile;
    private boolean prepay;
    private int priceAdditionalHours;
    private int priceFirstHours;
    private int priceOneDay;
    private int priceOvernight;
    private String roomTypeName;
    private int roomTypeSn;
    private int sn;
    private String startTime;
    private int totalAmount;
    private int type;
    private int paymentOption;
    private int discountType;
    private int maxDiscount;
    private CouponForm donateCoupon;
    private String checkinCode;
    private int prepayAmount;
    private int redeemValue;

    public UserBookingForm() {
    }

    protected UserBookingForm(Parcel in) {
        additionalHours = in.readInt();
        amountAfterCommission = in.readInt();
        amountFromUser = in.readInt();
        appUserNickName = in.readString();
        appUserSn = in.readInt();
        bookingNo = in.readInt();
        bookingStatus = in.readInt();
        checkInDatePlan = in.readString();
        checkInTime = in.readString();
        commissionAmount = in.readInt();
        confirmed = in.readInt();
        couponIssuedSn = in.readInt();
        couponName = in.readString();
        createTime = in.readString();
        discount = in.readInt();
        endDate = in.readString();
        endTime = in.readString();
        firstHours = in.readInt();
        hotelAddress = in.readString();
        hotelName = in.readString();
        hotelSn = in.readInt();
        hotelStatus = in.readInt();
        inPast = in.readByte() != 0;
        lastUpdate = in.readString();
        memberId = in.readInt();
        mobile = in.readString();
        prepay = in.readByte() != 0;
        priceAdditionalHours = in.readInt();
        priceFirstHours = in.readInt();
        priceOneDay = in.readInt();
        priceOvernight = in.readInt();
        roomTypeName = in.readString();
        roomTypeSn = in.readInt();
        sn = in.readInt();
        startTime = in.readString();
        totalAmount = in.readInt();
        type = in.readInt();
        paymentOption = in.readInt();
        discountType = in.readInt();
        maxDiscount = in.readInt();
        donateCoupon = in.readParcelable(CouponForm.class.getClassLoader());
        checkinCode = in.readString();
        prepayAmount = in.readInt();
        redeemValue = in.readInt();
    }

    public static final Creator<UserBookingForm> CREATOR = new Creator<UserBookingForm>() {
        @Override
        public UserBookingForm createFromParcel(Parcel in) {
            return new UserBookingForm(in);
        }

        @Override
        public UserBookingForm[] newArray(int size) {
            return new UserBookingForm[size];
        }
    };

    public int getAdditionalHours() {
        return additionalHours;
    }

    public void setAdditionalHours(int additionalHours) {
        this.additionalHours = additionalHours;
    }

    public int getAmountAfterCommission() {
        return amountAfterCommission;
    }

    public void setAmountAfterCommission(int amountAfterCommission) {
        this.amountAfterCommission = amountAfterCommission;
    }

    public int getAmountFromUser() {
        return amountFromUser;
    }

    public void setAmountFromUser(int amountFromUser) {
        this.amountFromUser = amountFromUser;
    }

    public String getAppUserNickName() {
        return appUserNickName;
    }

    public void setAppUserNickName(String appUserNickName) {
        this.appUserNickName = appUserNickName;
    }

    public int getAppUserSn() {
        return appUserSn;
    }

    public void setAppUserSn(int appUserSn) {
        this.appUserSn = appUserSn;
    }

    public int getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(int bookingNo) {
        this.bookingNo = bookingNo;
    }

    public int getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(int bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getCheckInDatePlan() {
        return checkInDatePlan;
    }

    public void setCheckInDatePlan(String checkInDatePlan) {
        this.checkInDatePlan = checkInDatePlan;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public int getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(int commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getCouponIssuedSn() {
        return couponIssuedSn;
    }

    public void setCouponIssuedSn(int couponIssuedSn) {
        this.couponIssuedSn = couponIssuedSn;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getFirstHours() {
        return firstHours;
    }

    public void setFirstHours(int firstHours) {
        this.firstHours = firstHours;
    }

    public String getHotelAddress() {
        return hotelAddress;
    }

    public void setHotelAddress(String hotelAddress) {
        this.hotelAddress = hotelAddress;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public int getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
    }

    public int getHotelStatus() {
        return hotelStatus;
    }

    public void setHotelStatus(int hotelStatus) {
        this.hotelStatus = hotelStatus;
    }

    public boolean isInPast() {
        return inPast;
    }

    public void setInPast(boolean inPast) {
        this.inPast = inPast;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isPrepay() {
        return prepay;
    }

    public void setPrepay(boolean prepay) {
        this.prepay = prepay;
    }

    public int getPriceAdditionalHours() {
        return priceAdditionalHours;
    }

    public void setPriceAdditionalHours(int priceAdditionalHours) {
        this.priceAdditionalHours = priceAdditionalHours;
    }

    public int getPriceFirstHours() {
        return priceFirstHours;
    }

    public void setPriceFirstHours(int priceFirstHours) {
        this.priceFirstHours = priceFirstHours;
    }

    public int getPriceOneDay() {
        return priceOneDay;
    }

    public void setPriceOneDay(int priceOneDay) {
        this.priceOneDay = priceOneDay;
    }

    public int getPriceOvernight() {
        return priceOvernight;
    }

    public void setPriceOvernight(int priceOvernight) {
        this.priceOvernight = priceOvernight;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public int getRoomTypeSn() {
        return roomTypeSn;
    }

    public void setRoomTypeSn(int roomTypeSn) {
        this.roomTypeSn = roomTypeSn;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(int paymentOption) {
        this.paymentOption = paymentOption;
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

    public CouponForm getDonateCoupon() {
        return donateCoupon;
    }

    public void setDonateCoupon(CouponForm donateCoupon) {
        this.donateCoupon = donateCoupon;
    }

    public String getCheckinCode() {
        return checkinCode;
    }

    public void setCheckinCode(String checkinCode) {
        this.checkinCode = checkinCode;
    }

    public int getPrepayAmount() {
        return prepayAmount;
    }

    public void setPrepayAmount(int prepayAmount) {
        this.prepayAmount = prepayAmount;
    }

    public int getRedeemValue() {
        return redeemValue;
    }

    public void setRedeemValue(int redeemValue) {
        this.redeemValue = redeemValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(additionalHours);
        dest.writeInt(amountAfterCommission);
        dest.writeInt(amountFromUser);
        dest.writeString(appUserNickName);
        dest.writeInt(appUserSn);
        dest.writeInt(bookingNo);
        dest.writeInt(bookingStatus);
        dest.writeString(checkInDatePlan);
        dest.writeString(checkInTime);
        dest.writeInt(commissionAmount);
        dest.writeInt(confirmed);
        dest.writeInt(couponIssuedSn);
        dest.writeString(couponName);
        dest.writeString(createTime);
        dest.writeInt(discount);
        dest.writeString(endDate);
        dest.writeString(endTime);
        dest.writeInt(firstHours);
        dest.writeString(hotelAddress);
        dest.writeString(hotelName);
        dest.writeInt(hotelSn);
        dest.writeInt(hotelStatus);
        dest.writeByte((byte) (inPast ? 1 : 0));
        dest.writeString(lastUpdate);
        dest.writeInt(memberId);
        dest.writeString(mobile);
        dest.writeByte((byte) (prepay ? 1 : 0));
        dest.writeInt(priceAdditionalHours);
        dest.writeInt(priceFirstHours);
        dest.writeInt(priceOneDay);
        dest.writeInt(priceOvernight);
        dest.writeString(roomTypeName);
        dest.writeInt(roomTypeSn);
        dest.writeInt(sn);
        dest.writeString(startTime);
        dest.writeInt(totalAmount);
        dest.writeInt(type);
        dest.writeInt(paymentOption);
        dest.writeInt(discountType);
        dest.writeInt(maxDiscount);
        dest.writeParcelable(donateCoupon, flags);
        dest.writeString(checkinCode);
        dest.writeInt(prepayAmount);
        dest.writeInt(redeemValue);
    }
}
