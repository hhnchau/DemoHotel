package com.appromobile.hotel.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.util.Log;

import com.appromobile.hotel.enums.SignupType;
import com.appromobile.hotel.model.view.AppUserForm;
import com.google.gson.Gson;

/**
 * Created by xuanquach on 8/11/15.
 */
public class PreferenceUtils {
    private static String HOTEL_PREFERENCE = "HOTEL_PREFERENCE";

    public static String getLatLocation(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString("LAT_LOCATION", "");
    }

    public static String getLongLocation(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString("LONG_LOCATION", "");
    }

    public static void setLatLocation(Context context, String strLocation) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LAT_LOCATION", strLocation);
        editor.apply();
    }

    public static void setLongLocation(Context context, String strLocation) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LONG_LOCATION", strLocation);
        editor.apply();
    }

    public static String getCurrentAddress(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString("CURRENT_ADDRESS", "");
    }

    public static void setCurrentAddress(Context context, String address) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("CURRENT_ADDRESS", address);
        editor.apply();
    }

    public static void setLastAddress(Context context, Address address) {
        Gson gson = new Gson();
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (address != null) {
            editor.putString("LAST_ADDRESS", gson.toJson(address));
        } else {
            editor.putString("LAST_ADDRESS", "");
        }
        editor.apply();

    }

    public static Address getLastAddress(Context context) {
        try {
            Gson gson = new Gson();
            final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
            String strLocation = prefs.getString("LAST_ADDRESS", "");
            return gson.fromJson(strLocation, Address.class);
        } catch (Exception e) {
            MyLog.writeLog("getLastAddress-------------------------->" + e);
        }
        return null;
    }

    public static void setToken(Context context, String token) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String getToken(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString("token", "");
    }

    public static void setTokenGInfo(Context context, String token) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("G_INFO", token);
        editor.apply();
    }

    public static String getTokenGInfo(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString("G_INFO", "");
    }

    public static void setIsPasscode(Context context, boolean isChecked) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("IsPasscode", isChecked);
        editor.apply();
    }

    public static boolean getIsPasscode(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getBoolean("IsPasscode", false);
    }

    public static void setPasscode(Context context, String passcode) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("passcode", passcode);
        editor.apply();
    }

    public static String getPasscode(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString("passcode", "");
    }

    public static void setAutoLogin(Context context, boolean isChecked) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isAutoLogin", isChecked);
        editor.apply();
    }

    public static boolean isAutoLogin(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getBoolean("isAutoLogin", true);
    }

    public static void setUserId(Context context, String userId) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("au_u", userId);
        editor.apply();
    }

    public static String getUserId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString("au_u", "");
    }

    public static void setPassword(Context context, String password) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("au_en", password);
        editor.apply();
    }

    public static String getPassword(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString("au_en", "");
    }

    public static void setLoginType(Context context, SignupType signupType) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("SignupType", signupType.getType());
        editor.apply();
    }

    public static SignupType getLoginType(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return SignupType.toType(prefs.getInt("SignupType", SignupType.Manual.getType()));
    }


    public static void setAppUser(Context context, AppUserForm appUserForm) {
        Gson gson = new Gson();
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("AppUserForm", gson.toJson(appUserForm));
        editor.apply();
    }

    public static AppUserForm getAppUser(Context context) {
        try {
            Gson gson = new Gson();
            final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
            String strLocation = prefs.getString("AppUserForm", "");
            return gson.fromJson(strLocation, AppUserForm.class);
        } catch (Exception e) {
            MyLog.writeLog("AppUserForm-------------------------->" + e);
        }
        return null;
    }

    public static void setInvitTimePopup(Context context, long time) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("InvitTimePopup", time);
        editor.apply();
    }

    public static long getInvitTimePopup(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getLong("InvitTimePopup", 0);
    }

    public static void setCounterNotifi(Context context, int count) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("NOTIFI_COUNTER", count);
        editor.apply();
    }

    public static int getCounterNotifi(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getInt("NOTIFI_COUNTER", 0);
    }

    public static void setCounterBooking(Context context, int count) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("NOTIFI_COUNTER", count);
        editor.apply();
    }

    public static int getCounterBooking(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getInt("NOTIFI_COUNTER", 0);
    }

    public static void setReadStatusPolicy (Context context, boolean status) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("READ_POLICY_STATUS", status);
        editor.apply();
    }

    public static boolean getReadStatusPolicy(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getBoolean("READ_POLICY_STATUS", true);
    }

    public static void setGuide (Context context, boolean status) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("GUIDE", status);
        editor.apply();
    }

    public static boolean getGuide(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getBoolean("GUIDE", true);
    }
}
