package com.appromobile.hotel.model.view;

/**
 * Created by thanh on 1/11/2017.
 */

public class BannerForm {

    public final static int ACTION_PROMOTION = 1;
    public final static int ACTION_INVITE = 2;
    public final static int ACTION_EVENT = 3;
    public final static int ACTION_HOTEL = 4;
    public final static int ACTION_NOTICE = 5;
    public final static int ACTION_LINK = 6;
    public final static int ACTION_DISTRICT = 7;

    private int action;
    private String applyEnd;
    private String applyStart;
    private int discount;
    private int discountType;
    private String targetName;
    private int imageSn;
    private boolean newItem;
    private int targetSn;
    private int targetType;
    private int sn;
    private String title;
    private String targetInfo;
    private String imageKey;

    public BannerForm() {
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public int getTargetSn() {
        return targetSn;
    }

    public void setTargetSn(int targetSn) {
        this.targetSn = targetSn;
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
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

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setImageSn(int imageSn) {
        this.imageSn = imageSn;
    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }

    public int getImageSn() {
        return imageSn;
    }

    public boolean isNewItem() {
        return newItem;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(String targetInfo) {
        this.targetInfo = targetInfo;
    }

    public String getImageKey() {
        return imageKey;
    }

    public BannerForm setImageKey(String imageKey) {
        this.imageKey = imageKey;
        return this;
    }
}
