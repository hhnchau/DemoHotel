package com.appromobile.hotel.callback;

import android.app.Dialog;

/**
 * Created by thanh on 8/2/2017.
 */

public interface CallBackListenerPopupCenter {
    void onSeeDetail(String targetInfo, int targetSn, int action);
    void onGetCoupon(int targetSn, Dialog dialog);
}
