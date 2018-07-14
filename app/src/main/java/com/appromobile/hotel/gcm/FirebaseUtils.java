package com.appromobile.hotel.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.appromobile.hotel.utils.MyLog;

/**
 * Created by xuan on 11/7/2016.
 */

public class FirebaseUtils {
    private static final String PROPERTY_REG_ID = "registration_id";

    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences("HOTEL_APP_FCM", Context.MODE_PRIVATE);
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            MyLog.writeLog("Registration not found.");
            return "";
        }
        return registrationId;
    }

    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.apply();
    }
}
