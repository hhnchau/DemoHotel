package com.appromobile.hotel.model.view;

/**
 * Created by xuan on 7/15/2016.
 */
public class RestResult {
    private int count;
    private String message;
    private String otherInfo;
    private int result;
    private int sn;
    private int[] snList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int[] getSnList() {
        return snList;
    }

    public void setSnList(int[] snList) {
        this.snList = snList;
    }

}
