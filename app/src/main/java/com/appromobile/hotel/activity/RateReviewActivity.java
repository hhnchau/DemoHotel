package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.RoomNoSpinAdapter;
import com.appromobile.hotel.adapter.RoomTypeSpinAdapter;
import com.appromobile.hotel.model.request.UserReviewDto;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.RoomForm;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.model.view.UserReviewForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/12/2016.
 */
public class RateReviewActivity extends BaseActivity implements View.OnClickListener {
    private EditTextSFRegular txtComment;
    private TextViewSFBold btnDone;
    private Spinner spinRoomType, spinRoomNo;
    private ImageView btnBack;
    private ImageView[] btnStars = new ImageView[5];
    private RoomTypeSpinAdapter roomTypeSpinAdapter;
    private RoomNoSpinAdapter roomNoSpinAdapter;
    private TextViewSFRegular tvHotelName;
    private List<UserReviewForm> myReviewForms;
    private LinearLayout linearLayout_hide;
    HotelDetailForm hotelDetailForm;
    List<RoomTypeForm> roomTypeForms;
    int mark = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.rate_review_activity);
        linearLayout_hide = findViewById(R.id.linearLayout_hide);
        linearLayout_hide.setVisibility(View.GONE);
        tvHotelName =  findViewById(R.id.tvHotelName);

        getIntent().setExtrasClassLoader(HotelDetailForm.class.getClassLoader());
        hotelDetailForm = getIntent().getParcelableExtra("HotelDetailForm");

        txtComment = findViewById(R.id.txtComment);
        btnDone =  findViewById(R.id.btnDone);
        spinRoomType =  findViewById(R.id.spinRoomType);
        spinRoomNo = findViewById(R.id.spinRoomNo);
        btnBack = findViewById(R.id.btnBack);
        btnDone.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        btnStars[0] = findViewById(R.id.btnStar1);
        btnStars[1] =  findViewById(R.id.btnStar2);
        btnStars[2] =  findViewById(R.id.btnStar3);
        btnStars[3] = findViewById(R.id.btnStar4);
        btnStars[4] = findViewById(R.id.btnStar5);

        btnStars[0].setOnClickListener(this);
        btnStars[1].setOnClickListener(this);
        btnStars[2].setOnClickListener(this);
        btnStars[3].setOnClickListener(this);
        btnStars[4].setOnClickListener(this);

        initData();
    }

    private void initData() {
        DialogUtils.showLoadingProgress(this, false);
        Map<String, Object> params = new HashMap<>();
        params.put("hotelSn", hotelDetailForm.getSn());
        HotelApplication.serviceApi.findUserReviewViaUser(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<UserReviewForm>>() {
            @Override
            public void onResponse(Call<List<UserReviewForm>> call, Response<List<UserReviewForm>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    myReviewForms = response.body();
                    roomTypeForms = filterRoomType(hotelDetailForm.getRoomTypeList());
                    roomTypeSpinAdapter = new RoomTypeSpinAdapter(RateReviewActivity.this, roomTypeForms);
                    spinRoomType.setAdapter(roomTypeSpinAdapter);
                    tvHotelName.setText(hotelDetailForm.getName());
                    if (roomTypeSpinAdapter.getCount() > 0) {
                        initRoomNo();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserReviewForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });


        spinRoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initRoomNo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<RoomTypeForm> filterRoomType(List<RoomTypeForm> roomTypeList) {
        try {
            List<RoomTypeForm> roomTypeForms = roomTypeList;
//            if (myReviewForms.size() == roomTypeForms.size()) {
//                roomTypeForms.clear();
//            }
            for (int j = 0; j < myReviewForms.size(); j++) {
                for (int i = 0; i < roomTypeForms.size(); i++) {
                    if (roomTypeForms.size() > 0) {
                        if (myReviewForms.get(j).getRoomTypeSn() == roomTypeForms.get(i).getSn()) {
                            roomTypeForms.remove(i);
                            i = 0;
                        }
                    }
                }
            }
            return roomTypeForms;
        } catch (Exception e) {
            return null;
        }
    }

    private void initRoomNo() {
        int roomTypeSn = (int) roomTypeSpinAdapter.getItem(spinRoomType.getSelectedItemPosition());
        Map<String, Object> params = new HashMap<>();
        params.put("roomTypeSn", roomTypeSn);
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.findAllRoomNoList(params, HotelApplication.DEVICE_ID).enqueue(new Callback<List<RoomForm>>() {
            @Override
            public void onResponse(Call<List<RoomForm>> call, Response<List<RoomForm>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    roomNoSpinAdapter = new RoomNoSpinAdapter(RateReviewActivity.this, response.body());
                    spinRoomNo.setAdapter(roomNoSpinAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<RoomForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;
            case R.id.btnStar1:
                showStar(0);
                mark = 1;
                break;
            case R.id.btnStar2:
                showStar(1);
                mark = 2;
                break;
            case R.id.btnStar3:
                showStar(2);
                mark = 3;
                break;
            case R.id.btnStar4:
                showStar(3);
                mark = 4;
                break;
            case R.id.btnStar5:
                showStar(4);
                mark = 5;
                break;
            case R.id.btnDone:
                postRateReview();
                break;

        }
    }

    private void postRateReview() {
        if (mark == -1) {
            return;
        }

        if (txtComment.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.enter_your_comment), Toast.LENGTH_LONG).show();
            return;
        }

        UserReviewDto userReviewDto = new UserReviewDto();
        userReviewDto.setMark(mark);
        userReviewDto.setComment(txtComment.getText().toString());
        userReviewDto.setHotelSn(hotelDetailForm.getSn());
        try {
            if (roomNoSpinAdapter.getCount() > 0) {
                int roomSn = (int) roomNoSpinAdapter.getItem(spinRoomNo.getSelectedItemPosition());
                if (roomSn != 0) {
                    userReviewDto.setRoomSn((long) roomSn);
                }
            }
        } catch (Exception e) {
        }
        try {
            if (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {
                int roomTypeSn = (int) roomTypeSpinAdapter.getItem(spinRoomType.getSelectedItemPosition());
                if (roomTypeSn != 0) {
                    userReviewDto.setRoomTypeSn((long) roomTypeSn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.createUserReview(userReviewDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(RateReviewActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showStar(int starIndex) {
        for (ImageView btnStar : btnStars) {
            btnStar.setImageResource(R.drawable.review_star);
        }
        for (int i = 0; i <= starIndex; i++) {
            btnStars[i].setImageResource(R.drawable.review_star_fill);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void setScreenName() {
        this.screenName="SWriteRateReview";
    }
}
