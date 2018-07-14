package com.appromobile.hotel.model.view;

/**
 * Created by thanh on 1/13/2017.
 */

public class PopupForm {
    public final static int ACTION_PROMOTION = 1;
    public final static int ACTION_INVITE = 2;
    public final static int ACTION_EVENT = 3;
    public final static int ACTION_HOTEL = 4;
    public final static int ACTION_NOTICE = 5;
    public final static int ACTION_LINK = 6;
    public final static int ACTION_DISTRICT = 7;

    private int action;
    private boolean applied;
    private boolean canApply;
    private String applyEnd;
    private String applyStart;
    private String content;
    private int discount;
    private int discountType;
    private String linkedName;
    private boolean newItem;
    private int sn;
    private String targetContent;
    private int targetType;
    private String targetName;
    private int targetSn;
    private String title;
    private String targetInfo;
    private int maxView;
    private String imageKey;

    public PopupForm() {
    }

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public String getTargetContent() {
        return targetContent;
    }

    public void setTargetContent(String targetContent) {
        this.targetContent = targetContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getLinkedName() {
        return linkedName;
    }

    public void setLinkedName(String linkedName) {
        this.linkedName = linkedName;
    }

    public boolean isNewItem() {
        return newItem;
    }

    public void setNewItem(boolean newItem) {
        this.newItem = newItem;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getTargetSn() {
        return targetSn;
    }

    public void setTargetSn(int targetSn) {
        this.targetSn = targetSn;
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

    public boolean isCanApply() {
        return canApply;
    }

    public void setCanApply(boolean canApply) {
        this.canApply = canApply;
    }

    public int getMaxView() {
        return maxView;
    }

    public void setMaxView(int maxView) {
        this.maxView = maxView;
    }

    public String getImageKey() {
        return imageKey;
    }

    public PopupForm setImageKey(String imageKey) {
        this.imageKey = imageKey;
        return this;
    }
}
