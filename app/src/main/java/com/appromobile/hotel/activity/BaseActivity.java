package com.appromobile.hotel.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.Dialag;
import com.appromobile.hotel.dialog.RequestNetwork;
import com.appromobile.hotel.model.request.UserCommonInfoDto;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.igaworks.IgawCommon;
import com.igaworks.adbrix.IgawAdbrix;
import com.igaworks.interfaces.DeferredLinkListener;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by xuan on 12/26/2016.
 */

public abstract class BaseActivity extends FragmentActivity {
    protected String screenName;
    private BroadcastReceiver broadcastReceiver;

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String INTENT_ACTION = intent.getAction();
                    if (INTENT_ACTION != null) {
                        switch (INTENT_ACTION) {
                            case ParamConstants.BROADCAST_POPUP_ACTION_COUPON:
                                Bundle bundle = intent.getExtras();
                                if (bundle != null) {
                                    String message = bundle.getString(ParamConstants.INTENT_KEY_BUNDLE_NOTI);
                                    if (message == null) {
                                        break;
                                    } else {
                                        prepareCouponPopup(message);
                                    }
                                }
                                break;
                            case ParamConstants.BROADCAST_POPUP_ACTION_POLICY:
                                preparePolicyPopup();
                                break;
                        }
                    }
                }
            }
        };
    }

    private void prepareCouponPopup(String message) {

        String btn1 = getString(R.string.ok);
        CallbackDialag callback = new CallbackDialag() {
            @Override
            public void button1() {

            }

            @Override
            public void button2() {

            }

            @Override
            public void button3(Dialog dialog) {

            }
        };
        Dialag.getInstance().show(BaseActivity.this, false, true, true, null, message, btn1, null, null, Dialag.BTN_LEFT, callback);
    }

    public void preparePolicyPopup() {
        if (!PreferenceUtils.getToken(this).equals("")) {
            String message = getString(R.string.msg_6_9_3_notification_message);
            String btn1 = getString(R.string.btn_6_9_3_cancel);
            String btn3 = getString(R.string.btn_6_9_3_agree);
            // prepare 2 spanable string for message
            SpannableString seeDetailString = new SpannableString(message);
            int start = message.indexOf('.') + 1;
            int stop = message.length();
            // sub string from See detail -> end.
            // set bold
            seeDetailString.setSpan(new StyleSpan(Typeface.BOLD), start, stop, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // set color
            seeDetailString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.org)), start, stop, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // set clickable
            ClickableSpan myClick = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    goToPolicyActivity();

                }
            };
            // set callback for dialog
            CallbackDialag callback = new CallbackDialag() {
                @Override
                public void button1() {
                    Dialag.getInstance().show(BaseActivity.this, false, false, false, null, getString(R.string.msg_6_9_3_cancel_message), getString(R.string.btn_6_9_3_back), null, getString(R.string.ok), Dialag.BTN_RIGHT, new CallbackDialag() {
                        @Override
                        public void button1() {
                            goToPolicyActivity();
                        }

                        @Override
                        public void button2() {

                        }

                        @Override
                        public void button3(Dialog dialog) {
                            dialog.dismiss();
                            goToMainActivtiyToClose();
                        }
                    });
                }

                @Override
                public void button2() {

                }

                @Override
                public void button3(final Dialog dialog) {
                    // update status TRUE to server
                    UserCommonInfoDto commonInfoDto = new UserCommonInfoDto();
                    commonInfoDto.setRead(true);
                    ControllerApi.getmInstance().updateReadStatusCommonInfo(BaseActivity.this, commonInfoDto, new ResultApi() {
                        @Override
                        public void resultApi(Object object) {
                            // không cần xử lý
                            PreferenceUtils.setReadStatusPolicy(BaseActivity.this, true);
                            dialog.dismiss();
                        }
                    });
                }
            };
            seeDetailString.setSpan(myClick, start, stop, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Dialag.getInstance().show(BaseActivity.this, false, false, false, null, seeDetailString, btn1, null, btn3, Dialag.BTN_RIGHT, callback);
        }
    }

    private void goToPolicyActivity() {
        Intent intent;
        if (BaseActivity.this.getClass() == TermPrivacyPolicyActivity.class) {
            // refresh TermPrivatePolicyActivtiy
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        } else {
            intent = new Intent(this, TermPrivacyPolicyActivity.class);
            startActivity(intent);
        }
    }

    private void goToMainActivtiyToClose() {
        if (this.getClass() != MainActivity.class) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(ParamConstants.INTENT_ACTION_CLOSE_APP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            this.finish();
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ParamConstants.BROADCAST_POPUP_ACTION_COUPON);
        filter.addAction(ParamConstants.BROADCAST_POPUP_ACTION_POLICY);
        if (broadcastReceiver == null) {
            initBroadcastReceiver();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }

    private void unregisterReceiver() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyLog.writeLog("BaseActivity-->onStart");
        if (HotelApplication.isRelease) {

            setEventAnalytic(getScreenName());

            setEventIgawAdbrix(getScreenName());

            /*
            / Facebook
            */
            //AppEventsLogger logger = AppEventsLogger.newLogger(this);
            //logger.logEvent(getScreenName());

            /*
            * AppFlyer
            */
            //AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), getScreenName(), null);
        }
    }

    public String getScreenName() {
        return screenName;
    }

    public abstract void setScreenName();

    public void setButtonName(String buttonName) {
        if (HotelApplication.isRelease && buttonName != null) {
            setEventAnalytic(buttonName);
            setEventIgawAdbrix(buttonName);
        }
    }

    /*
    * IgawAdbrix
    */
    private void setEventIgawAdbrix(String event) {
        if (event != null) {
            IgawAdbrix.retention(event);
            IgawAdbrix.firstTimeExperience(event);
            adbrix();
            MyLog.writeLog("BaseActivity IgawAdbrix---->onStart");
        }
    }

    /*
    / Firebase
    */
    private void setEventAnalytic(String event) {
        try {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            if (mFirebaseAnalytics != null && event != null) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event);
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                mFirebaseAnalytics.logEvent(event, bundle);
                MyLog.writeLog("BaseActivity Analytic: " + event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.writeLog("BaseActivity Analytic: " + e);
        }
    }

    /*
    / Facebook
    */
    public void setEventFacebook(String event, int sn, int totalFee) {
        try {
            Bundle params = new Bundle();
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "VND");
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, String.valueOf(sn));

            AppEventsLogger logger = AppEventsLogger.newLogger(this);

            if (event.equals(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT)) {
                logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, totalFee, params);
                MyLog.writeLog("BaseActivity Analytic: " + "EVENT_NAME_VIEWED_CONTENT");
            } else if (event.equals(AppEventsConstants.EVENT_NAME_ADDED_TO_CART)) {
                logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, totalFee, params);
                MyLog.writeLog("BaseActivity Analytic: " + "EVENT_NAME_ADDED_TO_CART");
            } else {
                Currency currency = Currency.getInstance("VND");
                logger.logPurchase(new BigDecimal(totalFee), currency, params);
                MyLog.writeLog("BaseActivity Analytic: " + "EVENT_NAME_PURCHASED");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.writeLog("BaseActivity Analytic: " + "EVENT_NAME_PURCHASED");
        }
    }


    @SuppressWarnings("deprecation")
    private void adbrix() {

        IgawCommon.startApplication(BaseActivity.this);

        IgawCommon.setDeferredLinkListener(this, new DeferredLinkListener() {
            @Override
            public void onReceiveDeeplink(String s) {
                try {
                    MyLog.writeLog("BaseActivity IGAWORKS Facebook Deeplink: " + s);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addCategory(Intent.CATEGORY_BROWSABLE);
                    i.setData(Uri.parse(s));
                    BaseActivity.this.startActivity(i);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    MyLog.writeLog("BaseActivity IGAWORKS Facebook Deeplink Exception: ------------->" + e);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        /*
        / Set ForceGround
        */
        HotelApplication.activityResumed();
        if (BaseActivity.this.getClass() != SplashActivity.class) {
            if (!Utils.isOpenWifi(BaseActivity.this)) {
                RequestNetwork.showDialogOneButton(BaseActivity.this);
            }
        }
        registerReceiver();
        super.onResume();
    }


    @Override
    protected void onPause() {
        /*
        / Set BackGround
        */
        HotelApplication.activityDestroy();
        unregisterReceiver();
        super.onPause();
    }

    public void startIntent (Class destination, boolean resetTop, String key, Parcelable object) {
        Intent intent = new Intent(this, destination);
        if (resetTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (key != null && object != null) {
            intent.putExtra(key, object);
        }
        startActivity(intent);
    }
}
