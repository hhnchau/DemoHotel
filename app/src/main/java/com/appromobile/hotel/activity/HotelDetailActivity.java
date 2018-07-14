package com.appromobile.hotel.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.HotelImageDetailAdapter;
import com.appromobile.hotel.adapter.NoticeAdapter;
import com.appromobile.hotel.adapter.RoomTypeListAdapter;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.api.controllerApi.CallbackPromotionInfoForm;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.Dialag;
import com.appromobile.hotel.dialog.DialogStamp;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.enums.ShareType;
import com.appromobile.hotel.model.request.UserFavoriteDto;
import com.appromobile.hotel.model.view.FacilityForm;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.HotelImageForm;
import com.appromobile.hotel.model.view.NoticeForm;
import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.model.view.PromotionInfoForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.model.view.UserStampForm;
import com.appromobile.hotel.picture.PicturePicasso;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.picture.PictureGlide;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.ShareUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.crashlytics.android.Crashlytics;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/4/2016.
 */
public class HotelDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final int OPEN_REVIEW = 1001;
    public static final int CALL_BOOKING = 1002;
    private TextViewSFBold[] lineTabs = new TextViewSFBold[3];
    private TextViewSFBold[] lineTabsFloating = new TextViewSFBold[3];
    private ListView lvRoom, lvNotice;
    private WebView webContentInfo;
    private HotelDetailForm hotelDetailForm;
    private ViewPager imgHotelGallery;
    private TextView tvAddress, tvPhoneNo, tvTotalReview, tvDistance, tvHotelTitle, tvRoomTypeName;
    private TextView tvCountLike;
    private LinearLayout btnBook;
    private LinearLayout boxHotelInfo;
    private RelativeLayout btnLike;
    private int sn;
    private TextView tvMessage;
    private List<NoticeForm> noticeForms = null;
    private static int LOGIN_DETAIL_REQUEST_LIKE = 1000;
    private ScrollView mainScroll;
    private LinearLayout floatingTab, realTab;
    private ImageView imgPromotion, imgNew, imgHot;
    private ImageView[] stars = new ImageView[5];
    private RoomTypeListAdapter roomTypeListAdapter;
    private View imgBar;
    int barWidth = 0;
    private ImageView imgLike, imgNoImage;
    private RelativeLayout boxBar;

    private int roomAvailable;
    private boolean isNotification = false;
    private NotificationData notificationData;
    private TextView tvCommonRule;
    private View lineCommon;
    public static Activity hotelDetailActivity;

    private LinearLayout container;
    private HotelImageDetailAdapter adapter;
    private ImageView icon360;

    private RelativeLayout iconStamp;
    private TextView tvNumStamp;
    private UserStampForm userStampForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.hotel_detail_activity);
        hotelDetailActivity = this;

        setScreenName();

        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("HotelDetailActivity" + e);
        }


        /*
        * Get Intent
        */
        MyLog.writeLog("HotelDetailActivity --- onCreate");
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isNotification = bundle.getBoolean("NOTIFICATON_SEND", false);
            notificationData = bundle.getParcelable("NotificationData");

            sn = bundle.getInt("sn");
            roomAvailable = bundle.getInt("RoomAvailable", 0);
        }


        mainScroll = findViewById(R.id.mainScroll);
        container = findViewById(R.id.container);

        icon360 = findViewById(R.id.detail_hotel_icon_360);
        icon360.setOnClickListener(this);
        imgNoImage = findViewById(R.id.imgNoImage);
        imgPromotion = findViewById(R.id.imgPromotion);
        imgNew = findViewById(R.id.imgNew);
        imgHot = findViewById(R.id.imgHot);
        imgLike = findViewById(R.id.imgLike);
        floatingTab = findViewById(R.id.floatingTab);
        realTab = findViewById(R.id.realTab);
        btnLike = findViewById(R.id.btnLike);
        btnBook = findViewById(R.id.btnBook);
        tvMessage = findViewById(R.id.tvMessage);
        boxHotelInfo = findViewById(R.id.boxHotelInfo);
        imgHotelGallery = findViewById(R.id.imgHotelGallery);
        tvCommonRule = findViewById(R.id.tvCommonRule);
        lineCommon = findViewById(R.id.lineCommonRule);
        tvCountLike = findViewById(R.id.tvTotalLike);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhoneNo = findViewById(R.id.tvPhoneNo);
        tvTotalReview = findViewById(R.id.tvTotalReview);
        tvDistance = findViewById(R.id.tvDistance);
        tvHotelTitle = findViewById(R.id.tvHotelTitle);
        boxBar = findViewById(R.id.boxBar);
        tvRoomTypeName = findViewById(R.id.tvRoomTypeName);

        imgBar = findViewById(R.id.imgBar);

        stars[0] = findViewById(R.id.btnStar1);
        stars[1] = findViewById(R.id.btnStar2);
        stars[2] = findViewById(R.id.btnStar3);
        stars[3] = findViewById(R.id.btnStar4);
        stars[4] = findViewById(R.id.btnStar5);

        barWidth = getResources().getDimensionPixelSize(R.dimen.dp_12);
        imgHotelGallery.setOffscreenPageLimit(5);
        findViewById(R.id.btnClose).setOnClickListener(this);
        findViewById(R.id.tabRoomType).setOnClickListener(this);
        findViewById(R.id.tabHotelInfo).setOnClickListener(this);
        findViewById(R.id.tabNotice).setOnClickListener(this);

        findViewById(R.id.tabRoomTypeFloating).setOnClickListener(this);
        findViewById(R.id.tabHotelInfoFloating).setOnClickListener(this);
        findViewById(R.id.tabNoticeFloating).setOnClickListener(this);

        findViewById(R.id.btnCall).setOnClickListener(this);
        findViewById(R.id.btnViewMap).setOnClickListener(this);
        findViewById(R.id.btnViewRating).setOnClickListener(this);
        tvNumStamp = findViewById(R.id.tvNumStamp);
        iconStamp = findViewById(R.id.iconStamp);
        iconStamp.setOnClickListener(this);

        btnBook.setOnClickListener(this);
        findViewById(R.id.btnShare).setOnClickListener(this);
        lineTabs[0] = findViewById(R.id.tvTab1);
        lineTabs[1] = findViewById(R.id.tvTab2);
        lineTabs[2] = findViewById(R.id.tvTab3);

        lineTabsFloating[0] = findViewById(R.id.tvTab1Floating);
        lineTabsFloating[1] = findViewById(R.id.tvTab2Floating);
        lineTabsFloating[2] = findViewById(R.id.tvTab3Floating);

        lineTabs[0].setTextColor(getResources().getColor(R.color.wh));
        lineTabsFloating[0].setTextColor(getResources().getColor(R.color.wh));

        lvNotice = findViewById(R.id.lvNotice);
        lvRoom = findViewById(R.id.lvRoom);
        webContentInfo = findViewById(R.id.webContentInfo);

        /*
        * onClick
        */

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavorite();
            }
        });


        lvNotice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticeForm noticeForm = noticeForms.get(position);
                Intent intent = new Intent(HotelDetailActivity.this, NoticeDetailActivity.class);
                intent.putExtra("NoticeForm", noticeForm);
                startActivity(intent);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });

        imgHotelGallery.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                try {
                    MyLog.writeLog("PageSelect--HotelGallery");
                    if (imgHotelGallery.getAdapter() != null && imgHotelGallery.getAdapter().getCount() > 1) {
                        if (pageScrollState == ViewPager.SCROLL_STATE_SETTLING) {
                            TranslateAnimation animation = null;
                            if (currentPos < position) {
                                animation = new TranslateAnimation(0, 1, 0, 0);
                            } else if (currentPos > position) {
                                animation = new TranslateAnimation(0, -1, 0, 0);
                            }
                            if (animation != null) {
                                animation.setDuration(200);
                                animation.setFillAfter(false);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        RelativeLayout.LayoutParams layout_description = (RelativeLayout.LayoutParams) imgBar.getLayoutParams();
                                        layout_description.leftMargin = barWidth * position;
                                        imgBar.setLayoutParams(layout_description);
                                        currentPos = position;
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                imgBar.startAnimation(animation);
                            }
                        }

                    }
                } catch (Exception e) {
                    MyLog.writeLog("addOnPageChangeListener" + e);
                }

                updateRoomTypeInfo(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                pageScrollState = state;
            }
        });

        currentView = lvRoom;
        initData();

        imgNoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotelDetailActivity.this, HotelPhotoDetailActivity.class);
                intent.putExtra("HotelDetailForm", hotelDetailForm);
                intent.putExtra("SelectedRoomType", 0);
                startActivity(intent);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });

    }

    int pageScrollState = 0;
    int currentPos = 0;

    private void addFavorite() {
        if (PreferenceUtils.getToken(HotelDetailActivity.this).equals("")) {
            Intent intent = new Intent(HotelDetailActivity.this, LoginActivity.class);
            (HotelDetailActivity.this).startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);
        } else {
            UserFavoriteDto userFavoriteDto = new UserFavoriteDto();
            userFavoriteDto.setHotelSn(hotelDetailForm.getSn());
            userFavoriteDto.setFavorite(hotelDetailForm.getFavorite() == 0); //1==0=>false; 0==0=>true
            DialogUtils.showLoadingProgress(HotelDetailActivity.this, false);
            HotelApplication.serviceApi.updateFavoriteHotelForUser(userFavoriteDto, PreferenceUtils.getToken(HotelDetailActivity.this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                @Override
                public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                    DialogUtils.hideLoadingProgress();

                    RestResult restResult = response.body();
                    if (restResult != null) {
                        if (response.isSuccessful()) {
                            if (restResult.getResult() == 1) {
                                if (hotelDetailForm.getFavorite() == 0) {
                                    hotelDetailForm.setFavorite(1);
                                    hotelDetailForm.setTotalFavorite(hotelDetailForm.getTotalFavorite() + 1);
                                } else {
                                    hotelDetailForm.setFavorite(0);
                                    hotelDetailForm.setTotalFavorite(hotelDetailForm.getTotalFavorite() - 1);
                                }

                                tvCountLike.setText(String.valueOf(hotelDetailForm.getTotalFavorite()));

                                if (!PreferenceUtils.getToken(HotelDetailActivity.this).equals("")) {
                                    if (hotelDetailForm.getFavorite() == 1) {
                                        imgLike.setImageResource(R.drawable.like_on);
                                        tvCountLike.setTextColor(getResources().getColor(R.color.org));
                                    } else {
                                        imgLike.setImageResource(R.drawable.like);
                                        tvCountLike.setTextColor(getResources().getColor(R.color.wh));
                                    }
                                }
                            } else if (response.code() == 401) {
                                DialogUtils.showExpiredDialog(HotelDetailActivity.this, new DialogCallback() {
                                    @Override
                                    public void finished() {
                                        Intent intent = new Intent(HotelDetailActivity.this, LoginActivity.class);
                                        startActivityForResult(intent, LOGIN_DETAIL_REQUEST_LIKE);
                                    }
                                });
                            }
                        } else if (response.code() == 401) {
                            DialogUtils.showExpiredDialog(HotelDetailActivity.this, new DialogCallback() {
                                @Override
                                public void finished() {
                                    Intent intent = new Intent(HotelDetailActivity.this, LoginActivity.class);
                                    startActivityForResult(intent, LOGIN_DETAIL_REQUEST_LIKE);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestResult> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                }
            });
        }

    }

    private void initData() {
        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("hotelSn", sn);
        params.put("promotionSn", Utils.getPromotionSn(sn));

        HotelApplication.serviceApi.getHotelDetail(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<HotelDetailForm>() {
            @Override
            public void onResponse(Call<HotelDetailForm> call, Response<HotelDetailForm> response) {
                MyLog.writeLog("HotelDetailActivity-------->getHotelDetail-------> response ok");
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    hotelDetailForm = response.body();
                    /*
                    * update View
                    */
                    setupDataToView();

                    if (isNotification) {
                        Intent intent = new Intent(HotelDetailActivity.this, NoticeDetailActivity.class);
                        intent.putExtra("NOTIFICATON_SEND", isNotification);
                        intent.putExtra("NotificationData", notificationData);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                    }

                } else if (response.code() == 401) {
                    DialogUtils.showExpiredDialog(HotelDetailActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(HotelDetailActivity.this, LoginActivity.class);
                            startActivityForResult(intent, LOGIN_DETAIL_REQUEST_LIKE);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<HotelDetailForm> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(HotelDetailActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    * update View
    */

    private void setupDataToView() {

        // bufferRoomTypeImage();
        if (hotelDetailForm != null) {

            //Set Stamp
            if (hotelDetailForm.getNumToRedeem() > 0) {
                iconStamp.setVisibility(View.VISIBLE);
                tvNumStamp.setText(hotelDetailForm.getActiveStamp() + "/" + hotelDetailForm.getNumToRedeem());
                //Create Tooltip
                createViewTooltip(iconStamp);
            } else {
                iconStamp.setVisibility(View.GONE);
            }

            if (!PreferenceUtils.getToken(this).equals("")) {
                if (hotelDetailForm.getFavorite() == 1) {
                    imgLike.setImageResource(R.drawable.like_on);
                    tvCountLike.setTextColor(getResources().getColor(R.color.org));
                } else {
                    imgLike.setImageResource(R.drawable.like);
                    tvCountLike.setTextColor(getResources().getColor(R.color.wh));
                }
            }

            /*
            / Set Image 360
            */
            if (hotelDetailForm.getCountExifImage() > 0) {
                icon360.setVisibility(View.VISIBLE);
            }

            /*
            /
            */
            if (hotelDetailForm.getHotelImageList() != null && hotelDetailForm.getHotelImageList().size() > 0) {
                adapter = new HotelImageDetailAdapter(this, hotelDetailForm.getHotelImageList(), hotelDetailForm);
                imgHotelGallery.setAdapter(adapter);
            } else {
                boxBar.setVisibility(View.GONE);
                imgHotelGallery.setVisibility(View.GONE);
                imgNoImage.setVisibility(View.VISIBLE);
                /*
                / Set ImageView
                */
                String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImage?hotelImageSn=" + 0 + "&fileName=" + "default_image";
                //String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImageViaKey?imageKey=" + ;

                PictureGlide.getInstance().show(
                        url,
                        getResources().getDimensionPixelSize(R.dimen.hotel_item_contract_width),
                        getResources().getDimensionPixelSize(R.dimen.hotel_detail_image_height),
                        R.drawable.loading_big,
                        imgNoImage
                );
            }
            try {
                if (imgHotelGallery.getAdapter() != null && imgHotelGallery.getAdapter().getCount() > 1) {
                    boxBar.setVisibility(View.VISIBLE);
                    int screenWidth = getResources().getDisplayMetrics().widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.dp_12);
                    barWidth = screenWidth / imgHotelGallery.getAdapter().getCount();
                    imgBar.getLayoutParams().width = barWidth + 10;
                    imgBar.requestLayout();
                } else {
                    boxBar.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                MyLog.writeLog("imgHotelGallery" + e);
            }


            imgNew.setVisibility(View.GONE);
            imgHot.setVisibility(View.GONE);
            imgPromotion.setVisibility(View.GONE);

            if (hotelDetailForm.getNewHotel() == 1) {
                imgNew.setVisibility(View.VISIBLE);
            }
            if (hotelDetailForm.getHasPromotion() == 1) {
                imgPromotion.setVisibility(View.VISIBLE);
            }
            if (hotelDetailForm.getHotHotel() == 1) {
                imgHot.setVisibility(View.VISIBLE);
            }

            tvHotelTitle.setText(hotelDetailForm.getName());
            tvCountLike.setText(String.valueOf(hotelDetailForm.getTotalFavorite()));
            tvAddress.setText(hotelDetailForm.getAddress());
            if (hotelDetailForm.getAreaCode() != null) {
                tvPhoneNo.setText(hotelDetailForm.getAreaCode() + hotelDetailForm.getPhone());
            } else {
                tvPhoneNo.setText(hotelDetailForm.getPhone());
            }
            tvTotalReview.setText(String.valueOf(hotelDetailForm.getTotalReview()));

            String hotelInfoCheckin = setupHotelInfo();
            if ((hotelDetailForm.getDescription() == null || hotelDetailForm.getDescription().equals("")) && hotelInfoCheckin.equals("")) {
                tvCommonRule.setVisibility(View.GONE);
                lineCommon.setVisibility(View.GONE);
                webContentInfo.setVisibility(View.GONE);
            } else {
                tvCommonRule.setVisibility(View.VISIBLE);
                lineCommon.setVisibility(View.VISIBLE);
                webContentInfo.loadData(hotelInfoCheckin + hotelDetailForm.getDescription(), "text/html; charset=utf-8", "utf-8");
            }

            Location location = new Location("gps");
            Location newCurrLocation = new Location("gps");
            location.setLatitude(hotelDetailForm.getLatitude());
            location.setLongitude(hotelDetailForm.getLongitude());

            if (PreferenceUtils.getLatLocation(this).equals("")) {
                newCurrLocation.setLongitude(Double.parseDouble(getString(R.string.longitude_default)));
                newCurrLocation.setLatitude(Double.parseDouble(getString(R.string.latitude_default)));

            } else {
                newCurrLocation.setLatitude(Double.parseDouble(PreferenceUtils.getLatLocation(this)));
                newCurrLocation.setLongitude(Double.parseDouble(PreferenceUtils.getLongLocation(this)));
            }
            float distance = location.distanceTo(newCurrLocation);
            tvDistance.setText(Utils.meterToKm(distance));

            /*
            * Set hotel contact and trial
            */

            if (hotelDetailForm.getHotelStatus() == ContractType.CONTRACT.getType() || hotelDetailForm.getHotelStatus() == ContractType.TRIAL.getType()) {
                MyLog.writeLog("HotelDetailActivity-------->setRoomTypeListAdapter");

                //Get Promotion Info Form
                if (HotelApplication.mapPromotionInfoForm == null || HotelApplication.mapPromotionInfoForm.size() == 0) {
                    ControllerApi.getmInstance().findPromotionInformation(this, new CallbackPromotionInfoForm() {
                        @Override
                        public void map(Map<String, PromotionInfoForm> map) {

                            HotelApplication.mapPromotionInfoForm = map;

                            //Set Room Type
                            roomTypeListAdapter = new RoomTypeListAdapter(HotelDetailActivity.this, hotelDetailForm);
                            lvRoom.setAdapter(roomTypeListAdapter);
                            Utils.setListViewHeightBasedOnChildren(lvRoom);

                        }
                    });
                } else {

                    //Set Room Type
                    roomTypeListAdapter = new RoomTypeListAdapter(this, hotelDetailForm);
                    lvRoom.setAdapter(roomTypeListAdapter);
                    Utils.setListViewHeightBasedOnChildren(lvRoom);

                }

            } else {
                tvMessage.setText(getString(R.string.this_hotel_is_not_contract_with_appro));
                tvMessage.setVisibility(View.VISIBLE);
            }

            /*
            * Set Facilities
            */

            setupFacility();

            /*
            * Set star
            */

            int numStar = (int) hotelDetailForm.getAverageMark();
            MyLog.writeLog("numStar: " + numStar);
            if (numStar > 5) {
                numStar = 5;
            }
            if (numStar < 0) {
                numStar = 0;
            }

            for (ImageView star : stars) {
                star.setImageResource(R.drawable.review_star);
            }

            if (0 < hotelDetailForm.getAverageMark() && hotelDetailForm.getAverageMark() <= 5) {
                for (int i = 0; i < numStar; i++) {
                    stars[i].setImageResource(R.drawable.review_star_fill);
                }
                if (numStar < stars.length) {
                    if (numStar != hotelDetailForm.getAverageMark()) {
                        stars[numStar].setImageResource(R.drawable.review_star_half);
                    }
                }
            }
        }
        MyLog.writeLog("HotelDetailActivity-------->getHotelDetail-----> update view finish");


        /*
        * Scroll to Home
        */
        mainScroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyLog.writeLog("----->Move to Home<-----");
                mainScroll.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 100);

    }

    /*
    * set facilities
    */

    private void setupFacility() {
        if (hotelDetailForm.getFacilityFormList() != null) {
            MyLog.writeLog("AddFacilitySize: " + hotelDetailForm.getFacilityFormList().size());

            if (hotelDetailForm.getFacilityFormList().size() == 0) {

                container.setVisibility(View.GONE);

            } else {

                container.setVisibility(View.VISIBLE);
                // set chiều ngang cho item in facilities list = 1/4 màn hình
                int width = (int) Utils.getScreenWidthPixel();
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(width / 4, ViewGroup.LayoutParams.WRAP_CONTENT, 0);

                for (int i = 0; i < hotelDetailForm.getFacilityFormList().size(); i++) {
                    View imageLayout = getLayoutInflater().inflate(R.layout.facility_item, null);
                    ImageView imgVIew = imageLayout.findViewById(R.id.imgItem);
                    TextView txtView = imageLayout.findViewById(R.id.tvName);

                    FacilityForm facilityForm = hotelDetailForm.getFacilityFormList().get(i);


                    String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadFacilityImage?facilitySn=" + facilityForm.getSn() + "&fileName=" + facilityForm.getCustomizePath();
                    //String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImageViaKey?imageKey="

                    PicturePicasso.getInstance().show(imgVIew, url);

                    txtView.setText(facilityForm.getName());

                    container.addView(imageLayout, itemParams);
                }


            }

        } else {
            MyLog.writeLog("HotelDetailActivity-------->facilities null");
        }
    }

    private String setupHotelInfo() {
        if (hotelDetailForm != null) {
            String dailyCheckin = getString(R.string.txt_3_2_checkin_time) + " " + hotelDetailForm.getCheckin() + "h.";
            dailyCheckin = changeStringToHTML(dailyCheckin);
            String dailyCheckout = getString(R.string.txt_3_2_checkout_time) + " " + hotelDetailForm.getCheckout() + "h.";
            dailyCheckout = changeStringToHTML(dailyCheckout);
            String overnight = getString(R.string.txt_3_2_overnight_time) + " " + hotelDetailForm.getStartOvernight() + "h ~ " + hotelDetailForm.getEndOvernight() + "h.";
            overnight = changeStringToHTML(overnight);
            String cancelPolicy = String.format(getString(R.string.txt_3_2_cancel_policy), String.valueOf(hotelDetailForm.getCancelBeforeHours()));
            cancelPolicy = changeStringToHTML(cancelPolicy);
            return dailyCheckin + dailyCheckout + overnight + cancelPolicy;
        }
        return "";
    }


    private String changeStringToHTML(String content) {
        return "<p><span style=\"font-family:SFUIDisplay-Regular;font-size:15px\">" + content + "</span></p>";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBook:
                gotoBooking();
                break;
            case R.id.tvTab1Floating:
            case R.id.tabRoomTypeFloating:
            case R.id.tvTab1:
            case R.id.tabRoomType:
                setButtonName("BHotelRoom");
                setTab(0);
                break;
            case R.id.tvTab2Floating:
            case R.id.tabHotelInfoFloating:
            case R.id.tvTab2:
            case R.id.tabHotelInfo:
                setButtonName("BHotelInfo");
                setTab(1);
                break;
            case R.id.tvTab3Floating:
            case R.id.tabNoticeFloating:
            case R.id.tvTab3:
            case R.id.tabNotice:
                setButtonName("BHotelNotice");
                setTab(2);
                break;
            case R.id.btnClose:
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;
            case R.id.btnShare:
                setButtonName("BHotelShare");
                openShareDialog();
                break;
            case R.id.tvFacebook:
                postShare(ShareType.FACEBOOK);
                break;
            case R.id.tvZalo:
                postShare(ShareType.ZALO);
                break;
            case R.id.tvViber:
                postShare(ShareType.VIBER);
                break;
            case R.id.tvSkype:
                postShare(ShareType.SKYPE);
                break;
            case R.id.tvSMS:
                postShare(ShareType.SMS);
                break;
            case R.id.btnViewRating:
                openViewRating();
                break;
            case R.id.btnCall:
                setButtonName("BHotelCall");
                final int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion < 23) {
                    openPhoneCall();
                } else {
                    checkPermissionCall();
                }
                break;
            case R.id.btnViewMap:
                openMap();
                break;
            case R.id.detail_hotel_icon_360:
                gotoPanoramaActivity();
                break;
            case R.id.iconStamp:
                if (!PreferenceUtils.getToken(this).equals("")) {
                    if (hotelDetailForm != null) {
                        findUserStampFormDetail(hotelDetailForm.getSn());
                    }
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent, ParamConstants.LOGIN_DETAIL_REQUEST_LIKE);
                }
                break;
        }
    }

    private void gotoPanoramaActivity() {
        Intent intent = new Intent(this, Panorama.class);
        intent.setAction("HotelDetailActivity");
        intent.putExtra("HotelDetailForm", hotelDetailForm);
        intent.putExtra("SelectedRoomType", -1);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void gotoBooking() {
        if (!PreferenceUtils.getToken(this).equals("")) {
            gotoReservation();
        } else {
            showDialogGuestBooking();
        }
    }

    private void showDialogGuestBooking() {
        Dialag.getInstance().show(this, false, true, false, null, getString(R.string.msg_3_9_book_as_guest), getString(R.string.login_button), getString(R.string.txt_3_9_book_as_guest), null, Dialag.BTN_MIDDLE, new CallbackDialag() {
            @Override
            public void button1() { //goto LogIn
                Intent intent = new Intent(HotelDetailActivity.this, LoginActivity.class);
                startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOTEL_DETAIL);
            }

            @Override
            public void button2() { //Continues
                gotoReservation();
            }

            @Override
            public void button3(Dialog dialog) {

            }
        });
    }

    private void gotoReservation() {
        Intent intent = new Intent(this, ReservationActivity.class);

        intent.putExtra("HotelDetailForm", hotelDetailForm);
        intent.putExtra("RoomTypeIndex", getRoomTypeIndexAvailable());
        startActivityForResult(intent, CALL_BOOKING);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    //Check room type available
    private int getRoomTypeIndexAvailable() {
        int result = 0;
        List<RoomTypeForm> roomTypeList = hotelDetailForm.getRoomTypeList();
        for (int i = 0; i < roomTypeList.size(); i++) {
            if (!roomTypeList.get(i).isFlashSale()) {
                break;
            }
            if (!roomTypeList.get(i).isFlashSaleRoomAvailable()) {
                result++;
            } else {
                break;
            }
        }
        return result;
    }


    private void openMap() {
        Intent intent = new Intent(this, MapViewDetailActivity.class);
        intent.putExtra("HotelDetailForm", hotelDetailForm);
        intent.putExtra("RoomAvailable", roomAvailable);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void openViewRating() {
        Intent intent = new Intent(this, RateReviewListActivity.class);
        intent.putExtra("HotelDetailForm", hotelDetailForm);
        startActivityForResult(intent, OPEN_REVIEW);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }


    final private int REQUEST_CODE_ASK_CALL_PHONE = 124;

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionCall() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();

        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("Phone Call");

        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_CALL_PHONE);
        } else {
            //Continue
            openPhoneCall();
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
            case REQUEST_CODE_ASK_CALL_PHONE: {
                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    //Continue
                    openPhoneCall();

                } else {
                    // Permission Denied
                    finish();
                }
            }
            break;
        }
    }


    private void openPhoneCall() {

        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_confirm_dialog);
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
        btnNo.setText(getString(R.string.cancel));
        btnYes.setText(getString(R.string.ok));
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        String phoneNumber = hotelDetailForm.getPhone();
        if (hotelDetailForm.getAreaCode() != null) {
            phoneNumber = hotelDetailForm.getAreaCode() + phoneNumber;
        }
        String confirmCall = getString(R.string.call_hotel) + "\n" + hotelDetailForm.getName() + "\n\n" + phoneNumber;
        tvMessage.setText(confirmCall);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final String finalPhoneNumber = phoneNumber;
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + finalPhoneNumber;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                if (ActivityCompat.checkSelfPermission(HotelDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    private void postShare(final ShareType shareType) {
        dialog.dismiss();

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.findShareHotelLink(HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();

                RestResult restResult = response.body();
                if (restResult != null) {
                    if (response.isSuccessful()) {
                        switch (shareType) {
                            case VIBER:
                                ShareUtils.shareViber(HotelDetailActivity.this, restResult.getOtherInfo() + hotelDetailForm.getSn());
                                break;
                            case FACEBOOK:
                                ShareUtils.shareFacebook(HotelDetailActivity.this, restResult.getOtherInfo() + hotelDetailForm.getSn());
                                break;
                            case ZALO:
                                ShareUtils.shareZalo(HotelDetailActivity.this, restResult.getOtherInfo() + hotelDetailForm.getSn());
                                break;
                            case SKYPE:
                                ShareUtils.shareSkype(HotelDetailActivity.this, restResult.getOtherInfo() + hotelDetailForm.getSn());
                                break;
                            case SMS:
                                ShareUtils.shareSMS(HotelDetailActivity.this, restResult.getOtherInfo() + hotelDetailForm.getSn());
                                break;
                        }
                    } else {
                        Toast.makeText(HotelDetailActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(HotelDetailActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });


    }

    private Dialog dialog;

    private void openShareDialog() {
        dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }

        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tvFacebook).setOnClickListener(this);
        dialog.findViewById(R.id.tvZalo).setOnClickListener(this);
        dialog.findViewById(R.id.tvViber).setOnClickListener(this);
        dialog.findViewById(R.id.tvSkype).setOnClickListener(this);
        dialog.findViewById(R.id.tvSMS).setOnClickListener(this);
    }


    /*
    * Set tab List Room type
    */
    private void setTab(final int tab) {
        if (hotelDetailForm != null) {

            /*
            * Show button Reservation
            */
            if (hotelDetailForm.getHotelStatus() == ContractType.CONTRACT.getType() || hotelDetailForm.getHotelStatus() == ContractType.TRIAL.getType()) {
                if (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {
                    btnBook.setVisibility(View.VISIBLE);
                }
            }

        }
        lineTabsFloating[0].setTextColor(getResources().getColor(R.color.bk));
        lineTabsFloating[0].setBackgroundColor(getResources().getColor(R.color.transparent));
        lineTabsFloating[1].setTextColor(getResources().getColor(R.color.bk));
        lineTabsFloating[1].setBackgroundColor(getResources().getColor(R.color.transparent));
        lineTabsFloating[2].setTextColor(getResources().getColor(R.color.bk));
        lineTabsFloating[2].setBackgroundColor(getResources().getColor(R.color.transparent));
        lineTabsFloating[tab].setTextColor(getResources().getColor(R.color.wh));
        lineTabsFloating[tab].setBackgroundColor(getResources().getColor(R.color.org));

        lineTabs[0].setTextColor(getResources().getColor(R.color.bk));
        lineTabs[0].setBackgroundColor(getResources().getColor(R.color.transparent));
        lineTabs[1].setTextColor(getResources().getColor(R.color.bk));
        lineTabs[1].setBackgroundColor(getResources().getColor(R.color.transparent));
        lineTabs[2].setTextColor(getResources().getColor(R.color.bk));
        lineTabs[2].setBackgroundColor(getResources().getColor(R.color.transparent));
        lineTabs[tab].setTextColor(getResources().getColor(R.color.wh));
        lineTabs[tab].setBackgroundColor(getResources().getColor(R.color.org));
        tvMessage.setVisibility(View.INVISIBLE);
        switch (tab) {

            case 0:
                MyLog.writeLog("tab-0-");
                //Hide btn Reservstion
                btnBook.setVisibility(View.GONE);
                //List Room Type
                currentView.setVisibility(View.GONE);
                //List
                currentView = lvRoom;

                if (hotelDetailForm != null) {
                    if (hotelDetailForm.getHotelStatus() == ContractType.CONTRACT.getType() || hotelDetailForm.getHotelStatus() == ContractType.TRIAL.getType()) {
                        currentView.setVisibility(View.VISIBLE);

                    } else {
                        tvMessage.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case 1:
                MyLog.writeLog("tab-1-");
                if (hotelDetailForm != null && hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {
                    if (hotelDetailForm.getHotelStatus() == ContractType.CONTRACT.getType() || hotelDetailForm.getHotelStatus() == ContractType.TRIAL.getType()) {
                        btnBook.setVisibility(View.VISIBLE);
                    }
                } else {
                    //Null List Room Type
                    btnBook.setVisibility(View.GONE);
                }
                //Set View list
                boxHotelInfo.setVisibility(View.VISIBLE);
                currentView.setVisibility(View.GONE);
                currentView = boxHotelInfo;
                currentView.setVisibility(View.VISIBLE);
                break;
            case 2:
                MyLog.writeLog("tab-2-");
                if (hotelDetailForm != null && hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {
                    if (hotelDetailForm.getHotelStatus() == ContractType.CONTRACT.getType() || hotelDetailForm.getHotelStatus() == ContractType.TRIAL.getType()) {
                        btnBook.setVisibility(View.VISIBLE);
                    }
                } else {
                    //Null List Room Type
                    btnBook.setVisibility(View.GONE);
                }

                lvNotice.setVisibility(View.VISIBLE);
                currentView.setVisibility(View.GONE);
                currentView = lvNotice;
                currentView.setVisibility(View.VISIBLE);
                break;
        }


        /*
        * Scroll to Bottom
        */
        mainScroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);

        if (tab == 2 && noticeForms == null) {
            /*
            * Get Notice List
            */
            Map<String, Object> params = new HashMap<>();
            params.put("hotelSn", sn);
            params.put("offset", 0);
            params.put("limit", 100);
            MyLog.writeLog("hotelSn: " + sn);
            HotelApplication.serviceApi.getNoticeList(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<NoticeForm>>() {
                @Override
                public void onResponse(Call<List<NoticeForm>> call, Response<List<NoticeForm>> response) {
                    if (response.isSuccessful()) {
                        noticeForms = response.body();
                        if (noticeForms != null) {
                            lvNotice.setAdapter(new NoticeAdapter(HotelDetailActivity.this, noticeForms));
                            Utils.setListViewHeightBasedOnChildren(lvNotice);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<NoticeForm>> call, Throwable t) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ParamConstants.LOGIN_DETAIL_REQUEST_LIKE) {
            if (resultCode == RESULT_OK) {
                initData();
            }
        } else if (requestCode == OPEN_REVIEW) {
            if (resultCode == RESULT_OK) {
                initData();
            }
        } else if (requestCode == CALL_BOOKING) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    private void updateRoomTypeInfo(int position) {
        tvRoomTypeName.setVisibility(View.GONE);
        if (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {
            HotelImageForm hotelImageForm = hotelDetailForm.getHotelImageList().get(position);
            int count = 0;
            for (int i = 0; i < hotelDetailForm.getRoomTypeList().size(); i++) {
                RoomTypeForm roomTypeForm = hotelDetailForm.getRoomTypeList().get(i);
                if (hotelImageForm.getRoomTypeSn() == roomTypeForm.getSn()) {
                    tvRoomTypeName.setVisibility(View.VISIBLE);
                    tvRoomTypeName.setText(roomTypeForm.getName());
                    roomTypeIndex = i;
                    break;
                }
                count++;
            }
            if (count >= hotelDetailForm.getRoomTypeList().size()) {
                roomTypeIndex = 0;
            }
        }
    }

    View currentView;

    int roomTypeIndex = 0;

    @Override
    public void setScreenName() {
        screenName = "SHotel";
    }

    @Override
    protected void onStart() {
        super.onStart();
        PictureGlide.getInstance().clearCache(this);
    }

    private void createViewTooltip(RelativeLayout iconStamp) {
        final Tooltip tooltip = new Tooltip.Builder(iconStamp, R.style.Tooltip)
                .setDismissOnClick(true)
                .setCornerRadius(20f)
                .setGravity(Gravity.BOTTOM)
                .setText(getString(R.string.msg_6_12_tip_stamp))
                .show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tooltip.dismiss();
            }
        }, 4000);
    }

    private void findUserStampFormDetail(long hotelSn) {
        if (userStampForm == null) {
            ControllerApi.getmInstance().findUserStampFormDetail(this, hotelSn, true, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    userStampForm = (UserStampForm) object;
                    DialogStamp.getInstance().show(HotelDetailActivity.this, userStampForm);
                }
            });
        } else {
            DialogStamp.getInstance().show(this, userStampForm);
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (hotelDetailActivity != null)
//            hotelDetailActivity = null;
//    }
}
