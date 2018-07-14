package com.appromobile.hotel.model.view;

/**
 * Created by xuan on 7/25/2016.
 */
public class Province {
    private String areaCode;
    private int countrySn;
    private long lastUpdate;
    private String name;
    private String nameCode;
    private int sn;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public int getCountrySn() {
        return countrySn;
    }

    public void setCountrySn(int countrySn) {
        this.countrySn = countrySn;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }
}
