package com.appromobile.hotel.model.view;

import java.util.List;

/**
 * Created by xuan on 7/19/2016.
 */
public class RoomTypeDetailForm {
    private int additionalHours;
    private int firstHours;
    private Long hotelSn;
    private String memo;
    private String name;
    private int priceAdditionalHours;
    private int priceFirstHours;
    private int priceOvernight;
    private List<HotelImageForm> roomTypeImageList ;
    private Long sn;

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

    public Long getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(Long hotelSn) {
        this.hotelSn = hotelSn;
    }

    public String getMemo() {
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

    public List<HotelImageForm> getRoomTypeImageList() {
        return roomTypeImageList;
    }

    public void setRoomTypeImageList(List<HotelImageForm> roomTypeImageList) {
        this.roomTypeImageList = roomTypeImageList;
    }

    public Long getSn() {
        return sn;
    }

    public void setSn(Long sn) {
        this.sn = sn;
    }
}
