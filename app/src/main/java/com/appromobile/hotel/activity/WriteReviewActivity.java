package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.RecentHotelReviewAdapter;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.HotelForm;
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
 * Created by xuan on 7/5/2016.
 */
public class WriteReviewActivity extends BaseActivity implements View.OnClickListener{
    ListView lvRecentHotel;
    List<HotelForm> hotelForms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);
        setScreenName();
        try {
            Fabric.with(this, new Crashlytics());
        }catch (Exception e){}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.org));
        }
        setContentView(R.layout.write_review_activity);
        lvRecentHotel = findViewById(R.id.lvRecentHotel);
        findViewById(R.id.btnClose).setOnClickListener(this);
        findViewById(R.id.btnChooseFromList).setOnClickListener(this);

        lvRecentHotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HotelForm hotelForm = hotelForms.get(position);
                try {
                    if(hotelForm.getRoomTypeFormList()!=null && hotelForm.getRoomTypeFormList().size()>0) {
                        HotelDetailForm hotelDetailForm = new HotelDetailForm();
                        hotelDetailForm.setName(hotelForm.getName());
                        hotelDetailForm.setAddress(hotelForm.getAddress());
                        hotelDetailForm.setRoomTypeList(hotelForm.getRoomTypeFormList());
                        hotelDetailForm.setHotelStatus(hotelForm.getHotelStatus());
                        Intent intent = new Intent(WriteReviewActivity.this, RateReviewActivity.class);
                        intent.putExtra("HotelDetailForm", hotelDetailForm);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        initData();
    }

    private void initData() {
        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("offset", 0);
        params.put("limit", 3);
        HotelApplication.serviceApi.findLimitRecentHotelList(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<HotelForm>>() {
            @Override
            public void onResponse(Call<List<HotelForm>> call, Response<List<HotelForm>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    hotelForms = response.body();
                    RecentHotelReviewAdapter recentHotelAdapter = new RecentHotelReviewAdapter(WriteReviewActivity.this, hotelForms);
                    lvRecentHotel.setAdapter(recentHotelAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<HotelForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(WriteReviewActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnClose:
                finish();
                break;
            case R.id.btnChooseFromList:
                setResult(RESULT_OK, null);
                finish();
                break;
        }
    }

    @Override
    public void setScreenName() {
        this.screenName="SWriteReview";
    }
}
