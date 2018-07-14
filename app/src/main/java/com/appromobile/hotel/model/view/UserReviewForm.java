package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 8/10/2016.
 */
public class UserReviewForm implements Parcelable {
    private boolean author;
    private String comment;
    private String createTime;
    private String hotelSn;
    private int mark;
    private int roomSn;
    private String roomName;
    private String roomTypeName;
    private int roomTypeSn;
    private int sn;
    private String userNickName;

    public boolean isAuthor() {
        return author;
    }

    public void setAuthor(boolean author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHotelSn() {
        return hotelSn;
    }

    public void setHotelSn(String hotelSn) {
        this.hotelSn = hotelSn;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getRoomSn() {
        return roomSn;
    }

    public void setRoomSn(int roomSn) {
        this.roomSn = roomSn;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
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

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.author ? (byte) 1 : (byte) 0);
        dest.writeString(this.comment);
        dest.writeString(this.createTime);
        dest.writeString(this.hotelSn);
        dest.writeInt(this.mark);
        dest.writeInt(this.roomSn);
        dest.writeString(this.roomName);
        dest.writeString(this.roomTypeName);
        dest.writeInt(this.roomTypeSn);
        dest.writeInt(this.sn);
        dest.writeString(this.userNickName);
    }

    public UserReviewForm() {
    }

    protected UserReviewForm(Parcel in) {
        this.author = in.readByte() != 0;
        this.comment = in.readString();
        this.createTime = in.readString();
        this.hotelSn = in.readString();
        this.mark = in.readInt();
        this.roomSn = in.readInt();
        this.roomName = in.readString();
        this.roomTypeName = in.readString();
        this.roomTypeSn = in.readInt();
        this.sn = in.readInt();
        this.userNickName = in.readString();
    }

    public static final Creator<UserReviewForm> CREATOR = new Creator<UserReviewForm>() {
        @Override
        public UserReviewForm createFromParcel(Parcel source) {
            return new UserReviewForm(source);
        }

        @Override
        public UserReviewForm[] newArray(int size) {
            return new UserReviewForm[size];
        }
    };
}
