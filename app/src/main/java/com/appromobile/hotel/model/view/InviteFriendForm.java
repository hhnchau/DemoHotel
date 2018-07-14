package com.appromobile.hotel.model.view;

/**
 * Created by xuan on 12/20/2016.
 */

public class InviteFriendForm{
    private String content;
    private int discount;
    private int memberId;
    private String mobile;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getPromotionSn() {
        return promotionSn;
    }

    public void setPromotionSn(int promotionSn) {
        this.promotionSn = promotionSn;
    }

    private String nickName;
    private int promotionSn;
}
