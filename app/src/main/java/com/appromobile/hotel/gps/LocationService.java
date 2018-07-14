package com.appromobile.hotel.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.appromobile.hotel.BuildConfig;
import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.gcm.FirebaseUtils;
import com.appromobile.hotel.model.request.MobileDeviceInput;
import com.appromobile.hotel.model.request.UserLocationForm;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.android.device.DeviceName;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 60 * 3; // 3min
    private static final long FASTEST_INTERVAL = 1000 * 60 * 2;

    private float LIMIT_DISTANCE = 200;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private AddressResultReceiver mResultReceiver;
    private double latitude;
    private double longitude;
    private Address address;
    private Location oldLocation;
    private int countGeoCode;
    private Handler check;
    private Runnable retryLocation;


    @Override
    public void onCreate() {
        super.onCreate();

        if (HotelApplication.apiSettingForm != null) {
            LIMIT_DISTANCE = (float) (HotelApplication.apiSettingForm.getUpdateLocationDistance() * 1000);
        } else {
            ControllerApi.getmInstance().findApiSetting(this, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    ApiSettingForm apiSettingForm = (ApiSettingForm) object;
                    LIMIT_DISTANCE = (float) apiSettingForm.getUpdateLocationDistance() * 1000;
                }
            });
        }

        check = new Handler();
        retryLocation = new Runnable() {
            @Override
            public void run() {
                MyLog.writeLog("---------//////////--------->sendBroadcastToStart Update Location Fail");
                sendBroadcastToStart(Constants.LOCATION_UPDATE);
                check.removeCallbacks(retryLocation);
                MyLog.writeLog("Remove Callback Location -----/////---->timeout");

            }
        };

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.writeLog("onStartCommand");
        createLocationRequest();
        initializeLocationAPI();
        mResultReceiver = new AddressResultReceiver(new Handler());
        updateTokenToServer();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        check.removeCallbacks(retryLocation);
        mGoogleApiClient.disconnect();
        stopLocationUpdates();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MyLog.writeLog("startLocationUpdates");
        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MyLog.writeLog("checkSelfPermission");
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            MyLog.writeLog("requestLocationUpdates --------////-------> Start");
            check.postDelayed(retryLocation, 4000);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } else {
            MyLog.writeLog("connectGoogleAPI");
            connectGoogleAPI();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        countGeoCode = 0;
        //Clear counter timer
        check.removeCallbacks(retryLocation);
        MyLog.writeLog("Remove Callback Location -----/////---->getLocation");

        if (HotelApplication.newLocation == null) {
            MyLog.writeLog("HotelApplication.newLocation == null");
            HotelApplication.newLocation = location;

//            latitude = 10.8092579;
//            longitude = 106.7266634;

            latitude = HotelApplication.newLocation.getLatitude();
            longitude = HotelApplication.newLocation.getLongitude();

            MyLog.writeLog("latitude: -->" + latitude + " longitude: -->" + longitude);

            storeLocationToPref(latitude, longitude);
            startGeoCodeIntentService(latitude, longitude);
        } else {
            oldLocation = Utils.getLocationFromPref(getApplicationContext());
            HotelApplication.newLocation = location;
            float distance = getDistanceBetween2Location(oldLocation, HotelApplication.newLocation);
            MyLog.writeLog("HotelApplication.newLocation != null ----> Distance:---> " + distance);
            if (distance >= LIMIT_DISTANCE) {


//                latitude = 10.8092579;
//                longitude = 106.7266634;

                latitude = HotelApplication.newLocation.getLatitude();
                longitude = HotelApplication.newLocation.getLongitude();

                MyLog.writeLog("latitude: -->" + latitude + " longitude: -->" + longitude);

                storeLocationToPref(latitude, longitude);
                startGeoCodeIntentService(latitude, longitude);
            }
        }
    }

    private void sendBroadcastToStart(String broadcastType) {
        Intent broadcast;
        if (broadcastType.equals(Constants.LOCATION_UPDATE)) {
            broadcast = new Intent(Constants.LOCATION_UPDATE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
            //sendBroadcast(broadcast);
            MyLog.writeLog("Start ---------//////////--------->sendBroadcast Location Fail");
        } else if (broadcastType.equals(Constants.FIRST_TIME_RUN)) {
            broadcast = new Intent(Constants.FIRST_TIME_RUN);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
            MyLog.writeLog("Start ---------//////////--------->sendBroadcast gotoMainScreen");
        }
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        //mLocationRequest.setSmallestDisplacement(LIMIT_DISTANCE - 10);
        mLocationRequest.setSmallestDisplacement(10);
    }

    private void initializeLocationAPI() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    protected void startGeoCodeIntentService(double latitude, double longitude) {
        Intent intent = new Intent(this, GeoCodeService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LATITUDE_DATA_EXTRA, latitude);
        intent.putExtra(Constants.LONGITUDE_DATA_EXTRA, longitude);
        startService(intent);
    }

    private void initializeDataAgain() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        Location temp = Utils.getLocationFromPref(getApplicationContext());
        if (temp != null) {
            latitude = temp.getLatitude();
            longitude = temp.getLongitude();
        }
    }

    private void connectGoogleAPI() {
        if (servicesAvailable()) {
            mGoogleApiClient.connect();
        }
    }


    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private boolean servicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getApplicationContext());
        if (result != ConnectionResult.SUCCESS) {
            MyLog.writeLog("Google Service not available");
            if (googleAPI.isUserResolvableError(result)) {
                MyLog.writeLog("isUserResolvableError");
                if (HotelApplication.activity != null) {
                    MyLog.writeLog("activity != null");
                    googleAPI.getErrorDialog(HotelApplication.activity, result,
                            0).show();
                } else {
                    return false;
                }
            }
            return false;
        }
        MyLog.writeLog("Google Service available");
        return true;
    }

    //Store My Location
    private void storeLocationToPref(double latitude, double longitude) {
        PreferenceUtils.setLatLocation(getApplicationContext(), Double.toString(latitude));
        PreferenceUtils.setLongLocation(getApplicationContext(), Double.toString(longitude));
    }

    private float getDistanceBetween2Location(Location oldLocation, Location newLocation) {
        return newLocation.distanceTo(oldLocation);
    }

    private void updateTokenToServer() {
        MyLog.writeLog("Start updateTokenToServer");
        MobileDeviceInput mobileDeviceInput = new MobileDeviceInput();
        String language = Locale.getDefault().getLanguage();
        try {
            if (language.equals("vi")) {
                mobileDeviceInput.setLanguage(3);
            } else {
                mobileDeviceInput.setLanguage(2);
            }
        } catch (Exception e) {
            MyLog.writeLog("updateTokenToServer---------------------->" + e);
        }

        mobileDeviceInput.setAppVersion(BuildConfig.VERSION_NAME);
        mobileDeviceInput.setPhoneModel(DeviceName.getDeviceName());
        mobileDeviceInput.setOsVersion(Build.VERSION.RELEASE);
        mobileDeviceInput.setMobileUserId(HotelApplication.DEVICE_ID);
        mobileDeviceInput.setOs(2);

        //Disable GCM Receiver Message // Check Login or Logout
        //if (!PreferenceUtils.getToken(this).equals("")) {
        mobileDeviceInput.setTokenId(FirebaseUtils.getRegistrationId(this));
        //}

        HotelApplication.serviceApi.updateAppUserToken(mobileDeviceInput, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                if (response.isSuccessful()) {
                    MyLog.writeLog("updateAppUserToken");
                    if (FirebaseUtils.getRegistrationId(LocationService.this).equals("")) {
                        MyLog.writeLog("Firebase getRegistrationId");
                        GCMBackgroundTask gcmBackgroundTask = new GCMBackgroundTask();
                        gcmBackgroundTask.execute();
                    } else {
                        MyLog.writeLog("mGoogleApiClient.srtartConnect()");
                        connectGoogleAPI();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {

            }
        });
    }

    private void updateUserLocationToServer(final Address addressLocation) {

        String country = addressLocation.getCountryName();
        MyLog.writeLog("Address-------------------------------------------"+country);
        String province = addressLocation.getAdminArea();
        MyLog.writeLog("Address-------------------------------------------"+province);
        String district = addressLocation.getSubAdminArea();
        MyLog.writeLog("Address-------------------------------------------"+district);
        final UserLocationForm userLocationForm = new UserLocationForm();

        userLocationForm.setLatitude(latitude);
        userLocationForm.setLongitude(longitude);
        String setMobileUserId = HotelApplication.DEVICE_ID;
        userLocationForm.setMobileUserId(setMobileUserId);
        userLocationForm.setCountryCode(country != null ? country : "");
        userLocationForm.setDistrictCode(district != null ? district : "Tan Binh");
        userLocationForm.setProvinceCode(province != null ? province : "Ho Chi Minh");
        userLocationForm.setOffset(0);
        userLocationForm.setLimit(HotelApplication.LIMIT_REQUEST);
        userLocationForm.setAlwaysRefresh(false);
        userLocationForm.setAddress((PreferenceUtils.getCurrentAddress(getApplicationContext())));
        MyLog.writeLog("---------------->UPDATE-LOCATION-TO-SERVER:----------->Start:-------------->" + "long: " + userLocationForm.getLongitude() + "lat: " + userLocationForm.getLatitude() + "Address: " + userLocationForm.getAddress() + " - Country: " + userLocationForm.getCountryCode() + " - Provice: " + userLocationForm.getProvinceCode() + " - District: " + userLocationForm.getDistrictCode());
        HotelApplication.serviceApi.updateUserLocation(userLocationForm, PreferenceUtils.getToken(LocationService.this), HotelApplication.DEVICE_ID).enqueue(new retrofit2.Callback<List<HotelForm>>() {

            @Override
            public void onResponse(Call<List<HotelForm>> call, final Response<List<HotelForm>> response) {
                MyLog.writeLog("------------------>UPDATE-LOCATION-TO-SERVER:----------> : Status:---> " + response.code());
                if (response.isSuccessful()) {
                    //Check Start Broadcast
                    if (!HotelApplication.checkOpenedApp) {
                        HotelApplication.checkOpenedApp = true;
                        sendBroadcastToStart(Constants.FIRST_TIME_RUN);
                    } else {
//                        sendBroadcastToStart(Constants.UPDATE_LOCATION_DATA);
                        MyLog.writeLog("---------------->UPDATE-LOCATION-TO-SERVER:------------>: AppOpen: " + response.body().size());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
                MyLog.writeLog("---------------->UPDATE-LOCATION-TO-SERVER:------------> fail");
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnectionSuspended(int i) {
        MyLog.writeLog("onConnectionSuspended");
        mGoogleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyLog.writeLog("onConnectionFailed");
        mGoogleApiClient.reconnect();
    }


    @SuppressLint("ParcelCreator")
    public class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {
                MyLog.writeLog("Receive result successfully");
                String addressString = resultData.getString(Constants.RESULT_DATA_KEY);
                MyLog.writeLog("onReceiveResult success");
                PreferenceUtils.setCurrentAddress(LocationService.this, addressString);
                address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                //Set last Address
                PreferenceUtils.setLastAddress(LocationService.this, address);
                updateUserLocationToServer(address);

            } else if (resultCode == Constants.FAILURE_RESULT) {
                MyLog.writeLog("Receive result fail");
                String failure = resultData.getString(Constants.RESULT_DATA_KEY);
                // invalid lat-lng params (very rarely happen)
                if (failure.equals(getString(R.string.invalid_lat_long_used))) {
                    sendBroadcastToStart(Constants.LOCATION_UPDATE);
                } else if (failure.equals(getString(R.string.service_not_available))) {
                    // can not connect Internet
                    countGeoCode++;
                    if (countGeoCode <= 6) {
                        startGeoCodeIntentService(resultData.getDouble(Constants.LATITUDE_DATA_EXTRA, 0), resultData.getDouble(Constants.LONGITUDE_DATA_EXTRA, 0));
                    } else {
                        sendBroadcastToStart(Constants.LOCATION_UPDATE);
                    }
                } else {
                    // can not find address with lat-lng
                    sendBroadcastToStart(Constants.LOCATION_UPDATE);
                }
            }
        }
    }

    private class GCMBackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                MyLog.writeLog("Start getting token");
                String token = FirebaseInstanceId.getInstance().getToken();
                if (token != null) {
                    FirebaseUtils.storeRegistrationId(LocationService.this, token);
                    MyLog.writeLog("FirebaseUtils.storeRegistrationId");
                }
            } catch (Exception e) {
                MyLog.writeLog("GCMBackgroundTask-------------------------->" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MyLog.writeLog("onPostExecute");
            connectGoogleAPI();
        }
    }

}
