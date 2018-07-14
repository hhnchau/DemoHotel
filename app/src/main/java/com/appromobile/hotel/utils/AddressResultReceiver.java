package com.appromobile.hotel.utils;

import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.appromobile.hotel.callback.ResultCallback;
import com.appromobile.hotel.gps.Constants;

/**
 * Created by thanh on 3/9/2017.
 */

public class AddressResultReceiver extends ResultReceiver {
    private ResultCallback callback;

    public AddressResultReceiver(Handler handler, ResultCallback callback) {
        super(handler);
        this.callback = callback;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        String province = "";
        try {
            if (resultCode == Constants.SUCCESS_RESULT) {
               MyLog.writeLog( "Map receive result successs");
                Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                if (address != null && address.getAdminArea() != null) {
                    province = address.getAdminArea();
                }
            }
            MyLog.writeLog( "message: " + resultData.getString(Constants.RESULT_DATA_KEY));
            callback.onFinishedResult(province, resultData.getString(Constants.RESULT_DATA_KEY));
        } catch (Exception e) {
            MyLog.writeLog( "error----------------------> " + e.getCause().getMessage());
        }
    }
}
