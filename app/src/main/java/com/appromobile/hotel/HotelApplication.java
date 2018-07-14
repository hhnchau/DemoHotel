package com.appromobile.hotel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.provider.Settings;
import android.support.multidex.MultiDex;

import com.appromobile.hotel.api.HotelRequestInterceptor;
import com.appromobile.hotel.api.ServiceApi;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.db.search.DatabaseConnection;

import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.UserAreaFavoriteForm;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.firebase.FirebaseApp;
import com.igaworks.IgawCommon;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;

/**
 * Created by xuan on 6/23/2016.
 */
public class HotelApplication extends Application {
    public Retrofit retrofit;
    public static ServiceApi serviceApi;
    private static Context context;
    public static GoogleSignInOptions googleSignInOptions;
    public static GoogleApiClient googleApiClient;
    public static Activity activity;
    public static ApiSettingForm apiSettingForm = null;
    public static Location newLocation;
    public static boolean checkOpenedApp;
    public static List<UserAreaFavoriteForm> userAreaFavoriteForms = null;
    public static String DEVICE_ID;
    public static String ID;
    public static int LIMIT_REQUEST = 30;
    public static final int TOTAL_USER_FAV = 3;
    public static boolean isEnglish;
    public static boolean isFlashSaleChange = false;
    private static boolean activityVisible;

    public static Map<String, PromotionInfoForm> mapPromotionInfoForm;

    public static final boolean isRelease = false; // Change

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        //Sql Lite
        DatabaseConnection.createDBInstance();
        FirebaseApp.initializeApp(this);
        initializeGoogleSignIn();

        //Set Language

        String language = PreferenceUtils.getLanguage(this);

        isEnglish = !language.equals(ParamConstants.VIETNAM);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //Set device Id
        ID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DEVICE_ID = Utils.md5(ID);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(new HotelRequestInterceptor(this));
        builder.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(UrlParams.MAIN_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            serviceApi = retrofit.create(ServiceApi.class);

            MyLog.writeLog("----> onCreate HotelApplication Go2Joy  <-----");
        } catch (Exception e) {
            if (retrofit == null) {
                MyLog.writeLog("retrofit: Cannot connect to server");

            } else {
                MyLog.writeLog("retrofit: Successfully connect to server");
            }
            e.printStackTrace();
            MyLog.writeLog("HotelApplication:------------------------>" + e);
        }

        //disable firebase crash report
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                /* Check whether it is development or release mode*/
                if (BuildConfig.REPORT_CRASH) {
//                    FirebaseCrash.report( e);
                    MyLog.writeLog(e.getMessage());
                }
            }
        });
        IgawCommon.autoSessionTracking(this);
    }

    public static Context getContext() {
        return context;
    }

    public static void initializeGoogleSignIn() {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestIdToken("170640652110-tc01mpf2ucq65o8dejksatq699ofnfc0.apps.googleusercontent.com")
                .requestEmail().requestProfile()
                .build();
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityDestroy() {
        activityVisible = false;
    }

}
