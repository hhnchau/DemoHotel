package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.ChooseCouponAdapter;
import com.appromobile.hotel.model.view.CouponIssuedForm;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by xuan on 9/22/2016.
 */

public class ChooseCouponActivity extends BaseActivity {

    private ArrayList<CouponIssuedForm> couponIssuedForms;
    private int couponIndex = ParamConstants.NOTHING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("ChooseCouponActivity " + e);
        }

        setContentView(R.layout.choose_coupon_activity);
        ListView lvMyCoupon = findViewById(R.id.lvMyCoupon);

        /*
        / Get Intent
        */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            couponIssuedForms = bundle.getParcelableArrayList("CouponIssuedForm");
            couponIndex = bundle.getInt("couponIndex", ParamConstants.NOTHING);
        }

        //Set Adapter
        ChooseCouponAdapter roomTypeListAdapter = new ChooseCouponAdapter(this, couponIssuedForms, couponIndex);
        lvMyCoupon.setAdapter(roomTypeListAdapter);

        //Onclick
        lvMyCoupon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Check for click coupon
                if (couponIssuedForms.get(position).getCanUse() != ParamConstants.HOTEL_NOT_ACCEPT) {
                    Intent intent = new Intent();
                    intent.putExtra("CouponIndex", position);
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.stay, R.anim.slide_down_reservation);
                }
            }
        });

        findViewById(R.id.emptyView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_down_reservation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_down_reservation);
    }

    @Override
    public void setScreenName() {
        this.screenName = "SHotelCoupon";
    }
}
