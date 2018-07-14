package com.appromobile.hotel.model.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuan on 7/19/2016.
 */
public class NoticeForm implements Parcelable {
    private String content;
    private Long createStaffSn;
    private String createTime;
    private String lastUpdate;
    private Long sn;
    private String title;
    private int top;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateStaffSn() {
        return createStaffSn;
    }

    public void setCreateStaffSn(Long createStaffSn) {
        this.createStaffSn = createStaffSn;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getSn() {
        return sn;
    }

    public void setSn(Long sn) {
        this.sn = sn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeValue(this.createStaffSn);
        dest.writeString(this.createTime);
        dest.writeString(this.lastUpdate);
        dest.writeValue(this.sn);
        dest.writeString(this.title);
        dest.writeInt(this.top);
    }

    public NoticeForm() {
    }

    protected NoticeForm(Parcel in) {
        this.content = in.readString();
        this.createStaffSn = (Long) in.readValue(Long.class.getClassLoader());
        this.createTime = in.readString();
        this.lastUpdate = in.readString();
        this.sn = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.top = in.readInt();
    }

    public static final Parcelable.Creator<NoticeForm> CREATOR = new Parcelable.Creator<NoticeForm>() {
        @Override
        public NoticeForm createFromParcel(Parcel source) {
            return new NoticeForm(source);
        }

        @Override
        public NoticeForm[] newArray(int size) {
            return new NoticeForm[size];
        }
    };
}
