package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by xuan on 7/18/2016.
 */
public class HotelDetailForm implements Parcelable {
    private String address;
    private List<FacilityForm> facilityFormList;
    private int favorite;
    private String areaCode;
    private int hasPromotion;
    private int hotHotel;
    private List<HotelImageForm> hotelImageList;
    private int hotelStatus;
    private String impressMemo;
    private double latitude;
    private double longitude;
    private String name;
    private int newHotel;
    private String phone;
    private List<RoomTypeForm> roomTypeList;
    private int sn;
    private int totalFavorite;
    private int totalReview;
    private String description;
    private double averageMark;
    private int checkin;
    private int checkout;
    private double cancelBeforeHours;
    private int startOvernight;
    private int endOvernight;
    private int countExifImage;
    private int lowestPriceOvernight;
    private int lowestPrice;
    private int activeStamp;
    private int numToRedeem;
    private RoomApplyPromotion roomApplyPromotion;
    private int firstHours;
    private String imageKey;

    protected HotelDetailForm(Parcel in) {
        address = in.readString();
        facilityFormList = in.createTypedArrayList(FacilityForm.CREATOR);
        favorite = in.readInt();
        areaCode = in.readString();
        hasPromotion = in.readInt();
        hotHotel = in.readInt();
        hotelImageList = in.createTypedArrayList(HotelImageForm.CREATOR);
        hotelStatus = in.readInt();
        impressMemo = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
        newHotel = in.readInt();
        phone = in.readString();
        roomTypeList = in.createTypedArrayList(RoomTypeForm.CREATOR);
        sn = in.readInt();
        totalFavorite = in.readInt();
        totalReview = in.readInt();
        description = in.readString();
        averageMark = in.readDouble();
        checkin = in.readInt();
        checkout = in.readInt();
        cancelBeforeHours = in.readDouble();
        startOvernight = in.readInt();
        endOvernight = in.readInt();
        countExifImage = in.readInt();
        lowestPrice = in.readInt();
        lowestPriceOvernight = in.readInt();
        activeStamp = in.readInt();
        numToRedeem = in.readInt();
        roomApplyPromotion = in.readParcelable(RoomApplyPromotion.class.getClassLoader());
        firstHours = in.readInt();
        imageKey = in.readString();
    }

    public static final Creator<HotelDetailForm> CREATOR = new Creator<HotelDetailForm>() {
        @Override
        public HotelDetailForm createFromParcel(Parcel in) {
            return new HotelDetailForm(in);
        }

        @Override
        public HotelDetailForm[] newArray(int size) {
            return new HotelDetailForm[size];
        }
    };

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCheckin() {
        return checkin;
    }

    public void setCheckin(int checkin) {
        this.checkin = checkin;
    }

    public int getCheckout() {
        return checkout;
    }

    public void setCheckout(int checkout) {
        this.checkout = checkout;
    }

    public double getCancelBeforeHours() {
        return cancelBeforeHours;
    }

    public void setCancelBeforeHours(double cancelBeforeHours) {
        this.cancelBeforeHours = cancelBeforeHours;
    }

    public int getStartOvernight() {
        return startOvernight;
    }

    public void setStartOvernight(int startOvernight) {
        this.startOvernight = startOvernight;
    }

    public int getEndOvernight() {
        return endOvernight;
    }

    public void setEndOvernight(int endOvernight) {
        this.endOvernight = endOvernight;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getHasPromotion() {
        return hasPromotion;
    }

    public void setHasPromotion(int hasPromotion) {
        this.hasPromotion = hasPromotion;
    }

    public int getHotHotel() {
        return hotHotel;
    }

    public void setHotHotel(int hotHotel) {
        this.hotHotel = hotHotel;
    }

    public List<HotelImageForm> getHotelImageList() {
        return hotelImageList;
    }

    public void setHotelImageList(List<HotelImageForm> hotelImageList) {
        this.hotelImageList = hotelImageList;
    }

    public int getHotelStatus() {
        return hotelStatus;
    }

    public void setHotelStatus(int hotelStatus) {
        this.hotelStatus = hotelStatus;
    }

    public String getImpressMemo() {
        return impressMemo;
    }

    public void setImpressMemo(String impressMemo) {
        this.impressMemo = impressMemo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNewHotel() {
        return newHotel;
    }

    public void setNewHotel(int newHotel) {
        this.newHotel = newHotel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<RoomTypeForm> getRoomTypeList() {
        return roomTypeList;
    }

    public void setRoomTypeList(List<RoomTypeForm> roomTypeList) {
        this.roomTypeList = roomTypeList;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getTotalFavorite() {
        return totalFavorite;
    }

    public void setTotalFavorite(int totalFavorite) {
        this.totalFavorite = totalFavorite;
    }

    public int getTotalReview() {
        return totalReview;
    }

    public void setTotalReview(int totalReview) {
        this.totalReview = totalReview;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAverageMark() {
        return averageMark;
    }

    public void setAverageMark(double averageMark) {
        this.averageMark = averageMark;
    }

    public int getCountExifImage() {
        return countExifImage;
    }

    public void setCountExifImage(int countExifImage) {
        this.countExifImage = countExifImage;
    }

    public int getActiveStamp() {
        return activeStamp;
    }

    public void setActiveStamp(int activeStamp) {
        this.activeStamp = activeStamp;
    }

    public int getNumToRedeem() {
        return numToRedeem;
    }

    public void setNumToRedeem(int numToRedeem) {
        this.numToRedeem = numToRedeem;
    }

    public HotelDetailForm() {
    }

    public List<FacilityForm> getFacilityFormList() {
        return facilityFormList;
    }

    public void setFacilityFormList(List<FacilityForm> facilityFormList) {
        this.facilityFormList = facilityFormList;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public int getLowestPriceOvernight() {
        return lowestPriceOvernight;
    }

    public void setLowestPriceOvernight(int lowestPriceOvernight) {
        this.lowestPriceOvernight = lowestPriceOvernight;
    }

    public int getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(int lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public RoomApplyPromotion getRoomApplyPromotion() {
        return roomApplyPromotion;
    }

    public HotelDetailForm setRoomApplyPromotion(RoomApplyPromotion roomApplyPromotion) {
        this.roomApplyPromotion = roomApplyPromotion;
        return this;
    }

    public int getFirstHours() {
        return firstHours;
    }

    public HotelDetailForm setFirstHours(int firstHours) {
        this.firstHours = firstHours;
        return this;
    }

    public String getImageKey() {
        return imageKey;
    }

    public HotelDetailForm setImageKey(String imageKey) {
        this.imageKey = imageKey;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeTypedList(facilityFormList);
        dest.writeInt(favorite);
        dest.writeString(areaCode);
        dest.writeInt(hasPromotion);
        dest.writeInt(hotHotel);
        dest.writeTypedList(hotelImageList);
        dest.writeInt(hotelStatus);
        dest.writeString(impressMemo);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeInt(newHotel);
        dest.writeString(phone);
        dest.writeTypedList(roomTypeList);
        dest.writeInt(sn);
        dest.writeInt(totalFavorite);
        dest.writeInt(totalReview);
        dest.writeString(description);
        dest.writeDouble(averageMark);
        dest.writeInt(checkin);
        dest.writeInt(checkout);
        dest.writeDouble(cancelBeforeHours);
        dest.writeInt(startOvernight);
        dest.writeInt(endOvernight);
        dest.writeInt(countExifImage);
        dest.writeInt(lowestPrice);
        dest.writeInt(lowestPriceOvernight);
        dest.writeInt(activeStamp);
        dest.writeInt(numToRedeem);
        dest.writeParcelable(roomApplyPromotion, flags);
        dest.writeInt(firstHours);
        dest.writeString(imageKey);
    }

    public int checkFlashSale() {
        int p = -1;
        for (int i = 0; i < roomTypeList.size(); i++) {
            if (roomTypeList.get(i).isFlashSale()) {
                p = i;
            }
        }
        return p;
    }

    public int checkCineJoy() {
        int p = -1;
        for (int i = 0; i < roomTypeList.size(); i++) {
            if (roomTypeList.get(i).isCinema()) {
                p = i;
            }
        }
        return p;
    }

    public RoomTypeForm getFlashSaleRoomTypeForm() {
        for (int i = 0; i < roomTypeList.size(); i++) {
            if (roomTypeList.get(i).getRoomType().equals("FlashSale")) {
                return roomTypeList.get(i);
            }
        }
        return null;
    }
}
