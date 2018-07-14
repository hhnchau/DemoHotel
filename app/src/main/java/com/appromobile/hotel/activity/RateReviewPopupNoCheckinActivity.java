package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.enums.ReasonNotCheckinType;
import com.appromobile.hotel.model.request.UpdateReasonDto;
import com.appromobile.hotel.model.view.LastBookingForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.utils.GlideApp;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 12/16/2016.
 */

public class RateReviewPopupNoCheckinActivity extends BaseActivity{
    private LinearLayout lnFramePopup;
    private LastBookingForm lastBookingForm;
    private TextViewSFRegular tvDate;
    private TextViewSFBold tvHotelName;
    private ImageView imgHotel;
    private ReasonNotCheckinType reasonNotCheckinType = ReasonNotCheckinType.NoVisit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        setContentView(R.layout.rate_review_popup_no_checkin);
        lnFramePopup = findViewById(R.id.frame_popup);
        tvDate =  findViewById(R.id.tvDate);
        imgHotel =  findViewById(R.id.imgHotel);
        tvHotelName =  findViewById(R.id.tvHotelName);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            lastBookingForm = bundle.getParcelable("LastBookingForm");
        }

        findViewById(R.id.btnNoVisit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonNotCheckinType = ReasonNotCheckinType.NoVisit;
                updateReasonNotCheckin();
            }
        });

        findViewById(R.id.btnCheckedIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonNotCheckinType = ReasonNotCheckinType.CheckinHotel;
                updateReasonNotCheckin();
            }
        });

        findViewById(R.id.btnNoRoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonNotCheckinType = ReasonNotCheckinType.NoRoom;
                updateReasonNotCheckin();
            }
        });
        
        lnFramePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RateReviewPopupNoCheckinActivity.this, getString(R.string.msg_11_1_please_choose_one_of_theme), Toast.LENGTH_LONG).show();
            }
        });

        fillData();
    }

    private void updateReasonNotCheckin() {
        UpdateReasonDto updateReasonDto = new UpdateReasonDto();
        updateReasonDto.setUserBookingSn(lastBookingForm.getSn());
        updateReasonDto.setReasonNotCheckin(reasonNotCheckinType.getType());
        HotelApplication.serviceApi.updateReasonNotCheckin(updateReasonDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                if(response.isSuccessful()){
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                Toast.makeText(RateReviewPopupNoCheckinActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fillData() {
        try{
            tvDate.setText(lastBookingForm.getCheckInDatePlan());
        }catch (Exception e){}
        tvHotelName.setText(lastBookingForm.getHotelName());
        String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelHomeImage?hotelSn=" + lastBookingForm.getHotelSn();
        try {
            GlideApp
                    .with(imgHotel.getContext())
                    .load(url).placeholder(R.drawable.loading_small)
                    .override(getResources().getDimensionPixelSize(R.dimen.hotel_popup_width), getResources().getDimensionPixelSize(R.dimen.hotel_popup_height))
                    .into(imgHotel);
        }catch (Exception e){}
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void setScreenName() {
        this.screenName="SPopBook";
    }
}
