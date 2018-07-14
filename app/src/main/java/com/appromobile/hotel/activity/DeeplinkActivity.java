package com.appromobile.hotel.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.appromobile.hotel.R;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuan on 11/24/2016.
 */

public class DeeplinkActivity extends BaseActivity {

    private final static String HOST_PROMOTION = "promotionSn";
    private final static String HOST_HOTEL = "hotelSn";
    private final static String HOST_DISTRICT = "districtSn";
    private final static String HOST_LOGIN = "loginapp";

    private final static String SCHEME_HTTP = "http";
    private final static String SCHEME_HTTPS = "https";
    private final static String SCHEME_APPRO = "approhotel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.splash_notify_activity);
        setScreenName();
        Uri data = getIntent().getData();
        if (data != null) {
            String scheme = data.getScheme();
            String host = data.getHost();
            if (scheme != null && host != null) {

                //Open App
                if (scheme.equals(SCHEME_HTTP) || scheme.equals(SCHEME_HTTPS)) {
                    if (checkUrl(data.toString(), HOST_PROMOTION)) {

                        int promotionSn = Integer.parseInt(data.getQueryParameter(HOST_PROMOTION));
                        gotoIntent(ParamConstants.INTENT_ACTION_PROMOTION, promotionSn);

                    } else if (checkUrl(data.toString(), HOST_DISTRICT)) {

                        int districtSn = Integer.parseInt(data.getQueryParameter(HOST_DISTRICT));
                        gotoIntent(ParamConstants.INTENT_ACTION_DISTRICT, districtSn);

                    } else if (checkUrl(data.toString(), HOST_HOTEL)) {

                        int hotelSn = Integer.parseInt(data.getQueryParameter(HOST_HOTEL));
                        gotoIntent(ParamConstants.INTENT_ACTION_HOTEL, hotelSn);

                    } else if (checkUrl(data.toString(), HOST_LOGIN)) {

                        gotoIntent(null, -2);

                    } else if (data.toString().endsWith("/openApp")) {

                        gotoIntent(null, -1);

                    }

                    //Open Browser
                } else if (scheme.equals(SCHEME_APPRO)) {
                    switch (host) {
                        case HOST_PROMOTION:

                            gotoIntent(ParamConstants.INTENT_ACTION_PROMOTION, getSn(data.toString()));

                            break;
                        case HOST_DISTRICT:

                            gotoIntent(ParamConstants.INTENT_ACTION_DISTRICT, getSn(data.toString()));

                            break;
                        case HOST_HOTEL:

                            gotoIntent(ParamConstants.INTENT_ACTION_HOTEL, getSn(data.toString()));

                            break;
                        case HOST_LOGIN:

                            gotoIntent(null, -2);

                            break;
                    }
                }
            }
        }
    }


    private void gotoIntent(String action, int sn) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (sn != -1) {
            intent.putExtra("hotelDeeplinkSn", sn);
            intent.setAction(action);
        }

        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private int getSn(String s) {
        int result = -1;
        try {
            String[] dataParse = s.split("\\/");
            if (dataParse.length > 1) {

                result = Integer.parseInt(dataParse[dataParse.length - 1]);

            }

        } catch (Exception e) {
            MyLog.writeLog("DeeplinkActivity------>Error" + e);
        }

        return result;
    }

    private boolean checkUrl(String url, String value) {
        Pattern pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE + Pattern.LITERAL);
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    @Override
    public void setScreenName() {
        this.screenName = "Deeplink";
    }
}
