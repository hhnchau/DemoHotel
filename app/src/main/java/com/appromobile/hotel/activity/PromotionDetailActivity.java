package com.appromobile.hotel.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.DialagWebview;
import com.appromobile.hotel.model.view.PromotionForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.GlideApp;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFHeavy;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 9/14/2016.
 */
public class PromotionDetailActivity extends BaseActivity {
    private static final int LOGIN_APPLY_COUPON_REQUEST = 1000;
    private TextViewSFRegular tvTimePeriod, tvCouponPeriod;
    private TextViewSFHeavy tvScreenTitle;
    private WebView webViewContent;
    private PromotionForm promotionForm;
    private ImageView img;
    private TextViewSFBold btnApply, tvTitle;
    private int promotionSn = 0;
    private LinearLayout lnCoupon;


    private final int LOAD_TYPE_DISPLAY_REQUEST = 2;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("Fabric" + e);
        }

        setContentView(R.layout.promotion_detail_activity);

        img = findViewById(R.id.img);
        lnCoupon = findViewById(R.id.linnear_coupon);
        btnApply = findViewById(R.id.btnApply);
        tvTimePeriod = findViewById(R.id.tvTimePeriod);
        tvCouponPeriod = findViewById(R.id.tvCouponPeriod);
        tvScreenTitle = findViewById(R.id.tvScreenTitle);
        webViewContent = findViewById(R.id.wvContent);
        webViewContent.getSettings().setJavaScriptEnabled(true);
        tvTitle = findViewById(R.id.tvTitle);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            promotionSn = bundle.getInt("promotionSn");
        }

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreferenceUtils.getToken(PromotionDetailActivity.this).equals("")) {
                    setButtonName("BPromoApply1");
                    if (promotionForm != null) {
                        applyPromotion();
                    } else {
                        requestPromotionData();
                    }
                } else {
                    Intent intent = new Intent(PromotionDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void requestPromotionData() {

        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("sn", promotionSn);
        HotelApplication.serviceApi.findPromotionForApp(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<PromotionForm>() {
            @Override
            public void onResponse(Call<PromotionForm> call, Response<PromotionForm> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful() && response.body() != null) {
                    promotionForm = response.body();
                    initPromotion();
                } else if (response.code() == 401) {
                    DialogUtils.showExpiredDialog(PromotionDetailActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(PromotionDetailActivity.this, LoginActivity.class);
                            startActivityForResult(intent, LOGIN_APPLY_COUPON_REQUEST);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<PromotionForm> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(PromotionDetailActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initPromotion() {
        SimpleDateFormat formatApi = new SimpleDateFormat(getString(R.string.date_format_request));
        SimpleDateFormat formatView = new SimpleDateFormat(getString(R.string.date_format_view));
        try {
            Date start = formatApi.parse(promotionForm.getApplyStart());
            Date end = formatApi.parse(promotionForm.getApplyEnd());
            tvTimePeriod.setText(formatView.format(start) + " ~ " + formatView.format(end));


            if (promotionForm.getNumActiveDay() > 0) {
                tvCouponPeriod.setText(promotionForm.getNumActiveDay() + " " + getString(R.string.txt_5_2_birthday_issued));
            } else {
                if (promotionForm.getType() == PromotionForm.TYPE_EVENT) {
                    lnCoupon.setVisibility(View.GONE);
                } else {


                    try {
                        start = formatApi.parse(promotionForm.getCouponStart());
                        end = formatApi.parse(promotionForm.getCouponEnd());
                    } catch (ParseException e) {
                        tvCouponPeriod.setText(promotionForm.getCouponStart() + " ~ " + promotionForm.getCouponEnd());
                    }
                    tvCouponPeriod.setText(formatView.format(start) + " ~ " + formatView.format(end));

                }
            }

        } catch (Exception e) {
            MyLog.writeLog("initPromotion" + e);
        }


        // 18 is the padding of layout
        String promotionContent = Utils.handlePictureRatio(promotionForm.getContent(), 36);
        webViewContent.loadDataWithBaseURL("", promotionContent, "text/html", "UTF-8", "");
        tvTitle.setText(promotionForm.getTitle());
        /*
        / Check Button Apply
        */
        if (promotionForm.getType() == 1) { //Promotion

            tvScreenTitle.setText(getString(R.string.promotion));

            if (promotionForm.isApply()) {
                btnApply.setVisibility(View.VISIBLE);
                btnApply.setText(getString(R.string.applied));
                btnApply.setEnabled(false);
            } else {
                //Check Expired
                if (promotionForm.getStatus() == 1) {
                    //Active
                    btnApply.setVisibility(View.VISIBLE);
                    btnApply.setText(getString(R.string.apply));
                    btnApply.setEnabled(true);
                } else {
                    //Expired
                    btnApply.setVisibility(View.GONE);
                }
            }
        } else if (promotionForm.getType() == 2) { //Event

            tvScreenTitle.setText(getString(R.string.menu_event));

            if (promotionForm.isApply()) {
                btnApply.setVisibility(View.VISIBLE);
                btnApply.setText(getString(R.string.txt_2_joined));
                btnApply.setEnabled(false);
            } else {
                //Check Expired
                if (promotionForm.getStatus() == 1) {
                    //Active
                    btnApply.setVisibility(View.VISIBLE);
                    btnApply.setText(getString(R.string.txt_2_join));
                    btnApply.setEnabled(true);
                } else {
                    //Expired
                    btnApply.setVisibility(View.GONE);
                }
            }

        } else if (promotionForm.getType() > 2) { //Tat ca lon hon 2 deu an nut

            tvScreenTitle.setText(getString(R.string.promotion));
            btnApply.setVisibility(View.GONE);
        }

        try {

            int promotionImageSn = 0;

            try {
                if (promotionForm.getPromotionImageFormList() != null) {
                    for (int i = 0; i < promotionForm.getPromotionImageFormList().size(); i++) {
                        if (promotionForm.getPromotionImageFormList().get(i).getTypeDisplay() == LOAD_TYPE_DISPLAY_REQUEST) {
                            promotionImageSn = promotionForm.getPromotionImageFormList().get(i).getSn();
                        }
                    }
                }
            } catch (Exception e) {
                MyLog.writeLog("promotionImageSn" + e);
            }

            String url = UrlParams.MAIN_URL + "/hotelapi/promotion/download/downloadPromotionImage?promotionImageSn=" + promotionImageSn;
            GlideApp
                    .with(img.getContext())
                    .load(url)
                    .override(getResources().getDimensionPixelSize(R.dimen.hotel_item_contract_width), getResources().getDimensionPixelSize(R.dimen.promotion_item_height))
                    .into(img);

        } catch (Exception e) {
            MyLog.writeLog("promotionImageSn" + e);
        }
    }

    private void applyPromotion() {
        if (PreferenceUtils.getToken(this).equals("")) {
            Intent intent = new Intent(PromotionDetailActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_APPLY_COUPON_REQUEST);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("promotionSn", promotionForm.getSn());
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.applyPromotionEvent(map, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();

                RestResult restResult = response.body();
                if (response.isSuccessful() && restResult != null) {
                    if (restResult.getResult() == 1) {
                        //Check Event/Promotion
                        if (promotionForm.getType() == 2) {
                            //Event
                            DialagWebview.getInstance().show(PromotionDetailActivity.this, false, false, false, null, promotionForm.getMemo(), getString(R.string.ok), null, null, 1, new CallbackDialag() {
                                @Override
                                public void button1() {
                                    finish();
                                }

                                @Override
                                public void button2() {

                                }

                                @Override
                                public void button3(Dialog dialog) {

                                }
                            });

                        } else {
                            //Promotion
                            Toast.makeText(PromotionDetailActivity.this, getString(R.string.applied_sucessful), Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    } else {
                        Toast.makeText(PromotionDetailActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 401) {
                    DialogUtils.showExpiredDialog(PromotionDetailActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(PromotionDetailActivity.this, LoginActivity.class);
                            startActivityForResult(intent, LOGIN_APPLY_COUPON_REQUEST);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(PromotionDetailActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_APPLY_COUPON_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (promotionForm.isApply()) {
                    if (!PreferenceUtils.getToken(PromotionDetailActivity.this).equals("")) {
                        //logged
                        btnApply.setVisibility(View.VISIBLE);
                        btnApply.setText(getString(R.string.applied));
                        btnApply.setEnabled(false);
                    }
                } else {
                    applyPromotion();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPromotionData();
    }

    @Override
    public void setScreenName() {
        this.screenName = "SPromoDetail";
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable("promotionForm", promotionForm);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getParcelable("promotionForm") != null) {
            promotionForm = savedInstanceState.getParcelable("promotionForm");
        }
    }
}
