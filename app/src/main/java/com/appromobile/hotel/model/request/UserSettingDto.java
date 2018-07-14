package com.appromobile.hotel.model.request;

/**
 * Created by appro on 19/04/2018.
 */

public class UserSettingDto {
    private int language;
    private boolean notiAll;
    private boolean notiBefore1;
    private boolean notiBefore2;
    private boolean notiOff;

    public UserSettingDto() {
    }

    public UserSettingDto(int language, boolean notiAll, boolean notiBefore1, boolean notiBefore2, boolean notiOff) {
        this.language = language;
        this.notiAll = notiAll;
        this.notiBefore1 = notiBefore1;
        this.notiBefore2 = notiBefore2;
        this.notiOff = notiOff;
    }

    public int getLanguage() {
        return language;
    }

    public UserSettingDto setLanguage(int language) {
        this.language = language;
        return this;
    }

    public boolean isNotiAll() {
        return notiAll;
    }

    public UserSettingDto setNotiAll(boolean notiAll) {
        this.notiAll = notiAll;
        return this;
    }

    public boolean isNotiBefore1() {
        return notiBefore1;
    }

    public UserSettingDto setNotiBefore1(boolean notiBefore1) {
        this.notiBefore1 = notiBefore1;
        return this;
    }

    public boolean isNotiBefore2() {
        return notiBefore2;
    }

    public UserSettingDto setNotiBefore2(boolean notiBefore2) {
        this.notiBefore2 = notiBefore2;
        return this;
    }

    public boolean isNotiOff() {
        return notiOff;
    }

    public UserSettingDto setNotiOff(boolean notiOff) {
        this.notiOff = notiOff;
        return this;
    }
}
