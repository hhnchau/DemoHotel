package com.appromobile.hotel.model.view;

/**
 * Created by xuan on 10/3/2016.
 */

public class ApiSettingForm {
    private String fileTypeSupport;
    private int maxDisplayHotel = 50;
    private int maxSizeUpload = 2097152;
    private String maxSizeUploadName;
    private double nearbyDistance = 1;
    private int minMoney;
    private double updateLocationDistance = 0.2;
    private String lastAndroidAppVersion;
    private int timeIntervalGetCode =60;
    private boolean readAgreementPolicy;

    public boolean isReadAgreementPolicy() {
        return readAgreementPolicy;
    }

    public void setReadAgreementPolicy(boolean readAgreementPolicy) {
        this.readAgreementPolicy = readAgreementPolicy;
    }

    public String getFileTypeSupport() {
        return fileTypeSupport;
    }

    public void setFileTypeSupport(String fileTypeSupport) {
        this.fileTypeSupport = fileTypeSupport;
    }

    public int getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(int minMoney) {
        this.minMoney = minMoney;
    }

    public int getMaxDisplayHotel() {
        return maxDisplayHotel;
    }

    public void setMaxDisplayHotel(int maxDisplayHotel) {
        this.maxDisplayHotel = maxDisplayHotel;
    }

    public int getMaxSizeUpload() {
        return maxSizeUpload;
    }

    public void setMaxSizeUpload(int maxSizeUpload) {
        this.maxSizeUpload = maxSizeUpload;
    }

    public String getMaxSizeUploadName() {
        return maxSizeUploadName;
    }

    public void setMaxSizeUploadName(String maxSizeUploadName) {
        this.maxSizeUploadName = maxSizeUploadName;
    }

    public double getNearbyDistance() {
        return nearbyDistance;
    }

    public void setNearbyDistance(double nearbyDistance) {
        this.nearbyDistance = nearbyDistance;
    }

    public double getUpdateLocationDistance() {
        return updateLocationDistance;
    }

    public void setUpdateLocationDistance(double updateLocationDistance) {
        this.updateLocationDistance = updateLocationDistance;
    }


    public String getLastAndroidAppVersion() {
        return lastAndroidAppVersion;
    }

    public void setLastAndroidAppVersion(String lastAndroidAppVersion) {
        this.lastAndroidAppVersion = lastAndroidAppVersion;
    }

    public int getTimeIntervalGetCode() {
        return timeIntervalGetCode;
    }

    public void setTimeIntervalGetCode(int timeIntervalGetCode) {
        this.timeIntervalGetCode = timeIntervalGetCode;
    }
}
