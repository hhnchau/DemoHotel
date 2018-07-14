package com.appromobile.hotel.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.NotificationType;
import com.appromobile.hotel.model.view.NoticeForm;
import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;

import java.util.Map;


import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 8/1/2016.
 */
public class NoticeDetailActivity extends BaseActivity {
    private NoticeForm noticeForm;
    TextViewSFRegular tvTitle, tvDate;
    WebView webContent;
    boolean isNoficationSend = false;
    NotificationData notificationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("NoticeDetailActivity" + e);
        }

        setContentView(R.layout.notice_detail_activity);

        tvTitle =  findViewById(R.id.tvTitle);
        tvDate =  findViewById(R.id.tvDate);
        webContent =  findViewById(R.id.webContent);

        //Get intent
        isNoficationSend = getIntent().getExtras().getBoolean("NOTIFICATON_SEND", false);

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        //goto from Notify
        if (!isNoficationSend) {
            setupDataView();
        } else {
            Map<String, Object> params = new HashMap<>();

            params.put("noticeSn", notificationData.getSn());

            HotelApplication.serviceApi.findNotice(params, HotelApplication.DEVICE_ID).enqueue(new Callback<NoticeForm>() {
                @Override
                public void onResponse(Call<NoticeForm> call, Response<NoticeForm> response) {
                    if (response.isSuccessful()) {
                        noticeForm = response.body();
                        setupDataView();
                    }
                }

                @Override
                public void onFailure(Call<NoticeForm> call, Throwable t) {

                }
            });
        }
    }

    private void setupDataView() {

        //goto from Notify
        if (!isNoficationSend) {
            noticeForm = getIntent().getParcelableExtra("NoticeForm");
        }

        if (noticeForm != null) {
            tvTitle.setText(noticeForm.getTitle());

            SimpleDateFormat apiFormat = new SimpleDateFormat(getString(R.string.date_format_request));
            SimpleDateFormat viewFormat = new SimpleDateFormat(getString(R.string.date_format_view));
            try {
                Date date = apiFormat.parse(noticeForm.getCreateTime());
                tvDate.setText(viewFormat.format(date));
            } catch (Exception e) {
                MyLog.writeLog("setupDataView" + e);
            }
            String data = noticeForm.getContent();
            //Set View
            webContent.getSettings().setJavaScriptEnabled(true);
            webContent.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);

    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetNoticeDetail";
    }
}
