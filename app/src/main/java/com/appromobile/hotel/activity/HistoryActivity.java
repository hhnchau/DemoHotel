package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.HistoryAdapter;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.NotificationData;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.crashlytics.android.Crashlytics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 8/1/2016.
 */
public class HistoryActivity extends BaseActivity {
    private ListView lvHistory;
    AppUserForm appUserForm;
    boolean isNotification = false;
    NotificationData notificationData;
    List<UserBookingForm> userBookingForms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
        }

        appUserForm = PreferenceUtils.getAppUser(this);
        setContentView(R.layout.history_activity);
        lvHistory = findViewById(R.id.lvHistory);
        isNotification = getIntent().getBooleanExtra("NOTIFICATON_SEND", false);

        if (isNotification) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                notificationData = bundle.getParcelable("NotificationData");
            }
        }

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ControllerApi.getmInstance().findUserBookingDetail(HistoryActivity.this, userBookingForms.get(position).getSn(), true, new ResultApi() {
                    @Override
                    public void resultApi(Object object) {
                        //hide loading
                        DialogUtils.hideLoadingProgress();
                        //result
                        UserBookingForm userBookingForm = (UserBookingForm) object;

                        Intent detail = new Intent(HistoryActivity.this, ReservationDetailActivity.class);
                        detail.putExtra("UserBookingForm", userBookingForm);
                        detail.putExtra(Booking_Successful.FLAG_SHOW_REWARD_CHECKIN, false);
                        startActivity(detail);
                    }
                });
            }
        });
    }

    private void initData() {
        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("appUserSn", appUserForm.getSn());
        params.put("offset", 0);
        params.put("limit", 100);
        HotelApplication.serviceApi.findLimitHistoryReservationList(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<UserBookingForm>>() {
            @Override
            public void onResponse(Call<List<UserBookingForm>> call, Response<List<UserBookingForm>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    userBookingForms = response.body();
                    HistoryAdapter historyAdapter = new HistoryAdapter(HistoryActivity.this, userBookingForms);
                    lvHistory.setAdapter(historyAdapter);

                    if (isNotification) {
                        Intent intent = new Intent(HistoryActivity.this, ReservationDetailActivity.class);
                        intent.putExtra("UserBookingForm", userBookingForms.get(findHistoryIndex(notificationData.getSn())));
                        startActivity(intent);
                        isNotification = false;
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserBookingForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(HistoryActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    public int findHistoryIndex(int sn) {
        for (int i = 0; i < userBookingForms.size(); i++) {
            if (userBookingForms.get(i).getSn() == sn) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PreferenceUtils.getToken(this).equals("")) {
            initData();
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetHistory";
    }
}
