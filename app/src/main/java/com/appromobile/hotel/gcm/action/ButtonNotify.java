package com.appromobile.hotel.gcm.action;

import android.app.PendingIntent;

/**
 * Created by appro on 26/09/2017.
 */
public class ButtonNotify {
    private int drawableIcon;
    private String title;
    private PendingIntent pendingIntent;

    public ButtonNotify(int drawableIcon, String title, PendingIntent pendingIntent) {
        this.drawableIcon = drawableIcon;
        this.title = title;
        this.pendingIntent = pendingIntent;
    }

    public int getDrawableIcon() {
        return drawableIcon;
    }

    public String getTitle() {
        return title;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }
}
