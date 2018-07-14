package com.appromobile.hotel.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by appro on 21/07/2017.
 */
public class MyLocationApi extends HotelApplication implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private static Location myLocation;
    private static String[] myAddress = new String[4];
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 5;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(MIN_TIME_FOR_UPDATE);
        mLocationRequest.setFastestInterval(MIN_TIME_FOR_UPDATE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        //mLocationRequest.setSmallestDisplacement(0.1F);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.reconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        MyLog.writeLog("onLocationChanged --->" + location.toString());
        myLocation = location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.reconnect();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLocationAPI();
    }

    private void initializeLocationAPI() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {

            createLocationRequest();
            mGoogleApiClient.connect();

        }
    }

    private void createLocationRequest() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            mGoogleApiClient.connect();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public static Location getMyLocation() {
        MyLog.writeLog("onLocationChanged Return --->");
        return myLocation;
    }

    public static String[] getMyAddress(Context context, Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        for (int k = 0; k < 5; k++) {
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    //Save Last Address
                    PreferenceUtils.setLastAddress(context, address);

                    String temp = "";
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        temp += address.getAddressLine(i) + " ";

                    }
                    //Address
                    myAddress[0] = temp;

                    // District
                    temp = "Tan Binh";
                    if (address.getSubAdminArea() != null) {
                        temp = address.getSubAdminArea();
                    }
                    myAddress[1] = temp;

                    // City
                    temp = "Ho Chi Minh";
                    if (address.getAdminArea() != null) {
                        temp = address.getAdminArea();
                    }
                    myAddress[2] = temp;

                    // Country
                    temp = "";
                    if (address.getCountryName() != null) {
                        temp = address.getCountryName();
                    }
                    myAddress[3] = temp;
                }

            } catch (IOException e) {
                MyLog.writeLog("Geocoder error: --->" + e);
            }

            if (myAddress[0] != null && myAddress[1] != null && myAddress[2] != null && myAddress[3] != null) {
                MyLog.writeLog("Geocode Loop: --->" + k);
                break;
            }

        }

        return myAddress;

    }
}
