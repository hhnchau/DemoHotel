package com.appromobile.hotel.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.api.controllerApi.CallbackPromotionInfoForm;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.callback.CallBackListenerPopupCenter;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.Dialag;
import com.appromobile.hotel.dialog.PromotionPopup;
import com.appromobile.hotel.enums.FragmentType;
import com.appromobile.hotel.enums.InviteFriendType;
import com.appromobile.hotel.enums.MapFilterType;
import com.appromobile.hotel.enums.NotificationType;
import com.appromobile.hotel.enums.SearchType;
import com.appromobile.hotel.enums.SortType;
import com.appromobile.hotel.fragment.BaseFragment;
import com.appromobile.hotel.gps.CheckLocation;
import com.appromobile.hotel.gps.MyLocationApi;
import com.appromobile.hotel.gps.MyLocationService;
import com.appromobile.hotel.model.request.CheckInRoomDto;
import com.appromobile.hotel.model.request.HomeHotelRequest;
import com.appromobile.hotel.model.request.UserLocationForm;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.District;
import com.appromobile.hotel.model.view.HotelForm;
import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.model.view.PopupApiForm;
import com.appromobile.hotel.model.view.PopupForm;
import com.appromobile.hotel.model.view.PromotionForm;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PictureUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.google.gson.Gson;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static int exit = 0;
    public android.support.v4.app.FragmentManager fragmentManager;
    private FragmentType currentFragmentType;
    private List<BaseFragment> fragments = new ArrayList<>();
    private List<FragmentType> fragmentTypes = new ArrayList<>();
    private HomeHotelRequest homeHotelRequest;
    private LinearLayout boxMapGuide;
    private TextView tvTitle;
    private ImageView[] imgTabs = new ImageView[4];
    private ImageButton btnSort, btnSearch, btnFilterMap;
    private MapFilterType mapFilterType = MapFilterType.All;

    private LinearLayout boxHotelPopup;
    private ImageView imgHotel, img360, imgIconPromotion1; //imgIconPromotion2, imgIconPromotion3, imgIconPromotion4;
    private TextView tvHotelNamePopup, tvAddressPopup, tvReview;
    private TextView tvPriceStatus, tvPriceHourlyNormal, tvPriceHourlyDiscount, tvPriceOvenightNormal, tvPriceOvernightDiscount;
    private LinearLayout boxHourly;

    private boolean
            isFilterAvailable = false,
            isFilterPromotion = false,
            isFilterNew = false,
            isFilterHot = false,
            isFilterFlashSale = false;

    boolean isNotification = false;
    NotificationData notificationData;
    private int hotelDeeplinkSn;
    private String actionDeeplink;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private NotificationBadge notificationBadge;

    private float LIMIT_DISTANCE = 200;

    private Location myLocation;
    private String[] myAddress;

    private String INTENT_ACTION = "";
    private int popupTargetSn;

    @Override
    protected void onStart() {
        super.onStart();
        PictureUtils.getInstance().clearCache(this);
    }

    @Override
    public void setScreenName() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent closeAppIntent = getIntent();
        if (closeAppIntent != null && closeAppIntent.getAction() != null && closeAppIntent.getAction().equals(ParamConstants.INTENT_ACTION_CLOSE_APP)) {
            finish();
        } else {
            try {
                Fabric.with(this, new Crashlytics());
            } catch (Exception e) {
                MyLog.writeLog("MainActivity Fabric---------------------------------->" + e);
            }

            fragmentManager = this.getSupportFragmentManager();
            setContentView(R.layout.activity_main);

            isNotification = getIntent().getBooleanExtra("NOTIFICATON_SEND", false);

            /*
            / Notification
            */
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

            /*
            * Get intent Deeplink
            */
            try {
                if (getIntent().getExtras() != null) {
                    hotelDeeplinkSn = getIntent().getExtras().getInt("hotelDeeplinkSn", -1);
                    actionDeeplink = getIntent().getAction();
                }
                MyLog.writeLog("Mainactivity---->Deeplink---->" + hotelDeeplinkSn);
            } catch (Exception e) {
                MyLog.writeLog("Mainactivity---->Deeplink---->" + e);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.org));
            }

            notificationBadge = findViewById(R.id.badge_notification);
            fragments.clear();
            fragmentTypes.clear();

            homeHotelRequest = new HomeHotelRequest();
            homeHotelRequest.setOffset(0);
            homeHotelRequest.setLimit(HotelApplication.LIMIT_REQUEST);
            homeHotelRequest.setMobileUserId(HotelApplication.DEVICE_ID);
            homeHotelRequest.setTypeSearch(SearchType.AREA.getType());

            imgTabs[0] = findViewById(R.id.imgTab1);
            imgTabs[1] = findViewById(R.id.imgTab2);
            imgTabs[2] = findViewById(R.id.imgTab3);
            imgTabs[3] = findViewById(R.id.imgTab4);

            boxMapGuide = findViewById(R.id.boxMapGuide);

            imgHotel = findViewById(R.id.imgHotel);
            boxHotelPopup = findViewById(R.id.boxHotelPopup);
            boxHotelPopup.setOnClickListener(this);
            tvHotelNamePopup = findViewById(R.id.tvNameVip);
            tvAddressPopup = findViewById(R.id.tvAddressPopup);
            tvReview = findViewById(R.id.txtReview);
            tvPriceStatus = findViewById(R.id.tvPriceStatus);
            tvPriceHourlyNormal = findViewById(R.id.tvPriceHourlyNormal);
            tvPriceHourlyDiscount = findViewById(R.id.tvPriceHourlyDiscount);
            tvPriceOvenightNormal = findViewById(R.id.tvPriceOvernightNormal);
            tvPriceOvernightDiscount = findViewById(R.id.tvPriceOvernightDiscount);

            boxHourly = findViewById(R.id.boxHourly);

            img360 = findViewById(R.id.item_hotel_icon_360);
            img360.setOnClickListener(this);

            imgIconPromotion1 = findViewById(R.id.imgIconPromotion1);


            findViewById(R.id.btnTab1).setOnClickListener(this);
            findViewById(R.id.btnTab2).setOnClickListener(this);
            findViewById(R.id.btnTab3).setOnClickListener(this);
            findViewById(R.id.btnTab4).setOnClickListener(this);

            imgTabs[0].setOnClickListener(this);
            imgTabs[1].setOnClickListener(this);
            imgTabs[2].setOnClickListener(this);
            imgTabs[3].setOnClickListener(this);

            btnSearch = findViewById(R.id.btnSearch);
            btnSort = findViewById(R.id.btnSort);
            btnFilterMap = findViewById(R.id.btnFilterMap);
            tvTitle = findViewById(R.id.tvTitle);
            findViewById(R.id.btnFloatingButton).setOnClickListener(this);
            findViewById(R.id.btnSort).setOnClickListener(this);
            findViewById(R.id.btnSearch).setOnClickListener(this);
            btnFilterMap.setOnClickListener(this);

            addFragment(FragmentType.MAP);
            setButtonName("SMap"); //Analytic
            addFragment(FragmentType.HOME);
            setButtonName("SHome"); //Analytic

            DialogUtils.showLoadingProgress(this, false);
            imgTabs[0].setImageResource(R.drawable.home_on);

            if (isNotification) {
                checkNotification();
            }

            if (hotelDeeplinkSn > 0) {
                //Check Action Deeplink
                if (actionDeeplink != null) {
                    switch (actionDeeplink) {
                        case ParamConstants.INTENT_ACTION_HOTEL:
                            //Goto Hotel Detail
                            gotoHotelDetailDeepLink(hotelDeeplinkSn);
                            break;
                        case ParamConstants.INTENT_ACTION_PROMOTION:
                            //Goto Promotion Detail
                            gotoPromotionDetail(hotelDeeplinkSn, PromotionForm.TYPE_PROMOTION);
                            break;
                        case ParamConstants.INTENT_ACTION_DISTRICT:
                            homeHotelRequest.setSort(SortType.DEEPLINK_DISTRICT.getType());
                            homeHotelRequest.setDistrictSn(Integer.toString(hotelDeeplinkSn));
                            //Find District Name
                            ControllerApi.getmInstance().findDistrictInformation(this, hotelDeeplinkSn, new ResultApi() {
                                @Override
                                public void resultApi(Object object) {
                                    District district = (District) object;
                                    if (district != null && district.getName() != null) {
                                        //Set Position Home

                                        BaseFragment baseFragment = findFragment(FragmentType.HOME);
                                        if (baseFragment != null && baseFragment.isAdded()) {
                                            baseFragment.setDistrictName(district.getName());
                                        } else {
                                            startIntent(SplashActivity.class, true, null, null);
                                        }

                                        baseFragment = findFragment(FragmentType.MAP);
                                        if (baseFragment != null && baseFragment.isAdded()) {
                                            baseFragment.chooseArea(hotelDeeplinkSn, district.getName());
                                        } else {
                                            startIntent(SplashActivity.class, true, null, null);
                                        }

                                    }
                                }
                            });

                            break;
                    }
                }

            /*
            * Check Deeplink forgot Password
            */
            } else if (hotelDeeplinkSn == -2) {
            /*
            * Check Auto Login
            */
                if (!PreferenceUtils.isAutoLogin(this)) { //Not Login --> goto Login
                    Intent login = new Intent(this, LoginActivity.class);
                    startActivity(login);
                }
                //else ---> Nothing
            }
            // Popup Info + Check when receive Notify ---> don't show popup
            if (!isNotification) {
                ControllerApi.getmInstance().findPopupInfo(this, PreferenceUtils.getToken(this), new ResultApi() {
                    @Override
                    public void resultApi(Object object) {
                        if (object != null) {
                            PopupApiForm popupApiForm = (PopupApiForm) object;
                            resolvePopupData(popupApiForm);
                            // show popup policy updated
                            checkAndShowPolicyPopupFirstTime();
                        }
                    }
                });
            }
        }
    }


    /**
     * Action: ---> 1:Apply Promotion, 2:Invite Friend, 3:Apply Event, 4:View Hotel, 5:View Notice, 6:View Link
     * <1> Open Promotion ---->check applied: ------>true:------>disable get coupon
     * false:------> show get Coupon
     * <2> Invite friend; ------>Old Popup
     * <3>, <4>, <5>------>targetSn: ------> When click button
     * <6> ------->targetInfo: ------->null: --------> hide button see detail
     * !null --------> open webView with link = targetInfo
     */
    private void resolvePopupData(PopupApiForm popupApiForm) {
        if (popupApiForm.getPopup() != null) {
            PopupForm popup = popupApiForm.getPopup();
            popupTargetSn = popup.getTargetSn();

            int getCouponButtonStatus = ParamConstants.BUTTON_INVISIBLE;
            int seeDetailButtonStatus = ParamConstants.BUTTON_SHOW;

            if (popup.getAction() == PopupForm.ACTION_INVITE) { // ==2
                //Invite Friend
                checkAndShowInviteDialog(popup);
            } else {
                if (popup.getAction() == PopupForm.ACTION_PROMOTION) { //==1
                    if (!popup.isApplied() && popup.isCanApply()) {
                        getCouponButtonStatus = ParamConstants.BUTTON_SHOW;
                    } else if (!popup.isCanApply()) {
                        getCouponButtonStatus = ParamConstants.BUTTON_HIDE;

                    }
                } else {
                    // Hide Button Get Coupon
                    getCouponButtonStatus = ParamConstants.BUTTON_HIDE;
                    if (popup.getAction() == PopupForm.ACTION_LINK && (popup.getTargetInfo() == null || popup.getTargetInfo().equals(""))) {
                        // Hide Button See Detail
                        seeDetailButtonStatus = ParamConstants.BUTTON_HIDE;
                    }
                }
                /*
                * Popup Center
                */
                showNewPromotionPopup(popup, getCouponButtonStatus, seeDetailButtonStatus);
            }
        }

        //// Rating
        if (popupApiForm.getLastCheckin() != null && popupApiForm.getLastCheckin().getSn() != 0) {
            Intent intent = new Intent(MainActivity.this, RateReviewPopupCheckinActivity.class);
            intent.putExtra("LastBookingForm", popupApiForm.getLastCheckin());
            startActivity(intent);
        } else if (popupApiForm.getLastBooking() != null && popupApiForm.getLastBooking().getSn() != 0) {
            Intent intent = new Intent(MainActivity.this, RateReviewPopupNoCheckinActivity.class);
            intent.putExtra("LastBookingForm", popupApiForm.getLastBooking());
            startActivity(intent);
        }
    }

    private void checkAndShowPolicyPopupFirstTime() {
        if (!PreferenceUtils.getReadStatusPolicy(MainActivity.this) && !PreferenceUtils.getToken(MainActivity.this).equals("")) {
            preparePolicyPopup();
        }
    }

    /*
    *  Popup Center show
    */
    private void showNewPromotionPopup(final PopupForm popup, final int statusButtonGetCoupon, final int statusButtonSeeDetail) {
        setButtonName("SPopCenter");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PromotionPopup.getInstance().showNew(MainActivity.this, popup, statusButtonGetCoupon, statusButtonSeeDetail, new CallBackListenerPopupCenter() {
                    @Override
                    public void onSeeDetail(String targetInfo) {
                        int action = popup.getAction();
                        if (action == PopupForm.ACTION_LINK && targetInfo != null) {
                            //Goto Webview
                            MyLog.writeLog("");
                        } else if (action == PopupForm.ACTION_HOTEL) {

                            gotoHotelDetailDeepLink(popup.getTargetSn());

                        } else if (action == PopupForm.ACTION_NOTICE) {

                            //Goto View Detail
                            gotoNoticeDetailFromPoup(popup.getTargetSn());

                        } else if (action == PopupForm.ACTION_PROMOTION || action == PopupForm.ACTION_EVENT) {

                            //Goto Promotion Detail
                            gotoPromotionDetail(popup.getTargetSn(), popup.getAction());

                        } else if (action == PopupForm.ACTION_DISTRICT) {

                            if (targetInfo != null) {

                                Intent intent = new Intent(MainActivity.this, IntentTemp.class);
                                intent.setAction(ParamConstants.ACTION_CHANGE_AREA);
                                intent.putExtra(ParamConstants.ACTION_CHANGE_AREA, targetInfo);
                                startActivityForResult(intent, ParamConstants.REQUEST_CHANGE_AREA);

                            }
                        }
                    }

                    @Override
                    public void onGetCoupon(int targetSn, Dialog dialog) {
                        checkAndApplyCoupon(targetSn, dialog);
                    }
                });
            }
        }, 100);
    }

    private void handleChangeDistrict(String targetInfo) {

        Gson gson = new Gson();
        District district = gson.fromJson(targetInfo, District.class);
        //Sync Home Fragment & Map Fragment
        //Set Position Home

        homeHotelRequest.setSort(SortType.DISTANCE.getType());

        BaseFragment baseFragment = findFragment(FragmentType.HOME);
        if (baseFragment != null && baseFragment.isAdded()) {
            baseFragment.chooseArea(district.getSn(), district.getName());
        } else {
            startIntent(SplashActivity.class, true, null, null);
        }

        baseFragment = findFragment(FragmentType.MAP);
        if (baseFragment != null && baseFragment.isAdded()) {
            baseFragment.chooseArea(district.getSn(), district.getName());
        } else {
            startIntent(SplashActivity.class, true, null, null);
        }
    }

    private void gotoPromotionDetail(int sn, int action) {
        Intent intent = new Intent(MainActivity.this, PromotionDetailActivity.class);
        intent.putExtra("promotionSn", sn);
        intent.putExtra("actionPopup", action);
        startActivityForResult(intent, ParamConstants.EVENT_PROMOTION_REQUEST);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void checkAndApplyCoupon(int sn, Dialog dialog) {
        // check login or not
        if (PreferenceUtils.getToken(this).equals("")) {
            if (dialog != null) {
                dialog.dismiss();
            }
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_TO_APPLY_PROMOTION);
            return;
        }
        applyCoupon(sn, dialog);
    }

    private void applyCoupon(int sn, final Dialog dialog) {
        ControllerApi.getmInstance().applyPromotionEvent(MainActivity.this, sn, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (object != null) {
                    gotoMyCoupon();
                }
            }
        });
    }

    private void gotoMyCoupon() {
        Intent intent = new Intent(MainActivity.this, MyCouponActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    //Check Invite
    public void checkAndShowInviteDialog(PopupForm popupForm) {
        long limitTime = PreferenceUtils.getInvitTimePopup(this);
        if (limitTime != 0) {
            Calendar limitCal = Calendar.getInstance();
            limitCal.setTimeInMillis(limitTime);
            limitCal.add(Calendar.DATE, 7);
            if (limitCal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                PreferenceUtils.setInvitTimePopup(MainActivity.this, 0);
                showInvitePopup(popupForm);
            }
        } else {
            showInvitePopup(popupForm);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void showInvitePopup(final PopupForm popupForm) {
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.invite_friend_center_popup);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        WebView tvInviteFriendDescription = dialog.findViewById(R.id.tvInviteFriendDescription);
        tvInviteFriendDescription.getSettings().setJavaScriptEnabled(true);
        tvInviteFriendDescription.loadDataWithBaseURL("", popupForm.getContent(), "text/html", "UTF-8", "");
        dialog.findViewById(R.id.btnInviteFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreferenceUtils.getToken(MainActivity.this).equals("")) {
                    Intent intent = new Intent(MainActivity.this, InviteFriendActivity.class);
                    intent.putExtra("InviteFriendType", InviteFriendType.POPUP.ordinal());
                    intent.putExtra("promotionDiscount", popupForm.getDiscount());
                    intent.putExtra("discountType", popupForm.getDiscountType());
                    startActivity(intent);
                    dialog.dismiss();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);
                }

            }
        });

        final ImageView btnCheckWeek = dialog.findViewById(R.id.btnCheckWeek);
        if (PreferenceUtils.getInvitTimePopup(MainActivity.this) == 0) {
            btnCheckWeek.setImageResource(R.drawable.checkbox);
        } else {
            btnCheckWeek.setImageResource(R.drawable.checkbox_selected);
        }

        btnCheckWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceUtils.getInvitTimePopup(MainActivity.this) == 0) {
                    PreferenceUtils.setInvitTimePopup(MainActivity.this, Calendar.getInstance().getTimeInMillis());
                    btnCheckWeek.setImageResource(R.drawable.checkbox_selected);
                } else {
                    PreferenceUtils.setInvitTimePopup(MainActivity.this, 0);
                    btnCheckWeek.setImageResource(R.drawable.checkbox);
                }
            }
        });

        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void gotoNoticeDetailFromPoup(int targetSn) {
        Intent resultIntent = new Intent(MainActivity.this, NoticeAppDetailActivity.class);
        resultIntent.putExtra("NOTIFICATON_SEND", true);
        NotificationData notice = new NotificationData();
        notice.setSn(targetSn);
        resultIntent.putExtra("NotificationData", notice);
        startActivity(resultIntent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void gotoHotelDetailDeepLink(int hotelDeeplinkSn) {
        Intent intent = new Intent(this, HotelDetailActivity.class);
        intent.putExtra("sn", hotelDeeplinkSn);
        intent.putExtra("RoomAvailable", true);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void checkNotification() {
        switch (notificationData.getType()) {
            case NotificationType.APP_NOTICE:
                if (notificationData.getTargetType() == 0) {
                    gotoNotificationAppNotice(notificationData);
                } else if (notificationData.getTargetType() == ParamConstants.TARGET_TYPE_PROMOTION) {
                    // check login or not
                    popupTargetSn = notificationData.getTargetSn();
                    handlePromotionNotice(notificationData);
                }
                break;
            case NotificationType.COUNSELING:
                gotoNotificationCounseling(notificationData);
                break;
            case NotificationType.USER_BOOKING:
                gotoNotificationBooking(notificationData);
                break;
            case NotificationType.NOTICE:
                gotoNotificationHotelNotice(notificationData);
                break;
            case NotificationType.COUPON_DISCOUNT:
                showCouponPopup(notificationData);
                break;
            case NotificationType.UPDATE_POLICY:
                gotoTermAndPolicy();
                break;
            case NotificationType.PROMOTION:
                gotoPromotionDetail(notificationData.getSn(), PromotionForm.TYPE_PROMOTION);
                break;
            case NotificationType.HOTEL_DETAIL:
                gotoHotelDetailDeepLink(notificationData.getSn());
            case NotificationType.STAMP:
                handleStamp(notificationData);
                break;
        }
    }

    private void handleStamp(NotificationData notificationData){
        if (notificationData != null){
            if (notificationData.getHotelSn() >0){
                gotoStampDetail(notificationData.getHotelSn());
            }else {
                gotoReservationDetail(notificationData.getSn());
            }
        }
    }

    private void handlePromotionNotice(NotificationData data) {

        if (INTENT_ACTION.equals(ParamConstants.INTENT_ACTION_OPEN_NOTICE)) {
            gotoNotificationAppNotice(data);
        } else {
            checkAndApplyCoupon(data.getTargetSn(), null);
        }
    }

    private void gotoStampDetail(long hotelSn){
        Intent resultIntent = new Intent(this, StampDetailActivity.class);
        resultIntent.putExtra("hotelSn", hotelSn);
        startActivity(resultIntent);
    }

    private void gotoReservationDetail(int userBookingSn){

        ControllerApi.getmInstance().findUserBookingDetail(MainActivity.this, userBookingSn, true, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                //hide loading
                DialogUtils.hideLoadingProgress();
                //result
                UserBookingForm userBookingForm = (UserBookingForm) object;

                Intent detail = new Intent(MainActivity.this, ReservationDetailActivity.class);
                detail.putExtra("UserBookingForm", userBookingForm);
                detail.putExtra("FLAG_SHOW_REWARD_CHECKIN", true);
                startActivity(detail);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });
    }

    private void gotoNotificationAppNotice(NotificationData notificationData) {
        Intent resultIntent = new Intent(this, NoticeAppDetailActivity.class);
        resultIntent.putExtra("NOTIFICATON_SEND", isNotification);
        resultIntent.putExtra("NotificationData", notificationData);
        startActivity(resultIntent);
    }

    private void gotoNotificationCounseling(NotificationData notificationData) {
        Intent resultIntent = new Intent(this, QADetailActivity.class);
        resultIntent.putExtra("NOTIFICATON_SEND", isNotification);
        resultIntent.putExtra("NotificationData", notificationData);
        startActivity(resultIntent);
    }

    private void gotoNotificationBooking(NotificationData notificationData) {
        Intent resultIntent = new Intent(this, HistoryActivity.class);
        resultIntent.putExtra("NOTIFICATON_SEND", isNotification);
        resultIntent.putExtra("NotificationData", notificationData);
        startActivity(resultIntent);
    }

    private void gotoNotificationHotelNotice(NotificationData notificationData) {
        Intent intent = new Intent(this, HotelDetailActivity.class);
        intent.putExtra("sn", notificationData.getHotelSn());
        intent.putExtra("RoomAvailable", 1);
        intent.putExtra("NOTIFICATON_SEND", isNotification);
        intent.putExtra("NotificationData", notificationData);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void gotoTermAndPolicy() {
        if (PreferenceUtils.getToken(this).equals("")) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, ParamConstants.REQUEST_LOGIN_TO_SEE_POLICY);
        } else {
            Intent intent = new Intent(this, TermPrivacyPolicyActivity.class);
            startActivity(intent);
        }

    }

    private void showCouponPopup(NotificationData notificationData) {
        // Show Popup Discount

        String discount = notificationData.getOtherInfoList()[0];

        if (notificationData.getOtherInfoList()[1].equals("1")) {
            discount = Utils.formatCurrency(Integer.parseInt(discount) * 1000) + " VNĐ";
        } else if (notificationData.getOtherInfoList()[1].equals("2")) {
            discount = discount + "%";
        }

        String message = getResources().getString(R.string.msg_3_9_congrat_receive_coupon_successful);
        message = message.replace("couponNumber", notificationData.getOtherInfoList()[3]);
        message = message.replace("couponMoney", discount);

        if (message != null) {
            Dialag.getInstance().show(MainActivity.this, false, false, true, null, message, getString(R.string.ok), null, null, Dialag.BTN_LEFT, new CallbackDialag() {
                @Override
                public void button1() {

                }

                @Override
                public void button2() {

                }

                @Override
                public void button3(Dialog dialog) {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        try {

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (int i = 0; i < fragments.size(); i++) {
                transaction.remove(fragments.get(i));
            }
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.writeLog("MainAvtivity onDestroy----------------------->" + e);
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Get Promotion Info Form
        if (HotelApplication.mapPromotionInfoForm == null || HotelApplication.mapPromotionInfoForm.size() == 0) {
            ControllerApi.getmInstance().findPromotionInformation(MainActivity.this, new CallbackPromotionInfoForm() {
                @Override
                public void map(Map<String, PromotionInfoForm> map) {

                    HotelApplication.mapPromotionInfoForm = map;

                }
            });
        }

        findViewById(R.id.btnFloatingButton).setVisibility(View.VISIBLE);

        /*
        / Set Badge
        */
        int count = PreferenceUtils.getCounterNotifi(this);
        if (count > 0 && !PreferenceUtils.getToken(this).equals("")) {
            notificationBadge.setNumber(count);
        } else {
            notificationBadge.clear();
        }

        /*
        / Location Change
        */
        MyLog.writeLog("-----\\\\----->MainActivity<----////----- Resume");

        setMinDistance();

        CheckLocation checkLocation = new CheckLocation(this);
        if (checkLocation.getStatusLocation()) {
            checkLocationChange();
        }
    }

    private void setMinDistance() {
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
    }

    private Location getLocationFromService() {
        MyLocationService myLocationService = new MyLocationService(this, LocationManager.NETWORK_PROVIDER);
        return myLocationService.getMyLocationFromNetworkProvider();
    }

    private void checkLocationChange() {
        MyLog.writeLog("-----\\\\----->HomeFragment<----////----- Change address");
        //Get Location Google
        myLocation = MyLocationApi.getMyLocation();

        //Get Location Network
        if (myLocation == null) {
            myLocation = getLocationFromService();
        }

        if (myLocation != null) {

            if (HotelApplication.newLocation == null) {
                HotelApplication.newLocation = myLocation;
            }

            float distance = Utils.getDistanceBetween2Location(HotelApplication.newLocation, myLocation);

            if (distance >= LIMIT_DISTANCE) {

                myAddress = MyLocationApi.getMyAddress(this, myLocation);

                double latitude = myLocation.getLatitude();
                double longitude = myLocation.getLongitude();

                String district = myAddress[1];
                String city = myAddress[2];
                String country = myAddress[3];

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
                userLocationForm.setAddress((PreferenceUtils.getCurrentAddress(this)));


                //Send to Server
                HotelApplication.serviceApi.updateUserLocation(userLocationForm, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new retrofit2.Callback<List<HotelForm>>() {

                    @Override
                    public void onResponse(Call<List<HotelForm>> call, final Response<List<HotelForm>> response) {
                        if (response.isSuccessful()) {
                            MyLog.writeLog("updateUserLocation Successful");

                            //Store Location
                            HotelApplication.newLocation = myLocation;

                            //STORE MY LOCATION DEFAULT
                            PreferenceUtils.setLatLocation(MainActivity.this, Double.toString(myLocation.getLatitude()));
                            PreferenceUtils.setLongLocation(MainActivity.this, Double.toString(myLocation.getLongitude()));

                            //STORE MY ADDRESS
                            String addr = myAddress[0];
                            PreferenceUtils.setCurrentAddress(MainActivity.this, addr);

                            //CALLBACK TO HOME
                            BaseFragment baseFragment = findFragment(FragmentType.HOME);
                            if (baseFragment != null && baseFragment.isAdded()) {
                                baseFragment.onUpdateLocation();
                            } else {
                                startIntent(SplashActivity.class, true, null, null);
                            }

                            baseFragment = findFragment(FragmentType.MAP);
                            if (baseFragment != null && baseFragment.isAdded()) {
                                baseFragment.onUpdateLocation();
                            } else {
                                startIntent(SplashActivity.class, true, null, null);
                            }

                            //Stop Service Location
                            //stopService(new Intent(MainActivity.this, MyLocationService.class));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<HotelForm>> call, Throwable t) {
                        MyLog.writeLog("updateUserLocationonFailure");
                        //Show cannot connect to server
                    }
                });
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        /*
        / Set Shortcut Badger
        */
        ShortcutBadger.applyCount(this, PreferenceUtils.getCounterNotifi(this)); //Delete = 0
    }

    public void addFragment(FragmentType fragmentType) {

        if (currentFragmentType == fragmentType) {
            return;
        }
        boolean fragmentExist = false;

        if (findFragmentType(fragmentType) != null) {
            fragmentExist = true;
            MyLog.writeLog("Manactivity " + fragmentType + "--->Exist<---");
        }

        if (fragmentExist) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (int i = 0; i < fragments.size(); i++) {
                transaction.hide(fragments.get(i));
            }
            transaction.setCustomAnimations(R.anim.right_to_left, R.anim.stable);
            transaction.show(findFragment(fragmentType));
            //transaction.commit();
            transaction.commitAllowingStateLoss();

//            if (fragmentType == FragmentType.MY_PAGE) {
//                findFragment(fragmentType).onRefreshData();
//                MyLog.writeLog("Manactivity " + fragmentType + "--->Refresh<---");
//            }

        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            BaseFragment baseFragment = BaseFragment.newInstance(fragmentType);
            transaction.setCustomAnimations(R.anim.right_to_left, R.anim.stable);
            transaction.add(R.id.frmBody, baseFragment, fragmentType.toString());
            transaction.addToBackStack(fragmentType.toString());
            transaction.commitAllowingStateLoss();
            fragmentTypes.add(fragmentType);
            fragments.add(baseFragment);
            MyLog.writeLog("Manactivity " + fragmentType + "--->Add<---");
        }
        currentFragmentType = fragmentType;
    }


    private FragmentType findFragmentType(FragmentType fragmentType) {
        for (FragmentType type : fragmentTypes) {
            if (type == fragmentType) {
                return fragmentType;
            }
        }
        return null;
    }

    private BaseFragment findFragment(FragmentType fragmentType) {
//        for (int i = 0; i < fragmentTypes.size(); i++) {
//            if (fragmentType == fragmentTypes.get(i)) {
//                return fragments.get(i);
//            }
//        }
        BaseFragment baseFragment = null;
        switch (fragmentType) {
            case HOME:
                baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(FragmentType.HOME.toString());
                break;
            case MAP:
                baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(FragmentType.MAP.toString());
                break;
            case EVENT:
                baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(FragmentType.EVENT.toString());
                break;
            case MY_PAGE:
                baseFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(FragmentType.MY_PAGE.toString());
                break;
        }
        return baseFragment;
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentType == FragmentType.MAP) {
            if (boxHotelPopup.getVisibility() == View.VISIBLE) {
                hideHotelBox();
                findFragment(currentFragmentType).closeClickEvent();
            } else {
                showTab(FragmentType.HOME);
            }
        } else if (currentFragmentType == FragmentType.HOME) {
            exit++;
            if (exit == 2) {
                finish();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.exit), Toast.LENGTH_SHORT).show();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = 0;
                }
            }, 3000);
        } else {
            showTab(FragmentType.HOME);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgTab1:
            case R.id.btnTab1:
                if (currentFragmentType != FragmentType.HOME) {

                    boxMapGuide.setVisibility(View.GONE);

                    showTab(FragmentType.HOME);
                    setButtonName("SHome");
                }
                break;
            case R.id.imgTab2:
            case R.id.btnTab2:
                if (currentFragmentType != FragmentType.MAP) {
                    showTab(FragmentType.MAP);

                    //Show Map Guide
                    boxMapGuide.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boxMapGuide.setVisibility(View.GONE);
                        }
                    }, 3000);
                    setButtonName("SMap");
                }
                break;
            case R.id.imgTab3:
            case R.id.btnTab3:
                if (currentFragmentType != FragmentType.EVENT) {

                    boxMapGuide.setVisibility(View.GONE);

                    showTab(FragmentType.EVENT);
                    setButtonName("SPromo");
                }
                break;
            case R.id.imgTab4:
            case R.id.btnTab4:
                if (currentFragmentType != FragmentType.MY_PAGE) {

                    boxMapGuide.setVisibility(View.GONE);

                    if (!PreferenceUtils.getToken(this).equals("")) {
                        showTab(FragmentType.MY_PAGE);
                    } else {
                        gotoLoginScreen(ParamConstants.REQUEST_LOGIN_MY_PAGE);
                    }
                    setButtonName("SSetPage");
                }
                break;
            case R.id.btnFloatingButton:
                boxMapGuide.setVisibility(View.GONE);
                setButtonName("SFloatButton");
                showFloatingDialog();
                break;
            case R.id.btnSort:
                setButtonName("SHomeSort");
                showSortDialog();
                break;
            case R.id.btnFilterMap:
                showFilterMap();
                break;
            case R.id.btnSearch:
                gotoSearchPage();
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.boxHotelPopup:
                gotoHotelDetail();
                break;

        }
    }

    /*
    / Filter for Map
    */
    private void showFilterMap() {
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_filter_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }

        final ImageView chkAvailability, chkPromotion, chkNew, chkHot, chkFlashSale;
        chkAvailability = dialog.findViewById(R.id.chkAvailability);
        chkPromotion = dialog.findViewById(R.id.chkPromotion);
        chkNew = dialog.findViewById(R.id.chkNew);
        chkHot = dialog.findViewById(R.id.chkHot);
        chkFlashSale = dialog.findViewById(R.id.chkFlashSale);

        //Set Default
        chkAvailability.setImageResource(R.drawable.radio_bg);
        chkPromotion.setImageResource(R.drawable.radio_bg);
        chkNew.setImageResource(R.drawable.radio_bg);
        chkHot.setImageResource(R.drawable.radio_bg);
        chkFlashSale.setImageResource(R.drawable.radio_bg);

        //Set Select
        if (isFilterAvailable) {
            chkAvailability.setImageResource(R.drawable.radio_check_bg);
        }
        if (isFilterPromotion) {
            chkPromotion.setImageResource(R.drawable.radio_check_bg);
        }
        if (isFilterNew) {
            chkNew.setImageResource(R.drawable.radio_check_bg);
        }
        if (isFilterHot) {
            chkHot.setImageResource(R.drawable.radio_check_bg);
        }
        if (isFilterFlashSale) {
            chkFlashSale.setImageResource(R.drawable.radio_check_bg);
        }

        //btn Close Click
        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //btn Available Click
        dialog.findViewById(R.id.btnAvailability).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFilterType = MapFilterType.Available;
                isFilterAvailable = !isFilterAvailable;
                if (isFilterAvailable) {
                    chkAvailability.setImageResource(R.drawable.radio_check_bg);
                } else {
                    chkAvailability.setImageResource(R.drawable.radio_bg);
                }
                findFragment(currentFragmentType).onFilter(mapFilterType, isFilterAvailable);
                dialog.dismiss();
            }
        });

        //btn Promotion Click
        dialog.findViewById(R.id.btnPromotion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFilterType = MapFilterType.Promotion;
                isFilterPromotion = !isFilterPromotion;
                if (isFilterAvailable) {
                    chkPromotion.setImageResource(R.drawable.radio_check_bg);
                } else {
                    chkPromotion.setImageResource(R.drawable.radio_bg);
                }

                findFragment(currentFragmentType).onFilter(mapFilterType, isFilterPromotion);
                dialog.dismiss();
            }
        });

        //btn New Click
        dialog.findViewById(R.id.btnNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFilterType = MapFilterType.New;
                isFilterNew = !isFilterNew;
                if (isFilterAvailable) {
                    chkNew.setImageResource(R.drawable.radio_check_bg);
                } else {
                    chkNew.setImageResource(R.drawable.radio_bg);
                }

                findFragment(currentFragmentType).onFilter(mapFilterType, isFilterNew);
                dialog.dismiss();
            }
        });

        //btn Hot Click
        dialog.findViewById(R.id.btnHot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFilterType = MapFilterType.Hot;
                isFilterHot = !isFilterHot;
                if (isFilterAvailable) {
                    chkHot.setImageResource(R.drawable.radio_check_bg);
                } else {
                    chkHot.setImageResource(R.drawable.radio_bg);
                }
                findFragment(currentFragmentType).onFilter(mapFilterType, isFilterHot);
                dialog.dismiss();
            }
        });

        //btn FlashSale Click
        dialog.findViewById(R.id.btnFlashSale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFilterType = MapFilterType.FlashSale;
                //Set true/false
                isFilterFlashSale = !isFilterFlashSale;

                if (isFilterFlashSale) {
                    chkFlashSale.setImageResource(R.drawable.radio_check_bg);
                } else {
                    chkFlashSale.setImageResource(R.drawable.radio_bg);
                }
                findFragment(currentFragmentType).onFilter(mapFilterType, isFilterFlashSale);
                dialog.dismiss();
            }
        });
    }

    private void gotoHotelDetail() {
        if (hotelForm != null) {
            Intent intent = new Intent(this, HotelDetailActivity.class);
            intent.putExtra("sn", hotelForm.getSn());
            intent.putExtra("RoomAvailable", hotelForm.getRoomAvailable());
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        }
    }

    public void gotoLoginScreen(int requestCode) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, requestCode);
    }

    private void gotoSearchPage() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
//        startActivityForResult(intent, REQUEST_SEARCH_TEXT);
    }

    private void showSortDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.short_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }

        TextViewSFRegular tvPromotion = dialog.findViewById(R.id.tvPromotion);
        TextViewSFRegular tvHot = dialog.findViewById(R.id.tvHot);
        TextViewSFRegular tvNew = dialog.findViewById(R.id.tvNew);
        TextViewSFRegular tvAlphabet = dialog.findViewById(R.id.tvAlphabet);
        TextViewSFRegular tvDistance = dialog.findViewById(R.id.tvDistance);

        tvPromotion.setTextColor(Color.WHITE);
        tvHot.setTextColor(Color.WHITE);
        tvNew.setTextColor(Color.WHITE);
        tvAlphabet.setTextColor(Color.WHITE);
        tvDistance.setTextColor(Color.WHITE);

        int sort = homeHotelRequest.getSort();
        if (sort == SortType.PROMOTION.getType()) {
            tvPromotion.setTextColor(getResources().getColor(R.color.org));
        } else if (sort == SortType.HOT.getType()) {
            tvHot.setTextColor(getResources().getColor(R.color.org));
        } else if (sort == SortType.NEW.getType()) {
            tvNew.setTextColor(getResources().getColor(R.color.org));
        } else if (sort == SortType.ALPHABET.getType()) {
            tvAlphabet.setTextColor(getResources().getColor(R.color.org));
        } else if (sort == SortType.DISTANCE.getType()) {
            tvDistance.setTextColor(getResources().getColor(R.color.org));
        }
        if (sort == 0) {
            tvDistance.setTextColor(getResources().getColor(R.color.org));
        }

        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tvPromotion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeHotelRequest.setSort(SortType.PROMOTION.getType());
                findFragment(currentFragmentType).onRefreshData();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tvHot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeHotelRequest.setSort(SortType.HOT.getType());
                findFragment(currentFragmentType).onRefreshData();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tvNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeHotelRequest.setSort(SortType.NEW.getType());
                findFragment(currentFragmentType).onRefreshData();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tvAlphabet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeHotelRequest.setSort(SortType.ALPHABET.getType());
                findFragment(currentFragmentType).onRefreshData();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tvDistance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeHotelRequest.setSort(SortType.DISTANCE.getType());
                findFragment(currentFragmentType).onRefreshData();
                dialog.dismiss();
            }
        });
    }

    //Show Float Button
    private void showFloatingDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.floating_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }

        LinearLayout btnBookNow = dialog.findViewById(R.id.btnBookNow);
        LinearLayout btnQRScan = dialog.findViewById(R.id.btnQRScan);
        LinearLayout btnReportNewHotel = dialog.findViewById(R.id.btnReportNewHotel);
        ImageView btnCloseFloating = dialog.findViewById(R.id.btnCloseFloating);

        Animation closeAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        closeAnim.setStartOffset(0);

        Animation newAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        newAnim.setStartOffset(100);

        Animation reviewAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        reviewAnim.setStartOffset(200);

        Animation bookAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        bookAnim.setStartOffset(300);

        btnCloseFloating.startAnimation(closeAnim);
        btnReportNewHotel.startAnimation(newAnim);
        btnQRScan.startAnimation(reviewAnim);
        btnBookNow.startAnimation(bookAnim);

        dialog.findViewById(R.id.btnCloseFloating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btnFloatingButton).setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        findViewById(R.id.btnFloatingButton).setVisibility(View.GONE);
        //Book Now
        dialog.findViewById(R.id.btnBookNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!PreferenceUtils.getToken(MainActivity.this).equals("")) {
                    Intent intent = new Intent(MainActivity.this, BookHotelNowActivity.class);
                    startActivity(intent);
                } else {
                    gotoLoginScreen(ParamConstants.REQUEST_REPORT_NEW_HOTEL);
                }

            }
        });


        dialog.findViewById(R.id.btnReportNewHotel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!PreferenceUtils.getToken(MainActivity.this).equals("")) {
                    Intent intent = new Intent(MainActivity.this, ReportNewHotelActivity.class);
                    startActivity(intent);
                } else {
                    gotoLoginScreen(ParamConstants.REQUEST_REPORT_NEW_HOTEL);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                findViewById(R.id.btnFloatingButton).setVisibility(View.VISIBLE);
            }
        });

        dialog.findViewById(R.id.btnQRScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!PreferenceUtils.getToken(MainActivity.this).equals("")) {
                    setButtonName("SFloatQRCode");
                    openQRScanner();
                } else {
                    gotoLoginScreen(ParamConstants.QR_LOGIN_REQUEST);
                }
            }
        });

        dialog.findViewById(R.id.btnRecent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!PreferenceUtils.getToken(MainActivity.this).equals("")) {
                    Intent intent = new Intent(MainActivity.this, RecentBookingActivity.class);
                    startActivity(intent);
                } else {
                    gotoLoginScreen(ParamConstants.RECENT_BOOKING_REQUEST);
                }
            }
        });
    }

    public void openQRScanner() {
        int hasCameraPermission;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        Intent intent = new Intent(this, ZBarScannerActivity.class);
        startActivityForResult(intent, ParamConstants.ZBAR_SCANNER_REQUEST);
    }

    public void showTab(FragmentType iTab) {
        imgTabs[0].setImageResource(R.drawable.home);
        imgTabs[1].setImageResource(R.drawable.map);
        imgTabs[2].setImageResource(R.drawable.event);
        imgTabs[3].setImageResource(R.drawable.mypage);
        btnFilterMap.setVisibility(View.GONE);
        btnSort.setVisibility(View.GONE);
        btnSearch.setVisibility(View.GONE);


        switch (iTab) {
            case HOME:
                tvTitle.setText(getString(R.string.app_name));
                btnSort.setVisibility(View.VISIBLE);
                btnSearch.setVisibility(View.VISIBLE);
                imgTabs[0].setImageResource(R.drawable.home_on);

                /*
                 / Show Badge If exist
                */
                int count = PreferenceUtils.getCounterNotifi(this);
                if (count > 0) {
                    notificationBadge.setNumber(count);
                } else {
                    notificationBadge.clear();
                }

                break;
            case MAP:
                btnFilterMap.setVisibility(View.VISIBLE);
                btnSearch.setVisibility(View.VISIBLE);
                tvTitle.setText(getString(R.string.menu_map));
                imgTabs[1].setImageResource(R.drawable.map_on);

                /*
                 / Show Badge If exist
                */
                int count1 = PreferenceUtils.getCounterNotifi(this);
                if (count1 > 0) {
                    notificationBadge.setNumber(count1);
                } else {
                    notificationBadge.clear();
                }

                break;
            case EVENT:
                imgTabs[2].setImageResource(R.drawable.event_on);
                tvTitle.setText(getString(R.string.menu_event_promotion));

                /*
                 / Show Badge If exist
                */
                int count2 = PreferenceUtils.getCounterNotifi(this);
                if (count2 > 0) {
                    notificationBadge.setNumber(count2);
                } else {
                    notificationBadge.clear();
                }

                break;
            case MY_PAGE:
                imgTabs[3].setImageResource(R.drawable.mypage_on);
                tvTitle.setText(getString(R.string.menu_my_page));
                /*
                /Clear Badge
                */
                notificationBadge.clear();
                break;
        }
        addFragment(iTab);
    }

    public HomeHotelRequest getHomeHotelRequest() {
        return this.homeHotelRequest;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ParamConstants.REQUEST_CHOOSE_AREA_FAVORITE) {
            //Set Type Sort
            homeHotelRequest.setSort(SortType.DISTANCE.getType());
            int position = data.getIntExtra("position", -1);
            findFragment(FragmentType.HOME).onRefreshFavorite(position);
            findFragment(FragmentType.MAP).onRefreshFavorite(position);
        } else if (requestCode == ParamConstants.REQUEST_RESET_NEARBY) {
            //Set Type Sort
            homeHotelRequest.setSort(SortType.DISTANCE.getType());
            findFragment(FragmentType.HOME).onRefreshNearby();
            findFragment(FragmentType.MAP).onRefreshNearby();
        } else if (requestCode == ParamConstants.REQUEST_CHOOSE_AREA_HOME) {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    //Set Type Sort

                    homeHotelRequest.setSort(SortType.DISTANCE.getType());
                    int districtSn = data.getIntExtra("districtSn", 0);
                    String districtName = data.getStringExtra("districtName");

                    findFragment(FragmentType.HOME).chooseArea(districtSn, districtName);
                    findFragment(FragmentType.MAP).chooseArea(districtSn, districtName);

                }
            } catch (Exception e) {
                e.printStackTrace();
                MyLog.writeLog("MainActivity onActivityResult-------------------------------> " + e);
            }

        } else if (requestCode == ParamConstants.REQUEST_CHOOSE_FAVORITE_AREA_HOME) {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    //Set Type Sort
                    homeHotelRequest.setSort(SortType.DISTANCE.getType());
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        int districtSn = bundle.getInt("districtSn", 0);
                        String districtName = data.getExtras().getString("districtName");

                        findFragment(FragmentType.HOME).chooseArea(districtSn, districtName);
                        findFragment(FragmentType.MAP).chooseArea(districtSn, districtName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyLog.writeLog("MainActivity onActivityResult-------------------------------> " + e);
            }

        } else if (requestCode == ParamConstants.REQUEST_CHOOSE_AREA_MAP) {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    //Set Type Sort
                    homeHotelRequest.setSort(SortType.DISTANCE.getType());
                    int districtSn = data.getIntExtra("districtSn", 0);
                    String districtName = data.getStringExtra("districtName");

                    findFragment(FragmentType.HOME).chooseArea(districtSn, districtName);
                    findFragment(FragmentType.MAP).chooseArea(districtSn, districtName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyLog.writeLog("MainActivity onActivityResult-------------------------------> " + e);
            }

        } else if (requestCode == ParamConstants.REQUEST_CHOOSE_FAVORITE_AREA_MAP) {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    //Set Type Sort
                    homeHotelRequest.setSort(SortType.DISTANCE.getType());
                    int districtSn = data.getIntExtra("districtSn", 0);
                    String districtName = data.getStringExtra("districtName");

                    findFragment(FragmentType.HOME).chooseArea(districtSn, districtName);
                    findFragment(FragmentType.MAP).chooseArea(districtSn, districtName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyLog.writeLog("MainActivity onActivityResult-------------------------------> " + e);
            }

        } else if (requestCode == ParamConstants.REQUEST_LOGIN_MY_PAGE) {
            if (resultCode == Activity.RESULT_OK) {
                showTab(FragmentType.MY_PAGE);
                try {
                    findFragment(FragmentType.HOME).requestAreaSetting(false);
                } catch (Exception e) {
                    MyLog.writeLog("MainActivity onActivityResult-------------------------------> " + e);
                }
            }
        } else if (requestCode == ParamConstants.QUICK_REVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                showTab(FragmentType.HOME);
            }
        } else if (requestCode == ParamConstants.REQUEST_LOGIN_AREA_SETTING_HOME) {
            if (resultCode == Activity.RESULT_OK) {
                findFragment(currentFragmentType).requestAreaSetting(false);
            }
        } else if (requestCode == ParamConstants.REQUEST_LOGIN_AREA_SETTING_MAP) {
            if (resultCode == Activity.RESULT_OK) {

            }
        } else if (requestCode == ParamConstants.REQUEST_CHANGE_PROFILE) {
            if (resultCode == ParamConstants.REQUEST_CHANGE_PROFILE) {
                showTab(FragmentType.MY_PAGE);
            }
        } else if (requestCode == ParamConstants.REQUEST_LOGIN_HOME) {
            if (resultCode == RESULT_OK) {
                try {
                    findFragment(currentFragmentType).onRefreshData();
                    findFragment(currentFragmentType).requestAreaSetting(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    MyLog.writeLog("MainActivity onActivityResult-------------------------------> " + e);
                }
            }
        } else if (requestCode == ParamConstants.REQUEST_MY_FAVORITE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    int districtSn = bundle.getInt("districtSn");
                    String districtName = data.getExtras().getString("districtName");
                    homeHotelRequest.setDistrictSn(Integer.toString(districtSn));
                    homeHotelRequest.setSort(SortType.DISTANCE.getType());
                    showTab(FragmentType.HOME);
                    try {

                        findFragment(FragmentType.HOME).chooseArea(districtSn, districtName);
                        findFragment(FragmentType.MAP).chooseArea(districtSn, districtName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MyLog.writeLog("MainActivity onActivityResult-------------------------------> " + e);
                    }
                }
            }
        } else if (requestCode == ParamConstants.EVENT_PROMOTION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (currentFragmentType == FragmentType.EVENT) {
                    BaseFragment eventFragment = findFragment(FragmentType.EVENT);
                    if (eventFragment != null && eventFragment.isAdded()) {
                        eventFragment.onRefreshData();
                    }
                } else if (currentFragmentType == FragmentType.HOME) {
                    findFragment(currentFragmentType).initAdvertising();
                }
            }
        } else if (requestCode == ParamConstants.LOGIN_USER_INFO_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                findFragment(currentFragmentType).initDataMyPageFragment();
                MyLog.writeLog("MainActivity ---->MyPage---->InitData");
            }
        } else if (requestCode == ParamConstants.ZBAR_SCANNER_REQUEST) {
            if (resultCode == RESULT_OK) {

                String dataResult = data.getStringExtra(ZBarConstants.SCAN_RESULT);
                updateCheckin(dataResult);

            } else if (resultCode == RESULT_CANCELED) {
                MyLog.writeLog("");
            }
        } else if (requestCode == ParamConstants.REQUEST_LOGIN_TO_SEE_POLICY) {
            if (resultCode == RESULT_OK) {
                gotoTermAndPolicy();
            }
        } else if (requestCode == ParamConstants.REQUEST_LOGIN_TO_APPLY_PROMOTION) {
            if (resultCode == RESULT_OK) {
                if (popupTargetSn > 0)
                    checkAndApplyCoupon(popupTargetSn, null);
            }
        } else if (requestCode == ParamConstants.REQUEST_CHANGE_AREA) {
            String targetInfo = data.getStringExtra(ParamConstants.ACTION_CHANGE_AREA);
            if (targetInfo != null) {
                handleChangeDistrict(targetInfo);
            }
        }
    }

    private HotelForm hotelForm;

    public void showHotelBox(HotelForm hotelForm) {
        this.hotelForm = hotelForm;
        if (boxHotelPopup.getVisibility() == View.GONE) {
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.hotel_slide_up);
            slide_up.setFillBefore(true);
            slide_up.setFillAfter(true);
            boxHotelPopup.setVisibility(View.INVISIBLE);
            slide_up.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    boxHotelPopup.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            boxHotelPopup.startAnimation(slide_up);
        }
        //Set Hotel Name
        tvHotelNamePopup.setText(hotelForm.getName());
        //Set Address
        tvAddressPopup.setText(hotelForm.getAddress());

        //Set Rating
        int rate = (int) hotelForm.getAverageMark() * 2;
        if (rate <= 0) {
            tvReview.setVisibility(View.GONE);
        } else {
            tvReview.setVisibility(View.VISIBLE);
            tvReview.setText(String.valueOf(rate));
        }

        //Check Flash Sale
        if (hotelForm.isFlashSale()) {
            RoomTypeForm roomTypeForm = hotelForm.getFlashSaleRoomTypeForm();
            if (roomTypeForm != null) {
                int rooms = roomTypeForm.getAvailableRoom();
                String s;
                if (rooms > 0) {
                    s = String.format(getString(R.string.txt_2_flashsale_room_left), String.valueOf(rooms));
                } else {
                    s = getString(R.string.txt_2_flashsale_sold_out);
                }
                //Set Price Status
                tvPriceStatus.setVisibility(View.VISIBLE);
                tvPriceStatus.setText(s);

                //Set Label Flash Sale
                imgIconPromotion1.setVisibility(View.VISIBLE);
                imgIconPromotion1.setImageResource(R.drawable.icon_sale);

                boxHourly.setVisibility(View.GONE);

                //Set Price Overnight Normal
                tvPriceOvenightNormal.setVisibility(View.VISIBLE);
                tvPriceOvenightNormal.setText(Utils.formatCurrency(hotelForm.getLowestPriceOvernight()));
                //StrikeThrough
                tvPriceOvenightNormal.setPaintFlags(tvPriceOvenightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                //Set Price Overnight Discount
                tvPriceOvernightDiscount.setText(Utils.formatCurrency(roomTypeForm.getPriceOvernight()));

            }
        } else {

            imgIconPromotion1.setVisibility(View.GONE);
            boxHourly.setVisibility(View.VISIBLE);

            //Set Price Status
            tvPriceStatus.setText("");

            //--------------Set Price------------
            int[] discount = Utils.getPromotionInfoForm(hotelForm.getSn());

            if (discount[0] > 0 || discount[1] > 0) {

                //Set Price Status
                tvPriceStatus.setVisibility(View.VISIBLE);
                tvPriceStatus.setText(getString(R.string.txt_2_coupon_applied));
                //Hourly
                if (discount[0] > 0) {
                    int priceHourlyDiscount = hotelForm.getLowestPrice() - discount[0];

                    if (priceHourlyDiscount < 0) {
                        priceHourlyDiscount = 0;
                    }

                    //Set Price Hourly Normal
                    tvPriceHourlyNormal.setVisibility(View.VISIBLE);
                    tvPriceHourlyNormal.setText(Utils.formatCurrency(hotelForm.getLowestPrice()));
                    //StrikeThrough
                    tvPriceHourlyNormal.setPaintFlags(tvPriceHourlyNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    //Set Price Hourly Discount
                    tvPriceHourlyDiscount.setText(Utils.formatCurrency(priceHourlyDiscount));

                } else {

                    //Set Price Hourly Normal
                    tvPriceHourlyNormal.setVisibility(View.GONE);
                    //Set Price Hourly Discount
                    tvPriceHourlyDiscount.setText(Utils.formatCurrency(hotelForm.getLowestPrice()));

                }

                //Overnight
                if (discount[1] > 0) {
                    int priceOvernightDiscount = hotelForm.getLowestPriceOvernight() - discount[1];

                    if (priceOvernightDiscount < 0) {
                        priceOvernightDiscount = 0;
                    }

                    //Set Price Overnight Normal
                    tvPriceOvenightNormal.setVisibility(View.VISIBLE);
                    tvPriceOvenightNormal.setText(Utils.formatCurrency(hotelForm.getLowestPriceOvernight()));
                    //StrikeThrough
                    tvPriceOvenightNormal.setPaintFlags(tvPriceOvenightNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    //Set Price Overnight Discount
                    tvPriceOvernightDiscount.setText(Utils.formatCurrency(priceOvernightDiscount));
                } else {
                    //Set Price Overnight Normal
                    tvPriceOvenightNormal.setVisibility(View.GONE);

                    //Set Price Overnight Discount
                    tvPriceOvernightDiscount.setText(Utils.formatCurrency(hotelForm.getLowestPriceOvernight()));
                }

            } else {

                tvPriceStatus.setVisibility(View.GONE);

                //Set Price Hourly Normal
                tvPriceHourlyNormal.setVisibility(View.GONE);
                //Set Price Hourly Discount
                tvPriceHourlyDiscount.setText(Utils.formatCurrency(hotelForm.getLowestPrice()));

                //Set Price Overnight Normal
                tvPriceOvenightNormal.setVisibility(View.GONE);

                //Set Price Overnight Discount
                tvPriceOvernightDiscount.setText(Utils.formatCurrency(hotelForm.getLowestPriceOvernight()));

            }

        }

        /*
        /Set Icon 360
        */
        if (hotelForm.getCountExifImage() > 0) {
            img360.setVisibility(View.VISIBLE);
        } else {
            img360.setVisibility(View.GONE);
        }


        String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelHomeImage?hotelSn=" + hotelForm.getSn() + "&fileName=" + hotelForm.getHomeImageName();
        PictureUtils.getInstance().load(
                url,
                getResources().getDimensionPixelSize(R.dimen.hotel_popup_width),
                getResources().getDimensionPixelSize(R.dimen.hotel_popup_height),
                R.drawable.loading_big,
                imgHotel
        );
    }

    public void hideHotelBox() {
        if (boxHotelPopup.getVisibility() == View.VISIBLE) {
            Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.banner_move_up);
            slide_down.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    boxHotelPopup.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            boxHotelPopup.startAnimation(slide_down);
        }
    }

    private void updateCheckin(String dataResult) {

        DialogUtils.showLoadingProgress(MainActivity.this, false);
        int hotelSn = 0;
        try {
            hotelSn = Integer.parseInt(dataResult);
        } catch (Exception e) {
            MyLog.writeLog("MainActivity updateCheckin-------------------------------> " + e);
        }
        CheckInRoomDto checkInRoomDto = new CheckInRoomDto();
        checkInRoomDto.setHotelSn(hotelSn);

        HotelApplication.serviceApi.checkInHotelRoom(checkInRoomDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();

                RestResult restResult = response.body();

                if (response.isSuccessful() && restResult != null) {
                    if (restResult.getResult() == 1) {
                        if (restResult.getCount() == 1) {
                            Toast.makeText(MainActivity.this, getString(R.string.checkin_successfully), Toast.LENGTH_LONG).show();
                        } else if (restResult.getCount() == 0) {
                            Toast.makeText(MainActivity.this, getString(R.string.msg_1_5_no_reservation_to_checkin), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.checkin_failure), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    DialogUtils.showExpiredDialog(MainActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intent, ParamConstants.LOGIN_USER_INFO_REQUEST);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(MainActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(this, ZBarScannerActivity.class);
                    startActivityForResult(intent, ParamConstants.ZBAR_SCANNER_REQUEST);
                } else {
                    // Permission Denied
                    MyLog.writeLog("");

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
