package com.appromobile.hotel.gps;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.appromobile.hotel.utils.MyLog;

/**
 * Created by appro on 24/07/2017.
 */
public class MyLocationService extends Service implements LocationListener {
    protected LocationManager locationManager;
    private Location myLocation;

    private static final long MIN_DISTANCE_FOR_UPDATE = 200;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 5;

    public MyLocationService(Activity context, String provider) {
        if (context != null) {
            try {
                MyLog.writeLog("getMyLocationFromNetworkProvider --->Start");
                locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                if (locationManager != null && locationManager.isProviderEnabled(provider)) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(provider, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                    if (locationManager != null) {
                        myLocation = locationManager.getLastKnownLocation(provider);

                        stopSelf();

                    }
                } else {
                    //Location Disable
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public Location getMyLocationFromNetworkProvider() {
        MyLog.writeLog("getMyLocationFromNetworkProvider --->");
        return myLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        MyLog.writeLog("getMyLocationFromNetworkProvider --->Change");
        myLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
