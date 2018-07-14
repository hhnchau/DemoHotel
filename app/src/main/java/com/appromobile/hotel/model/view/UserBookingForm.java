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
    private Long couponIssuedSn;
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
    private String paymentCode;
    private int discountPrice;
    private int mileageAmount;
    private int mileagePoint;
    private int fsGo2joyDiscount;
    private String transactionId;
    private int promotionDiscount;
    private int paymentProvider; //1:123Pay, 2:Payoo, 3:Momo ,
    private boolean hasPaymentPromotion;

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
        if (in.readByte() == 0) {
            couponIssuedSn = null;
        } else {
            couponIssuedSn = in.readLong();
        }
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
        paymentCode = in.readString();
        discountPrice = in.readInt();
        mileageAmount = in.readInt();
        mileagePoint = in.readInt();
        fsGo2joyDiscount = in.readInt();
        promotionDiscount = in.readInt();
        transactionId = in.readString();
        paymentProvider = in.readInt();
        hasPaymentPromotion = in.readByte() != 0;
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

    public Long getCouponIssuedSn() {
        return couponIssuedSn;
    }

    public void setCouponIssuedSn(Long couponIssuedSn) {
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

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getMileageAmount() {
        return mileageAmount;
    }

    public UserBookingForm setMileageAmount(int mileageAmount) {
        this.mileageAmount = mileageAmount;
        return this;
    }

    public int getMileagePoint() {
        return mileagePoint;
    }

    public int getFsGo2joyDiscount() {
        return fsGo2joyDiscount;
    }

    public UserBookingForm setFsGo2joyDiscount(int fsGo2joyDiscount) {
        this.fsGo2joyDiscount = fsGo2joyDiscount;
        return this;
    }

    public UserBookingForm setMileagePoint(int mileagePoint) {
        this.mileagePoint = mileagePoint;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public UserBookingForm setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    public UserBookingForm setPromotionDiscount(int promotionDiscount) {
        this.promotionDiscount = promotionDiscount;
        return this;
    }

    public int getPaymentProvider() {
        return paymentProvider;
    }

    public UserBookingForm setPaymentProvider(int paymentProvider) {
        this.paymentProvider = paymentProvider;
        return this;
    }

    public boolean isHasPaymentPromotion() {
        return hasPaymentPromotion;
    }

    public UserBookingForm setHasPaymentPromotion(boolean hasPaymentPromotion) {
        this.hasPaymentPromotion = hasPaymentPromotion;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(additionalHours);
        parcel.writeInt(amountAfterCommission);
        parcel.writeInt(amountFromUser);
        parcel.writeString(appUserNickName);
        parcel.writeInt(appUserSn);
        parcel.writeInt(bookingNo);
        parcel.writeInt(bookingStatus);
        parcel.writeString(checkInDatePlan);
        parcel.writeString(checkInTime);
        parcel.writeInt(commissionAmount);
        parcel.writeInt(confirmed);
        if (couponIssuedSn == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(couponIssuedSn);
        }
        parcel.writeString(couponName);
        parcel.writeString(createTime);
        parcel.writeInt(discount);
        parcel.writeString(endDate);
        parcel.writeString(endTime);
        parcel.writeInt(firstHours);
        parcel.writeString(hotelAddress);
        parcel.writeString(hotelName);
        parcel.writeInt(hotelSn);
        parcel.writeInt(hotelStatus);
        parcel.writeByte((byte) (inPast ? 1 : 0));
        parcel.writeString(lastUpdate);
        parcel.writeInt(memberId);
        parcel.writeString(mobile);
        parcel.writeByte((byte) (prepay ? 1 : 0));
        parcel.writeInt(priceAdditionalHours);
        parcel.writeInt(priceFirstHours);
        parcel.writeInt(priceOneDay);
        parcel.writeInt(priceOvernight);
        parcel.writeString(roomTypeName);
        parcel.writeInt(roomTypeSn);
        parcel.writeInt(sn);
        parcel.writeString(startTime);
        parcel.writeInt(totalAmount);
        parcel.writeInt(type);
        parcel.writeInt(paymentOption);
        parcel.writeInt(discountType);
        parcel.writeInt(maxDiscount);
        parcel.writeParcelable(donateCoupon, i);
        parcel.writeString(checkinCode);
        parcel.writeInt(prepayAmount);
        parcel.writeInt(redeemValue);
        parcel.writeString(paymentCode);
        parcel.writeInt(discountPrice);
        parcel.writeInt(mileageAmount);
        parcel.writeInt(mileagePoint);
        parcel.writeInt(fsGo2joyDiscount);
        parcel.writeInt(promotionDiscount);
        parcel.writeString(transactionId);
        parcel.writeInt(paymentProvider);
        parcel.writeByte((byte) (hasPaymentPromotion ? 1 : 0));
    }
}
