package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.CouponAdapter;
import com.appromobile.hotel.model.view.CouponIssuedForm;
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
public class MyCouponActivity extends BaseActivity {
    private ListView lvMyCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();

        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {

        }

        setContentView(R.layout.my_coupon_activity);

        lvMyCoupon =  findViewById(R.id.lvMyCoupon);
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

    }

    private void initData() {
        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("offset", 0);
        params.put("limit", 100);
        HotelApplication.serviceApi.findLimitCouponList(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<CouponIssuedForm>>() {
            @Override
            public void onResponse(Call<List<CouponIssuedForm>> call, Response<List<CouponIssuedForm>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    CouponAdapter couponAdapter = new CouponAdapter(MyCouponActivity.this, response.body());
                    lvMyCoupon.setAdapter(couponAdapter);
                } else if (response.code() == 401) {
                    Intent intent = new Intent(MyCouponActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<CouponIssuedForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(MyCouponActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetCoupon";
    }
}
