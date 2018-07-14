package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by xuan on 7/18/2016.
 */
public class RoomTypeForm implements Parcelable {
    private int additionalHours;
    private int firstHours;
    private int homeImageSn;
    private int hotelSn;
    private String memo;
    private String name;
    private int priceAdditionalHours;
    private int priceFirstHours;
    private int priceOvernight;
    private int priceOneDay;
    private List<HotelImageForm> roomTypeImageList;
    private int sn;
    private int availableRoom;
    private int bookCount;
    private String lastUpdate;
    private int numOfRoom;
    private String priceAdditionalHoursStr;
    private String priceFirstHoursStr;
    private String priceOneDayStr;
    private String priceOvernightStr;
    private List roomFormList;
    private String roomType;
    private String shortName;
    private int status;
    private int countExifImage;
    private boolean locked;
    private int square;
    private int bedType;
    private List<RoomView> roomViewList;

    public RoomTypeForm() {
    }


    protected RoomTypeForm(Parcel in) {
        additionalHours = in.readInt();
        firstHours = in.readInt();
        homeImageSn = in.readInt();
        hotelSn = in.readInt();
        memo = in.readString();
        name = in.readString();
        priceAdditionalHours = in.readInt();
        priceFirstHours = in.readInt();
        priceOvernight = in.readInt();
        priceOneDay = in.readInt();
        roomTypeImageList = in.createTypedArrayList(HotelImageForm.CREATOR);
        sn = in.readInt();
        availableRoom = in.readInt();
        bookCount = in.readInt();
        lastUpdate = in.readString();
        numOfRoom = in.readInt();
        priceAdditionalHoursStr = in.readString();
        priceFirstHoursStr = in.readString();
        priceOneDayStr = in.readString();
        priceOvernightStr = in.readString();
        roomType = in.readString();
        shortName = in.readString();
        status = in.readInt();
        countExifImage = in.readInt();
        locked = in.readByte() != 0;
        square = in.readInt();
        bedType = in.readInt();
        roomViewList = in.createTypedArrayList(RoomView.CREATOR);
    }

    public static final Creator<RoomTypeForm> CREATOR = new Creator<RoomTypeForm>() {
        @Override
        public RoomTypeForm createFromParcel(Parcel in) {
            return new RoomTypeForm(in);
        }

        @Override
        public RoomTypeForm[] newArray(int size) {
            return new RoomTypeForm[size];
        }
    };

    public int getAdditionalHours() {
        return additionalHours;
    }

    public void setAdditionalHours(int additionalHours) {
        this.additionalHours = additionalHours;
    }

    public int getFirstHours() {
        return firstHours;
    }

    public void setFirstHours(int firstHours) {
        this.firstHours = firstHours;
    }

    public int getHomeImageSn() {
        return homeImageSn;
    }

    public void setHomeImageSn(int homeImageSn) {
        this.homeImageSn = homeImageSn;
    }

    public int getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(int hotelSn) {
        this.hotelSn = hotelSn;
    }

    public String getMemo() {
        if (TextUtils.isEmpty(memo)){
            return " N/A";
        }
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getPriceOvernight() {
        return priceOvernight;
    }

    public void setPriceOvernight(int priceOvernight) {
        this.priceOvernight = priceOvernight;
    }

    public int getPriceOneDay() {
        return priceOneDay;
    }

    public void setPriceOneDay(int priceOneDay) {
        this.priceOneDay = priceOneDay;
    }

    public List<HotelImageForm> getRoomTypeImageList() {
        return roomTypeImageList;
    }

    public void setRoomTypeImageList(List<HotelImageForm> roomTypeImageList) {
        this.roomTypeImageList = roomTypeImageList;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getAvailableRoom() {
        return availableRoom;
    }

    public void setAvailableRoom(int availableRoom) {
        this.availableRoom = availableRoom;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getNumOfRoom() {
        return numOfRoom;
    }

    public void setNumOfRoom(int numOfRoom) {
        this.numOfRoom = numOfRoom;
    }

    public String getPriceAdditionalHoursStr() {
        return priceAdditionalHoursStr;
    }

    public void setPriceAdditionalHoursStr(String priceAdditionalHoursStr) {
        this.priceAdditionalHoursStr = priceAdditionalHoursStr;
    }

    public String getPriceFirstHoursStr() {
        return priceFirstHoursStr;
    }

    public void setPriceFirstHoursStr(String priceFirstHoursStr) {
        this.priceFirstHoursStr = priceFirstHoursStr;
    }

    public String getPriceOneDayStr() {
        return priceOneDayStr;
    }

    public void setPriceOneDayStr(String priceOneDayStr) {
        this.priceOneDayStr = priceOneDayStr;
    }

    public String getPriceOvernightStr() {
        return priceOvernightStr;
    }

    public void setPriceOvernightStr(String priceOvernightStr) {
        this.priceOvernightStr = priceOvernightStr;
    }

    public List getRoomFormList() {
        return roomFormList;
    }

    public void setRoomFormList(List roomFormList) {
        this.roomFormList = roomFormList;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCountExifImage() {
        return countExifImage;
    }

    public void setCountExifImage(int countExifImage) {
        this.countExifImage = countExifImage;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getSquare() {
        return square;
    }

    public void setSquare(int square) {
        this.square = square;
    }

    public int getBedType() {
        return bedType;
    }

    public void setBedType(int bedType) {
        this.bedType = bedType;
    }

    public List<RoomView> getRoomViewList() {
        return roomViewList;
    }

    public void setRoomViewList(List<RoomView> roomViewList) {
        this.roomViewList = roomViewList;
    }

    //Flash Sale
    public boolean isFlashSale() {
        boolean result = false;
        if (roomType != null && roomType.equals("FlashSale")) {
            result = true;
        }
        return result;
    }

    //Room Flash Sale Available
    public boolean isFlashSaleRoomAvailable() {
        boolean result = false;
        if (numOfRoom > 0 && roomType != null && roomType.equals("FlashSale")) {
            result = true;
        }
        return result;
    }

    //Flash Sale Soldout
    public boolean isFlashSaleSoldout() {
        boolean result = false;
        if (numOfRoom <= 0 && roomType != null && roomType.equals("FlashSale")) {
            result = true;
        }
        return result;
    }

    public String getRoomBed(int i){
        switch (i){
            case 0:
                return "N/A";
            case 1:
                return "Single";
            case 2:
                return "Double";
            case 3:
                return "Twin";
            case 4:
                return "Triple";
            case 5:
                return "2 Double";
        }
        return "N/A";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(additionalHours);
        parcel.writeInt(firstHours);
        parcel.writeInt(homeImageSn);
        parcel.writeInt(hotelSn);
        parcel.writeString(memo);
        parcel.writeString(name);
        parcel.writeInt(priceAdditionalHours);
        parcel.writeInt(priceFirstHours);
        parcel.writeInt(priceOvernight);
        parcel.writeInt(priceOneDay);
        parcel.writeTypedList(roomTypeImageList);
        parcel.writeInt(sn);
        parcel.writeInt(availableRoom);
        parcel.writeInt(bookCount);
        parcel.writeString(lastUpdate);
        parcel.writeInt(numOfRoom);
        parcel.writeString(priceAdditionalHoursStr);
        parcel.writeString(priceFirstHoursStr);
        parcel.writeString(priceOneDayStr);
        parcel.writeString(priceOvernightStr);
        parcel.writeString(roomType);
        parcel.writeString(shortName);
        parcel.writeInt(status);
        parcel.writeInt(countExifImage);
        parcel.writeByte((byte) (locked ? 1 : 0));
        parcel.writeInt(square);
        parcel.writeInt(bedType);
        parcel.writeTypedList(roomViewList);
    }
}
