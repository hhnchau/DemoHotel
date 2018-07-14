package com.appromobile.hotel.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.QADetailAdapter;
import com.appromobile.hotel.model.request.CounselingDetailDto;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.CounselingDetailForm;
import com.appromobile.hotel.model.view.CounselingForm;
import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 9/6/2016.
 */
public class QADetailActivity extends BaseActivity {
    TextViewSFRegular tvTitle, tvDate, tvNickname;
    ListView lvQA;
    EditText txtContent;
    private int counselingSn;
    private List<CounselingDetailForm> counselingDetailForms;
    private AppUserForm appUserForm;
    boolean isNotification = false;
    NotificationData notificationData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        }catch (Exception e){
            MyLog.writeLog("QADetailActivity " + e);
        }

        setContentView(R.layout.qa_detail_activity);

        appUserForm = PreferenceUtils.getAppUser(this);
        lvQA =  findViewById(R.id.lvQA);
        txtContent =  findViewById(R.id.txtContent);
        tvTitle =  findViewById(R.id.tvTitle);
        tvDate =  findViewById(R.id.tvDate);
        tvNickname =  findViewById(R.id.tvNickname);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            isNotification = bundle.getBoolean("NOTIFICATON_SEND", false);
            if (isNotification) {
                notificationData = bundle.getParcelable("NotificationData");
            }

            if (!isNotification) {
                String title = bundle.getString("Title");
                String nickname = bundle.getString("Nickname");
                String strDate = bundle.getString("Date");
                counselingSn = bundle.getInt("counselingSn", 0);

                tvTitle.setText(title);
                tvNickname.setText(nickname);
                try {
                    SimpleDateFormat apiFormat = new SimpleDateFormat(getString(R.string.date_format_request));
                    SimpleDateFormat viewFormat = new SimpleDateFormat(getString(R.string.date_format_view));
                    Date date = apiFormat.parse(strDate);
                    tvDate.setText(viewFormat.format(date));

                } catch (Exception e) {
                    MyLog.writeLog("QADetailActivity " + e);
                }
            } else {
                if (notificationData != null) {
                    counselingSn = notificationData.getSn();
                }
            }
        }




        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(QADetailActivity.this, txtContent);
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                Utils.hideKeyboard(QADetailActivity.this, txtContent);
            }

        });

        initData();
    }

    private void sendMessage() {
        if(txtContent.getText().toString().equals("")){
            return;
        }else{
            CounselingDetailDto counselingDetailDto = new CounselingDetailDto();
            counselingDetailDto.setContent(txtContent.getText().toString());
            counselingDetailDto.setCounselingSn(counselingSn);
            DialogUtils.showLoadingProgress(this, false);
            HotelApplication.serviceApi.replyCounselingDetail(counselingDetailDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
                @Override
                public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                    DialogUtils.hideLoadingProgress();
                    if(response.isSuccessful()){
                        initData();
                        txtContent.setText("");
                    }
                }

                @Override
                public void onFailure(Call<RestResult> call, Throwable t) {
                    DialogUtils.hideLoadingProgress();
                    Toast.makeText(QADetailActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void initData() {
        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("counselingSn", counselingSn);
        HotelApplication.serviceApi.findCounselingDetail(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<CounselingDetailForm>>() {
            @Override
            public void onResponse(Call<List<CounselingDetailForm>> call, Response<List<CounselingDetailForm>> response) {
                DialogUtils.hideLoadingProgress();
                if(response.isSuccessful()){
                    counselingDetailForms = response.body();
                    QADetailAdapter qaDetailAdapter = new QADetailAdapter(QADetailActivity.this, counselingDetailForms, appUserForm.getSn());
                    lvQA.setAdapter(qaDetailAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<CounselingDetailForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
        if(isNotification) {
            Map<String, Object> params1 = new HashMap<>();
            params1.put("counselingSn", notificationData.getSn());
            HotelApplication.serviceApi.findCounselingViaUser(params1, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<CounselingForm>() {
                @Override
                public void onResponse(Call<CounselingForm> call, Response<CounselingForm> response) {

                    CounselingForm counselingForm = response.body();
                    if(response.isSuccessful() && counselingForm != null){
                        String title = counselingForm.getTitle();
                        String nickname = counselingForm.getAppUserNickName();
                        if(nickname==null){
                            nickname = appUserForm.getNickName();
                        }
                        String strDate = counselingForm.getLastUpdate();

                        tvTitle.setText(title);
                        tvNickname.setText(nickname);
                        try{
                            SimpleDateFormat apiFormat = new SimpleDateFormat(getString(R.string.date_format_request));
                            SimpleDateFormat viewFormat = new SimpleDateFormat(getString(R.string.date_format_view));
                            Date date = apiFormat.parse(strDate);
                            tvDate.setText(viewFormat.format(date));

                        }catch (Exception e){
                            MyLog.writeLog("QADetailActivity " + e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<CounselingForm> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void setScreenName() {
        this.screenName="SSetQNADetail";
    }
}
