package com.appromobile.hotel.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.BuildConfig;
import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.CallbackRetry;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.callback.CallbackApiFail;
import com.appromobile.hotel.dialog.RequestNetwork;
import com.appromobile.hotel.enums.SignupType;
import com.appromobile.hotel.gcm.FirebaseUtils;
import com.appromobile.hotel.gps.Constants;
import com.appromobile.hotel.gps.MyLocationApi;
import com.appromobile.hotel.gps.MyLocationService;
import com.appromobile.hotel.model.request.LoginDto;
import com.appromobile.hotel.model.request.MobileDeviceInput;
import com.appromobile.hotel.model.request.SocialLoginDto;
import com.appromobile.hotel.model.request.UserLocationForm;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.CheckInternetAsync;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.jaredrummler.android.device.DeviceName;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 6/24/2016.
 */
public class SplashActivity extends BaseActivity {

    private LocationManager locationManager;

    private boolean isNotification = false;
    private NotificationData notificationData;
    private int hotelDeeplinkSn = -1;
    private String actionDeeplink;


    private String appVersionName, serverVersionName;
    private int COUNT = 3;

    private String INTENT_ACTION = "";


    @Override
    protected void onStart() {
        super.onStart();
        MyLog.writeLog("onStart Splash");
        HotelApplication.checkOpenedApp = false;
        HotelApplication.newLocation = null;

        try {
            if (HotelApplication.googleApiClient != null) {
                HotelApplication.googleApiClient.connect();
            } else {
                HotelApplication.initializeGoogleSignIn();
                HotelApplication.googleApiClient.connect();
            }
        } catch (Exception e) {
            MyLog.writeLog("Splash onStart---------------------->" + e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (HotelApplication.googleApiClient != null) {
            HotelApplication.googleApiClient.disconnect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();

        //setContentView(R.layout.activity_main);

        /*
        / AppFlyer
        */
        //AppsFlyerLib.getInstance().startTracking(getApplication(), "6vPtz83RxhLeBBrc5moi5M");

        MyLog.writeLog("OnCreate Splash");

        HotelApplication.activity = this;

        if (HotelApplication.googleApiClient == null || HotelApplication.googleSignInOptions == null) {
            HotelApplication.initializeGoogleSignIn();
        }

        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("Splash Fabric--------------------------------->" + e);
        }

        try {
            float density = getResources().getDisplayMetrics().density;
            MyLog.writeLog("density: " + Float.toString(density));
        } catch (Exception e) {
            MyLog.writeLog("Splash Density--------------------------------->" + e);
        }

        /*
        * Get intent Deeplink
        */

        try {
            if (getIntent().getExtras() != null) {
                hotelDeeplinkSn = getIntent().getExtras().getInt("hotelDeeplinkSn", -1);
                actionDeeplink = getIntent().getAction();
                MyLog.writeLog("Splash---->Deeplink---->" + hotelDeeplinkSn);
            }
        } catch (Exception e) {
            MyLog.writeLog("Splash---->Deeplink---->" + e);
        }

        isNotification = getIntent().getBooleanExtra("NOTIFICATON_SEND", false);
        if (isNotification) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                notificationData = bundle.getParcelable("NotificationData");
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.cancel(getIntent().getIntExtra("NOTI_ID", -1));
                    if (intent.getAction() != null) {
                        INTENT_ACTION = intent.getAction();
                    }
                }
            }

        }
        if (isNotification) {
            setContentView(R.layout.splash_notify_activity);
            ImageView imgLoading = findViewById(R.id.imgLoading);
            Glide
                    .with(SplashActivity.this)
                    .load(R.raw.anim_loading)
                    .into(imgLoading);

            /*
            * Update Notification to Server
            */
            if (notificationData != null && notificationData.getAppNotificationSn() >0 ){
                ControllerApi.getmInstance().updateViewNotification(notificationData.getAppNotificationSn());
            }

        } else {

            /*
            * Check Deeplink for Layout
            */
            if (hotelDeeplinkSn == -1) { //None Deeplink
                setContentView(R.layout.splash_activity);
            } else {
                setContentView(R.layout.splash_notify_activity);
                ImageView imgLoading = findViewById(R.id.imgLoading);
                Glide
                        .with(SplashActivity.this)
                        .load(R.raw.anim_loading)
                        .into(imgLoading);
            }
        }

        MyLog.writeLog("DEVICE_ID: " + HotelApplication.DEVICE_ID);

    }

    private Location getLocationFromService() {
        MyLocationService myLocationService = new MyLocationService(this, LocationManager.NETWORK_PROVIDER);
        return myLocationService.getMyLocationFromNetworkProvider();
    }

    private void updateLocationToServer() {
        MyLog.writeLog("Update User Location ---> Start");

        //Get Location Google
        Location location = MyLocationApi.getMyLocation();

        //Get Location Network
        if (location == null) {
            location = getLocationFromService();
        }

        //Get Location
        if (location == null) {
            double latitude = 10.8004808;
            double longitude = 106.6512822;
            location = new Location("dummyprovider");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            MyLog.writeLog("Location ---> DEFAULT");
        }

        //Store Location
        HotelApplication.newLocation = location;

        String[] address = MyLocationApi.getMyAddress(this, location);

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();


        MyLog.writeLog("My Location: --> " + "latitude: " + latitude + " / " + "longitude: " + longitude);

        String district = address[1];
        String city = address[2];
        String country = address[3];

        //STORE MY LOCATION DEFAULT
        PreferenceUtils.setLatLocation(getApplicationContext(), Double.toString(latitude));
        PreferenceUtils.setLongLocation(getApplicationContext(), Double.toString(longitude));

        //STORE MY ADDRESS
        String addr = address[0];
        PreferenceUtils.setCurrentAddress(getApplicationContext(), addr);
        MyLog.writeLog("My Address: --> " + addr);


        final UserLocationForm userLocationForm = new UserLocationForm();
        userLocationForm.setLatitude(latitude);
        userLocationForm.setLongitude(longitude);
        String setMobileUserId = HotelApplication.DEVICE_ID;
        userLocationForm.setMobileUserId(setMobileUserId);
        userLocationForm.setCountryCode(country != null ? country : "");
        userLocationForm.setDistrictCode(district != null ? district : "Tan Binh");
        userLocationForm.setProvinceCode(city != null ? city : "Ho Chi Minh");
        userLocationForm.setOffset(0);
        userLocationForm.setLimit(HotelApplication.LIMIT_REQUEST);
        userLocationForm.setAlwaysRefresh(false);
        userLocationForm.setAddress((PreferenceUtils.getCurrentAddress(getApplicationContext())));

        //Send to Server
        HotelApplication.serviceApi.updateUserLocation(userLocationForm, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new retrofit2.Callback<List<HotelForm>>() {

            @Override
            public void onResponse(Call<List<HotelForm>> call, final Response<List<HotelForm>> response) {
                if (response.isSuccessful()) {
                    MyLog.writeLog("updateUserLocation Successful");

                    gotoMainScreen();


                } else {
                    MyLog.writeLog("updateUserLocation Failed");
                }
            }

            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
                MyLog.writeLog("updateUserLocationonFailure");
                //Show cannot connect to server

                showDialogCannotConnectToServer(new CallbackRetry() {
                    @Override
                    public void retry() {
                        updateLocationToServer();
                    }
                });
            }
        });
    }

    private void updateTokenToServer(final String token) {
        MyLog.writeLog("Start updateTokenToServer");
        MobileDeviceInput mobileDeviceInput = new MobileDeviceInput();
        String language = Locale.getDefault().getLanguage();
        try {
            if (language.equals("vi")) {
                mobileDeviceInput.setLanguage(3);
            } else if (language.equals(ParamConstants.KOREA)) {
                mobileDeviceInput.setLanguage(1);
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
        mobileDeviceInput.setTokenId(token);
        //}

        HotelApplication.serviceApi.updateAppUserToken(mobileDeviceInput, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                MyLog.writeLog("Start updateAppUserId --->OK<---");

                //Update UserLocation
                updateLocationToServer();

            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                MyLog.writeLog("updateTokenToServer fail");
                //Show cannot connect to server

                showDialogCannotConnectToServer(new CallbackRetry() {
                    @Override
                    public void retry() {
                        updateTokenToServer(token);
                    }
                });
            }
        });
    }

    private void initSetting() {

        HotelApplication.serviceApi.findApiSetting(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<ApiSettingForm>() {
            @Override
            public void onResponse(Call<ApiSettingForm> call, Response<ApiSettingForm> response) {

                MyLog.writeLog("findApiSetting Splash");

                ApiSettingForm apiSettingForm = response.body();
                if (apiSettingForm != null) {
                    // get the status of read service agreement or not
                    PreferenceUtils.setReadStatusPolicy(SplashActivity.this, apiSettingForm.isReadAgreementPolicy());
                    MyLog.writeLog("READ STATUS FROM SERVER" + apiSettingForm.isReadAgreementPolicy());
                    //
                    if (apiSettingForm.getMaxDisplayHotel() > 0) {
                        HotelApplication.apiSettingForm = response.body();
                        HotelApplication.LIMIT_REQUEST = apiSettingForm.getMaxDisplayHotel();
                    }

                    appVersionName = BuildConfig.VERSION_NAME;
                    serverVersionName = apiSettingForm.getLastAndroidAppVersion();

                    //
                    //Check Version
                    //

                    //Change to int
                    int[] appBuild = changeVersionToInt(appVersionName);
                    int[] svBuild = changeVersionToInt(serverVersionName);

                    //App0 > Server0
                    if (appBuild[0] > svBuild[0]) {
                        //Continue
                        continueVersion();
                    } else {
                        //App < Server
                        if (appBuild[0] < svBuild[0]) {
                            //Force
                            forceVersion();
                        }
                        //App0 = Server0
                        else {
                            //App1 > Server1
                            if (appBuild[1] >= svBuild[1]) {
                                //Check number 3
                                if (appBuild[2] >= svBuild[2]) {
                                    //Continue
                                    continueVersion();
                                } else {
                                    //Update
                                    updateVersion();
                                }
                            } else {
                                if (appBuild[1] < svBuild[1]) {
                                    //Force
                                    forceVersion();
                                }
                            }
                        }

                    }

                } else {

                    //Show Dialog

                    showDialogCannotConnectToServer(new CallbackRetry() {
                        @Override
                        public void retry() {
                            initSetting();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<ApiSettingForm> call, Throwable t) {

                //Show Dialog

                showDialogCannotConnectToServer(new CallbackRetry() {
                    @Override
                    public void retry() {
                        initSetting();
                    }
                });

            }
        });
    }

    private void continueVersion() {
        final int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 23) {
            setupCheckGPSEnable();
        } else {
            checkPermission();
        }
    }

    private void forceVersion() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogForceVersion();
            }
        }, 100);
    }

    private void updateVersion() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogUpdateVersion();
            }
        }, 100);
    }


    private int[] changeVersionToInt(String version) {
        int[] intVer = new int[3];
        if (version != null) {
            String[] temp = version.split("\\.");
            for (int i = 0; i < intVer.length; i++) {
                if (i == 2 & temp.length != 3) {
                    intVer[2] = 0;
                } else {
                    intVer[i] = Integer.parseInt(temp[i]);
                }
            }
        }
        return intVer;
    }

    //ForceVersion
    private void dialogForceVersion() {
        if (!SplashActivity.this.isFinishing()) {
            final Dialog dialog = new Dialog(SplashActivity.this, R.style.dialog_full_transparent_background);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                    MyLog.writeLog("Finish Splash force Version");
                }
            });
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.force_update_dialog);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
            TextViewSFBold btnYes = dialog.findViewById(R.id.btnYes);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        MyLog.writeLog("forceVersion--------------------->" + anfe);
                    }
                    finish();
                    MyLog.writeLog("Finish Splash force Version");
                }
            });
        }
    }

    //Update Version
    private void dialogUpdateVersion() {
        if (!SplashActivity.this.isFinishing()) {
            final Dialog dialog = new Dialog(SplashActivity.this, R.style.dialog_full_transparent_background);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.delete_confirm_dialog);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                window.setAttributes(wlp);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
            TextViewSFBold btnYes = dialog.findViewById(R.id.btnYes);
            TextViewSFBold btnNo = dialog.findViewById(R.id.btnNo);
            TextViewSFRegular tvMessage = dialog.findViewById(R.id.tvMessage);
            tvMessage.setText(getString(R.string.msg_0_upgrade_app));
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
//                                DialogUtils.showMessageDialog(ReservationActivity.this, getString(R.string.make_reservation_success));
                    final int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion < 23) {
                        setupCheckGPSEnable();
                    } else {
                        checkPermission();
                    }
                }
            });

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        MyLog.writeLog("updateVersion--------------------->" + anfe);
                    }
                    dialog.dismiss();
                    finish();
                    MyLog.writeLog("Finish Splash force Version");
                }
            });
        }
    }

    //Dialog Cannot to server
    private void showDialogCannotConnectToServer(final CallbackRetry callbackRetry) {
        DialogUtils.apiFail(this, getString(R.string.cannot_connect_to_server), null, getString(R.string.retry), new CallbackApiFail() {
            @Override
            public void onPress(boolean retry) {
                if (retry) {
                    callbackRetry.retry();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.writeLog("onResume Splash");
        HotelApplication.checkOpenedApp = false;
        HotelApplication.newLocation = null;
        checkingNetwork();
        //Intent i = new Intent(this, BrowserActivity.class);
        //startActivity(i);
        //finish();
    }

    //Check Network
    private void checkingNetwork() {
        new CheckInternetAsync(SplashActivity.this, new OnTaskCompleted() {
            @Override
            public void onCheckingInternetComplete(int result) {

                switch (result) {
                    //successful
                    case Constants.PING_SUCCESSFULLY:
                        //Continue
                        initSetting();
                        break;
                    //
                    case Constants.NO_INTERNET:
                        try {
                            DialogUtils.showNetworkError(SplashActivity.this, new DialogCallback() {
                                @Override
                                public void finished() {
                                    checkingNetwork();
                                }
                            });
                        } catch (Exception e) {
                            MyLog.writeLog("No Internet Exception------------------->" + e);
                        }
                        break;
                    case Constants.NO_WIFI:
                        //DialogUtils.showNetworkConfirm(SplashActivity.this);
                        try {
                            RequestNetwork.show(SplashActivity.this);
                        } catch (Exception e) {
                            MyLog.writeLog("");
                        }
                        break;
                }
            }

        }).execute();

    }

    //Check GPS
    private void setupCheckGPSEnable() {
        MyLog.writeLog("setupCheckGPSEnable");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            showGpsConfirm();

        } else {

            checkAutoLogin();

        }

    }

    //Show Dialog Open GPS
    private void showGpsConfirm() {
        try {
            if (!this.isFinishing()) {
                final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.confirm_dialog);
                Window window = dialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.CENTER;
                    window.setAttributes(wlp);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.show();
                }
                TextView tvMessage = dialog.findViewById(R.id.tvMessage);
                tvMessage.setText(getString(R.string.app_need_to_query_location_please_turn_on_the_gps));
                TextView btnCancel = dialog.findViewById(R.id.btnCancel);
                TextView btnOK = dialog.findViewById(R.id.btnOK);

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);

                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        } catch (Exception e) {
            MyLog.writeLog("showGpsConfirm----------------------->" + e);
        }

    }

    //get Info User
    public void getAppUserForm() {
        HotelApplication.serviceApi.findAppUser(PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<AppUserForm>() {
            @Override
            public void onResponse(Call<AppUserForm> call, Response<AppUserForm> response) {
                DialogUtils.hideLoadingProgress();
                //Store Info User
                AppUserForm appUserForm = response.body();
                if (appUserForm != null) {
                    PreferenceUtils.setAppUser(SplashActivity.this, appUserForm);

                    //Update App User Id
                    getTokenFcm();

                } else {

                    //Update App User Id
                    getTokenFcm();

                }
            }

            @Override
            public void onFailure(Call<AppUserForm> call, Throwable t) {
                //Show Dialog Cannot to Server
                showDialogCannotConnectToServer(new CallbackRetry() {
                    @Override
                    public void retry() {
                        getAppUserForm();
                    }
                });
            }
        });
    }

    private void checkAutoLogin() {
        PreferenceUtils.setToken(this, "");
        if (PreferenceUtils.isAutoLogin(this)) {
            MyLog.writeLog("CHECK-AUTO-LOGIN----!!!!!----> True");
            /*
            / Auto Login
            */
            if (PreferenceUtils.getLoginType(SplashActivity.this) == SignupType.Manual) {
                /*
                / Manual
                */
                if (!PreferenceUtils.getUserId(this).equals("") && !PreferenceUtils.getPassword(this).equals("")) {
                    LoginDto loginDto = new LoginDto();
                    loginDto.setCache(false);
                    loginDto.setMobileUserId(HotelApplication.DEVICE_ID);
                    loginDto.setUserId(PreferenceUtils.getUserId(this));
                    loginDto.setPassword(PreferenceUtils.getPassword(this));
                    HotelApplication.serviceApi.login(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                        @Override
                        public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                            if (response.isSuccessful()) {
                                RestResult restResult = response.body();
                                if (restResult != null && restResult.getResult() == 1) {
                                    PreferenceUtils.setToken(SplashActivity.this, restResult.getOtherInfo());
                                    MyLog.writeLog("SESSION:------>" + restResult.getOtherInfo());
                                    PreferenceUtils.setLoginType(SplashActivity.this, SignupType.Manual);
                                }
                            }
                            getAppUserForm();
                        }

                        @Override
                        public void onFailure(Call<RestResult> call, Throwable t) {

                            //Update App User Id
                            getTokenFcm();

                        }
                    });
                } else {

                    //Update App User Id
                    getTokenFcm();

                }
            } else if (PreferenceUtils.getLoginType(SplashActivity.this) == SignupType.Facebook) {
                /*
                / Login Facebook
                */
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null && !accessToken.isExpired()) {

                    //Login Via Facebook
                    loginFacebook(accessToken.getToken());


                } else {

                    //Update App User Id
                    getTokenFcm();

                }

            } else {

                /*
                / Login Google
                */
                loginGooglePlus();
            }
        }
        // No login
        else {
            MyLog.writeLog("CHECK-AUTO-LOGIN----!!!!!----> False");

            //Update App User Id
            getTokenFcm();

        }

    }

    private void loginFacebook(final String tokenFacebook) {

        SocialLoginDto loginDto = new SocialLoginDto();
        loginDto.setMobileUserId(HotelApplication.DEVICE_ID);
        loginDto.setSocialToken(tokenFacebook);
        loginDto.setViaApp(SignupType.Facebook.getType());

        HotelApplication.serviceApi.loginViaSocialApp(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                if (response.isSuccessful()) {
                    RestResult restResult = response.body();
                    if (restResult != null) {
                        if (restResult.getResult() == 1) {
                            PreferenceUtils.setToken(SplashActivity.this, restResult.getOtherInfo());
                            MyLog.writeLog("SESSION:------>" + restResult.getOtherInfo());
                            MyLog.writeLog("LOGIN_VIA_FACEBOOK :" + "--> Successfull <--");
                        }

                        if (restResult.getResult() == 12) {

                            if (COUNT != 0) {
                                //ReLogin
                                loginFacebook(tokenFacebook);
                                COUNT--;
                                MyLog.writeLog("LOGIN_VIA_FACEBOOK :" + "-->" + COUNT + "<--");
                            } else {
                                //Delete Data
                                deleteLoginDataOnPref();
                                //Login Fail
                                //gotoMainScreen();
                                getTokenFcm();
                            }

                        }
                    }

                }
                getAppUserForm();
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {

                //Update App User Id
                getTokenFcm();

            }
        });

    }

    private void loginGooglePlus() {
        SocialLoginDto loginDto = new SocialLoginDto();
        loginDto.setMobileUserId(HotelApplication.DEVICE_ID);
        // token google
        loginDto.setSocialToken(PreferenceUtils.getTokenGInfo(SplashActivity.this));
        loginDto.setViaApp(SignupType.GooglePlus.getType());

        MyLog.writeLog("MODE-GOOGLE + :" + "----////----> Start <-////-" + PreferenceUtils.getTokenGInfo(SplashActivity.this));

        try {
            HotelApplication.serviceApi.loginViaSocialApp(loginDto, HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                @Override
                public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                    if (response.isSuccessful()) {
                        RestResult restResult = response.body();

                        if (restResult != null) {
                            MyLog.writeLog("GET_RESULT:------> " + restResult.getResult());
                            if (restResult.getResult() == 1) {
                                // setSocialToken = session
                                PreferenceUtils.setToken(SplashActivity.this, restResult.getOtherInfo());
                                MyLog.writeLog("GET_SESSION:------>" + restResult.getOtherInfo());
                            }
                            if (restResult.getResult() == 12) {
                                if (COUNT != 0) {
                                    //ReLogin
                                    silentSignInGooglePlus();
                                    COUNT--;
                                    MyLog.writeLog("LOGIN_VIA_GOOGLE PLUS :" + "-->" + COUNT + "<--");
                                } else {

                                    //Delete Data
                                    deleteLoginDataOnPref();

                                    //Login Fail
                                    //gotoMainScreen();
                                    getTokenFcm();

                                }

                            }
                        }
                    }
                    getAppUserForm();
                }

                @Override
                public void onFailure(Call<RestResult> call, Throwable t) {

                    //Update App User Id
                    getTokenFcm();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            //Update App User Id
            getTokenFcm();

            MyLog.writeLog("loginGooglePlus---------------------------------->" + e);
        }

    }

    private void getTokenFcm() {
        String token = FirebaseUtils.getRegistrationId(this);
        MyLog.writeLog("TOKEN FCM:" + token);
        if (!token.equals("")) {
            updateTokenToServer(token);
        } else {
            //Check again
            updateTokenToServer(token);
        }
    }

    private void gotoMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isNotification) {
            intent.putExtra("NOTIFICATON_SEND", true);
            intent.putExtra("NotificationData", notificationData);
            intent.setAction(INTENT_ACTION);
        }
        /*
        * Check Deeplink for Intent
        */
        if (hotelDeeplinkSn != -1) {
            intent.putExtra("hotelDeeplinkSn", hotelDeeplinkSn);
            intent.setAction(actionDeeplink);
        }
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            setupCheckGPSEnable();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Check Location
                    setupCheckGPSEnable();
                } else {
                    // Permission Denied
                    finish();
                    MyLog.writeLog("Permission Denied");
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        MyLog.writeLog("onBackPressed");
        finish();
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSplash";
    }


    private void silentSignInGooglePlus() {
        MyLog.writeLog("SILENT_SIGNIN_GOOGLE---->Start");
        OptionalPendingResult<GoogleSignInResult> option = Auth.GoogleSignInApi.silentSignIn(HotelApplication.googleApiClient);
        if (option.isDone()) {
            GoogleSignInResult result = option.get();
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) {
                    String newToken = account.getIdToken();
                    MyLog.writeLog("NEW_TOKEN_SILIENT-------()()()()------>" + newToken);
                    PreferenceUtils.setTokenGInfo(SplashActivity.this, newToken);

                /*
                / Login Again
                */

                    loginGooglePlus();
                }
            }
        } else {
            option.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    if (googleSignInResult.isSuccess()) {
                        GoogleSignInAccount account = googleSignInResult.getSignInAccount();
                        if (account != null) {
                            String newToken = account.getIdToken();
                            MyLog.writeLog("NEW_TOKEN_SILENT-------()()()()------>" + newToken);
                            PreferenceUtils.setTokenGInfo(SplashActivity.this, newToken);

                        /*
                        / Login Again
                        */

                            loginGooglePlus();
                        }
                    }
                }
            });
        }

    }

    private void deleteLoginDataOnPref() {
        PreferenceUtils.setToken(SplashActivity.this, "");
        PreferenceUtils.setPassword(SplashActivity.this, "");
        PreferenceUtils.setUserId(SplashActivity.this, "");
        PreferenceUtils.setPasscode(SplashActivity.this, "");
        PreferenceUtils.setIsPasscode(SplashActivity.this, false);
        PreferenceUtils.setAutoLogin(SplashActivity.this, false);
    }

    public interface OnTaskCompleted {
        void onCheckingInternetComplete(int result);
    }
}
