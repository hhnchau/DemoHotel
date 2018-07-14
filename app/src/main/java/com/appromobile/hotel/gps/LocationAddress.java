package com.appromobile.hotel.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.appromobile.hotel.utils.MyLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by xuan on 7/7/2016.
 */
public class LocationAddress {

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Address result = null;
                MyLog.writeLog("LocationAddress------------|||||||||--------------->getAddressFromLocation");
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        result = addressList.get(0);
                    }
                    else if (addressList == null || addressList.size() == 0){
                       getAddressFromLocation(latitude, longitude, context, handler);
                    }
                } catch (Exception e) {
                    MyLog.writeLog("LocationAddress--------------------------->"+e);
                }

                Message message = Message.obtain();
                message.setTarget(handler);
                message.what = 1;
                Bundle bundle = new Bundle();
                bundle.putParcelable("Address", result);
                message.setData(bundle);
                message.sendToTarget();

            }
        };
        thread.start();
    }
    public static Address getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context) {

                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Address result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        result = addressList.get(0);
                    }
                } catch (Exception e) {
                    MyLog.writeLog("LocationAddress--------------------------->"+e);
                }
               return result;
            }



    public static void queryAllLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                ArrayList<Address> arrayAddress = new ArrayList<>();
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 20);
                    for(int i=0;i<addressList.size();i++){
                        arrayAddress.add(addressList.get(i));
                    }
                } catch (Exception e) {
                    MyLog.writeLog("LocationAddress--------------------------->"+e);
                }
                Message message = Message.obtain();
                message.setTarget(handler);
                message.what = 1;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("AddressList", arrayAddress);
                message.setData(bundle);
                message.sendToTarget();

            }
        };
        thread.start();
    }

    public void queryAllLocation(final String address, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                ArrayList<Address> arrayAddress = new ArrayList<>();
                try {
                    List<Address> addressList = geocoder.getFromLocationName(
                            address, 20);

                    for(int i=0;i<addressList.size();i++){
                        arrayAddress.add(addressList.get(i));
                    }
                } catch (Exception e) {
                    MyLog.writeLog("LocationAddress--------------------------->"+e);
                }
                Message message = Message.obtain();
                message.setTarget(handler);
                message.what = 1;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("AddressList", arrayAddress);
                message.setData(bundle);
                message.sendToTarget();
            }
        };
        thread.start();
    }
}
