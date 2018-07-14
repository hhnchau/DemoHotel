package com.appromobile.hotel.model.view;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.activity.MainActivity;
import com.appromobile.hotel.enums.RoomType;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;

import java.util.List;

/**
 * Created by xuan on 7/4/2016.
 */
public class HotelForm implements Parcelable {
    private String address;
    private double distance;
    private int districtSn;
    private int favorite;
    private int hasPromotion;
    private int homeImageSn;
    private String homeImageName;
    private int hotHotel;
    private int hotelUserSn;
    private String impressMemo;
    private double latitude;
    private double longitude;
    private String name;
    private int newHotel;
    private String phone;
    private int sn;
    private int hotelStatus = 0;
    private int totalFavorite;
    private double averageMark;
    private int roomAvailable;
    private boolean isCategory;
    private String categoryName;
    private List<RoomTypeForm> roomTypeFormList;
    private int lowestPrice;
    private int lowestPriceOvernight;
    private RoomTypeForm flashSaleRoomTypeForm;
    private int countExifImage;
    private int activeStamp;
    private int numToRedeem;
    private int firstHours;
    private String imageKey;

    public HotelForm() {
    }

    protected HotelForm(Parcel in) {
        address = in.readString();
        distance = in.readDouble();
        districtSn = in.readInt();
        favorite = in.readInt();
        hasPromotion = in.readInt();
        homeImageSn = in.readInt();
        homeImageName = in.readString();
        hotHotel = in.readInt();
        hotelUserSn = in.readInt();
        impressMemo = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
        newHotel = in.readInt();
        phone = in.readString();
        sn = in.readInt();
        hotelStatus = in.readInt();
        totalFavorite = in.readInt();
        averageMark = in.readDouble();
        roomAvailable = in.readInt();
        isCategory = in.readByte() != 0;
        categoryName = in.readString();
        roomTypeFormList = in.createTypedArrayList(RoomTypeForm.CREATOR);
        lowestPrice = in.readInt();
        lowestPriceOvernight = in.readInt();
        flashSaleRoomTypeForm = in.readParcelable(RoomTypeForm.class.getClassLoader());
        countExifImage = in.readInt();
        activeStamp = in.readInt();
        numToRedeem = in.readInt();
        firstHours = in.readInt();
        imageKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeDouble(distance);
        dest.writeInt(districtSn);
        dest.writeInt(favorite);
        dest.writeInt(hasPromotion);
        dest.writeInt(homeImageSn);
        dest.writeString(homeImageName);
        dest.writeInt(hotHotel);
        dest.writeInt(hotelUserSn);
        dest.writeString(impressMemo);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeInt(newHotel);
        dest.writeString(phone);
        dest.writeInt(sn);
        dest.writeInt(hotelStatus);
        dest.writeInt(totalFavorite);
        dest.writeDouble(averageMark);
        dest.writeInt(roomAvailable);
        dest.writeByte((byte) (isCategory ? 1 : 0));
        dest.writeString(categoryName);
        dest.writeTypedList(roomTypeFormList);
        dest.writeInt(lowestPrice);
        dest.writeInt(lowestPriceOvernight);
        dest.writeParcelable(flashSaleRoomTypeForm, flags);
        dest.writeInt(countExifImage);
        dest.writeInt(activeStamp);
        dest.writeInt(numToRedeem);
        dest.writeInt(firstHours);
        dest.writeString(imageKey);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HotelForm> CREATOR = new Creator<HotelForm>() {
        @Override
        public HotelForm createFromParcel(Parcel in) {
            return new HotelForm(in);
        }

        @Override
        public HotelForm[] newArray(int size) {
            return new HotelForm[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDistrictSn() {
        return districtSn;
    }

    public void setDistrictSn(int districtSn) {
        this.districtSn = districtSn;
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

    public int getHomeImageSn() {
        return homeImageSn;
    }

    public void setHomeImageSn(int homeImageSn) {
        this.homeImageSn = homeImageSn;
    }

    public String getHomeImageName() {
        return homeImageName;
    }

    public void setHomeImageName(String homeImageName) {
        this.homeImageName = homeImageName;
    }

    public int getHotHotel() {
        return hotHotel;
    }

    public void setHotHotel(int hotHotel) {
        this.hotHotel = hotHotel;
    }

    public int getHotelUserSn() {
        return hotelUserSn;
    }

    public void setHotelUserSn(int hotelUserSn) {
        this.hotelUserSn = hotelUserSn;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
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

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getHotelStatus() {
        return hotelStatus;
    }

    public void setHotelStatus(int hotelStatus) {
        this.hotelStatus = hotelStatus;
    }

    public int getTotalFavorite() {
        return totalFavorite;
    }

    public void setTotalFavorite(int totalFavorite) {
        this.totalFavorite = totalFavorite;
    }

    public double getAverageMark() {
        return averageMark;
    }

    public void setAverageMark(double averageMark) {
        this.averageMark = averageMark;
    }

    public int getRoomAvailable() {
        return roomAvailable;
    }

    public void setRoomAvailable(int roomAvailable) {
        this.roomAvailable = roomAvailable;
    }

    public boolean isCategory() {
        return isCategory;
    }

    public void setCategory(boolean category) {
        isCategory = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<RoomTypeForm> getRoomTypeFormList() {
        return roomTypeFormList;
    }

    public void setRoomTypeFormList(List<RoomTypeForm> roomTypeFormList) {
        this.roomTypeFormList = roomTypeFormList;
    }

    public int getLowestPrice() {
        return lowestPrice;
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

    public void setLowestPrice(int lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public int getLowestPriceOvernight() {
        return lowestPriceOvernight;
    }

    public int getFirstHours() {
        return firstHours;
    }

    public HotelForm setFirstHours(int firstHours) {
        this.firstHours = firstHours;
        return this;
    }

    public void setLowestPriceOvernight(int lowestPriceOvernight) {
        this.lowestPriceOvernight = lowestPriceOvernight;
    }

    public String getImageKey() {
        return imageKey;
    }

    public HotelForm setImageKey(String imageKey) {
        this.imageKey = imageKey;
        return this;
    }

    public RoomTypeForm getFlashSaleRoomTypeForm() {
        return flashSaleRoomTypeForm;
    }

    public void setFlashSaleRoomTypeForm(RoomTypeForm flashSaleRoomTypeForm) {
        this.flashSaleRoomTypeForm = flashSaleRoomTypeForm;
    }

    public int getCountExifImage() {
        return countExifImage;
    }

    public void setCountExifImage(int countExifImage) {
        this.countExifImage = countExifImage;
    }

    public double getDistance(Context context) {
        Location location = new Location("gps");
        Location newCurrLocation = new Location("gps");
        location.setLatitude(this.getLatitude());
        location.setLongitude(this.getLongitude());
        Location currentLocation = Utils.getLocationFromPref(context);
        if (currentLocation == null) {
            if (PreferenceUtils.getLatLocation(HotelApplication.getContext()).equals("")) {
                newCurrLocation.setLongitude(Double.parseDouble(HotelApplication.getContext().getString(R.string.longitude_default)));
                newCurrLocation.setLatitude(Double.parseDouble(HotelApplication.getContext().getString(R.string.latitude_default)));
            } else {
                newCurrLocation.setLongitude(Double.parseDouble(PreferenceUtils.getLongLocation(HotelApplication.getContext())));
                newCurrLocation.setLatitude(Double.parseDouble(PreferenceUtils.getLatLocation(HotelApplication.getContext())));
            }

        } else {
            newCurrLocation.setLongitude(currentLocation.getLongitude());
            newCurrLocation.setLatitude(currentLocation.getLatitude());
        }
        return location.distanceTo(newCurrLocation);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isFlashSale() {
        boolean result = false;
        if (flashSaleRoomTypeForm != null) {
            if (flashSaleRoomTypeForm.getNumOfRoom() > 0 && flashSaleRoomTypeForm.getSn() > 0 && !flashSaleRoomTypeForm.getName().equals("")) {
                result = true;
            } else {
                result = false;
            }

        }
        return result;
    }

    public boolean checkFlashSale() {
        for (int i = 0; i < roomTypeFormList.size(); i++) {
            if (roomTypeFormList.get(i).getMode() == RoomType.FLASHSALE.getType() &&
                    roomTypeFormList.get(i).getBookCount() < roomTypeFormList.get(i).getNumOfRoom()) {
                return true;
            }
        }

        return false;
    }

}