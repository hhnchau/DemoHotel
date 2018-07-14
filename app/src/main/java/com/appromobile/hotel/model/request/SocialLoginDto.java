package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 8/12/2016.
 */
public class SocialLoginDto {
    private boolean cache = true;
    private String mobileUserId;
    private String token;
    private int viaApp;

    public String getMobileUserId() {
        return mobileUserId;
    }

    public void setMobileUserId(String mobileUserId) {
        this.mobileUserId = mobileUserId;
    }

    public String getSocialToken() {
        return token;
    }

    public void setSocialToken(String token) {
        this.token = token;
    }

    public int getViaApp() {
        return viaApp;
    }

    public void setViaApp(int viaApp) {
        this.viaApp = viaApp;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }
}
