package com.appromobile.hotel.gcm.action;

import android.text.TextUtils;

/**
 * Created by appro on 26/09/2017.
 */
public class ActionNotify {
    private String appName;
    private String mainTitle;
    private String subTitle;
    private String urlIcon;

    public ActionNotify(String appName, String mainTitle, String subTitle, String urlIcon) {
        this.appName = appName;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.urlIcon = urlIcon;
    }

    public String getAppName() {
        return appName;
    }

    public String getMainTitle() {
        if (TextUtils.isEmpty(mainTitle)) {
            mainTitle = "";
        }
        return mainTitle;
    }

    public String getSubTitle() {
        if (TextUtils.isEmpty(subTitle)) {
            subTitle = "";
        }
        return subTitle;
    }

    public String getUrlIcon() {
        return urlIcon;
    }
}
