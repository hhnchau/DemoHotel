package com.appromobile.hotel.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.model.view.AppNoticeForm;
import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 9/5/2016.
 */
public class NoticeAppDetailActivity extends BaseActivity {
    TextViewSFRegular tvTitle, tvDate;
    WebView webContent;
    AppNoticeForm appNoticeForm;
    boolean isNoficationSend = false;
    NotificationData notificationData;
    private Button btnApply;
    private int promotionSn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("NoticeAppDetailActivity" + e);
        }

        /*
        / Clear Counter
        */
        PreferenceUtils.setCounterNotifi(this, 0);

        /*
        / Set Shortcut Badger
        */
        ShortcutBadger.applyCount(this, 0); //Delete = 0

        setContentView(R.layout.notice_app_detail_activity);
        tvTitle = (TextViewSFRegular) findViewById(R.id.tvTitle);
        tvDate = (TextViewSFRegular) findViewById(R.id.tvDate);
        webContent = (WebView) findViewById(R.id.webContent);
        appNoticeForm = getIntent().getParcelableExtra("AppNoticeForm");
        btnApply = (Button) findViewById(R.id.btnApply);

        isNoficationSend = getIntent().getBooleanExtra("NOTIFICATON_SEND", false);
        if (isNoficationSend) {
            notificationData = getIntent().getExtras().getParcelable("NotificationData");

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
                checkAndApplyCoupon(promotionSn);
            }
        });

    }

    private void checkAndApplyCoupon(int sn) {
        // check login or not
        if (PreferenceUtils.getToken(this).equals("")) {
            Intent intent = new Intent(NoticeAppDetailActivity.this, LoginActivity.class);
            startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_TO_APPLY_PROMOTION);
            return;
        }
        applyCoupon(sn);
    }

    private void applyCoupon(int sn) {
        ControllerApi.getmInstance().applyPromotionEvent(NoticeAppDetailActivity.this, sn, new ResultApi() {
            @Override
            public void resultApi(Object object) {

                if (object != null) {
                    gotoMyCoupon();
                }
            }
        });
    }

    private void gotoMyCoupon() {
        Intent intent = new Intent(NoticeAppDetailActivity.this, MyCouponActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void setupDataView() {
        if (appNoticeForm != null) {
            tvTitle.setText(appNoticeForm.getTitle());
            if (appNoticeForm.getPromotionSn() > 0 && !appNoticeForm.isAppliedPromotion()) {
                btnApply.setVisibility(View.VISIBLE);
                promotionSn = appNoticeForm.getPromotionSn();
            }
            SimpleDateFormat apiFormat = new SimpleDateFormat(getString(R.string.date_format_request));
            SimpleDateFormat viewFormat = new SimpleDateFormat(getString(R.string.date_format_view));
            try {
                Date date = apiFormat.parse(appNoticeForm.getLastUpdate());
                tvDate.setText(getString(R.string.date) + ": " + viewFormat.format(date));
            } catch (Exception e) {
                MyLog.writeLog("setupDataView" + e);
            }
            String webData = appNoticeForm.getContent();
            // 48 is the padding of layout
            webData = Utils.handlePictureRatio(webData, 48);
            webContent.getSettings().setJavaScriptEnabled(true);
            webContent.loadDataWithBaseURL("", webData, "text/html", "UTF-8", "");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNoficationSend) {
            setupDataView();
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("appNoticeSn", notificationData.getSn());
            DialogUtils.showLoadingProgress(this, false);
            HotelApplication.serviceApi.findAppNotice(params, HotelApplication.DEVICE_ID).enqueue(new Callback<AppNoticeForm>() {
                @Override
                public void onResponse(Call<AppNoticeForm> call, Response<AppNoticeForm> response) {
                    DialogUtils.hideLoadingProgress();
                    if (response.isSuccessful()) {
                        appNoticeForm = response.body();
                        setupDataView();
                    } else {
                        Intent intent = new Intent(NoticeAppDetailActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<AppNoticeForm> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ParamConstants.REQUEST_LOGIN_TO_APPLY_PROMOTION) {
            if (resultCode == RESULT_OK) {
                checkAndApplyCoupon(promotionSn);
            }
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetNoticeDetailApp";
    }
}
