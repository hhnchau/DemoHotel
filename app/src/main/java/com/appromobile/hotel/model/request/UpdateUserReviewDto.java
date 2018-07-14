package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 8/10/2016.
 */
public class UpdateUserReviewDto {
    private String comment;
//    private int hotelSn;
    private int mark;
//    private Long roomSn;
//    private Long roomTypeSn;
    private int sn;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

//    public int getHotelSn() {
//        return hotelSn;
//    }
//
//    public void setHotelSn(int hotelSn) {
//        this.hotelSn = hotelSn;
//    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

//    public Long getRoomTypeSn() {
//        return roomTypeSn;
//    }
//
//    public void setRoomTypeSn(Long roomTypeSn) {
//        this.roomTypeSn = roomTypeSn;
//    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

//    public Long getRoomSn() {
//        return roomSn;
//    }
//
//    public void setRoomSn(Long roomSn) {
//        this.roomSn = roomSn;
//    }
}
