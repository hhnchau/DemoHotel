package com.appromobile.hotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.UrlParams;
import com.appromobile.hotel.model.request.UserReviewDto;
import com.appromobile.hotel.model.view.LastBookingForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.picture.PictureGlide;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.EditTextSFRegular;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 12/16/2016.
 */

public class RateReviewPopupCheckinActivity extends BaseActivity implements View.OnClickListener {
    private LastBookingForm lastBookingForm;
    TextViewSFRegular tvDate;
    TextViewSFBold tvHotelName, btnSubmit;
    EditTextSFRegular txtContent;
    private ImageView[] btnStars = new ImageView[5];
    int mark = 5;
    ImageView imgHotel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        setContentView(R.layout.rate_review_popup);

        getIntent().setExtrasClassLoader(LastBookingForm.class.getClassLoader());
        lastBookingForm = getIntent().getExtras().getParcelable("LastBookingForm");

        btnSubmit =  findViewById(R.id.btnSubmit);
        tvHotelName =  findViewById(R.id.tvHotelName);
        tvDate =  findViewById(R.id.tvDate);
        txtContent =  findViewById(R.id.txtContent);
        btnStars[0] =  findViewById(R.id.btnStar1);
        btnStars[1] =  findViewById(R.id.btnStar2);
        btnStars[2] =  findViewById(R.id.btnStar3);
        btnStars[3] =  findViewById(R.id.btnStar4);
        btnStars[4] =  findViewById(R.id.btnStar5);
        imgHotel = findViewById(R.id.imgHotel);

        btnStars[0].setOnClickListener(this);
        btnStars[1].setOnClickListener(this);
        btnStars[2].setOnClickListener(this);
        btnStars[3].setOnClickListener(this);
        btnStars[4].setOnClickListener(this);

        fillData();

        btnSubmit.setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void postRating() {
        if (mark == -1) {
            return;
        }

//        if (txtContent.getText().toString().equals("")) {
//            Toast.makeText(this, getString(R.string.enter_your_comment), Toast.LENGTH_LONG).show();
//            return;
//        }

        UserReviewDto userReviewDto = new UserReviewDto();
        userReviewDto.setMark(mark);
        userReviewDto.setComment(txtContent.getText().toString());
        userReviewDto.setHotelSn(lastBookingForm.getHotelSn());
        userReviewDto.setRoomTypeSn((long) lastBookingForm.getRoomTypeSn());

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
                Toast.makeText(RateReviewPopupCheckinActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fillData() {
//        SimpleDateFormat formatApi = new SimpleDateFormat(getString(R.string.date_format_request));
//        SimpleDateFormat formatView = new SimpleDateFormat(getString(R.string.date_format_view));
        try{
//            System.out.println("lastBookingForm.getCheckInDatePlan():"+lastBookingForm.getCheckInDatePlan());
//            Date date = formatApi.parse(lastBookingForm.getCheckInDatePlan());
            tvDate.setText(lastBookingForm.getCheckInDatePlan());
        }catch (Exception e){}
        tvHotelName.setText(lastBookingForm.getHotelName());

        //String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelHomeImage?hotelSn=" + lastBookingForm.getHotelSn();
        String url = UrlParams.MAIN_URL + "/hotelapi/hotel/download/downloadHotelImageViaKey?imageKey=" + lastBookingForm.getImageKey();

        try {
            PictureGlide.getInstance().show(url, getResources().getDimensionPixelSize(R.dimen.hotel_popup_width), getResources().getDimensionPixelSize(R.dimen.hotel_popup_height),R.drawable.loading_small, imgHotel);
        }catch (Exception e){}


        //set default 5 star
        showStar(4);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.btnSubmit:
                postRating();
                break;

        }
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

    }

    @Override
    public void setScreenName() {
        this.screenName="SPopBook";
    }
}
