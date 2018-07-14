package com.appromobile.hotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.UserReviewForm;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

/**
 * Created by xuan on 8/11/2016.
 * Kingpes
 */
public class RateReviewDetailActivity extends BaseActivity {
    public static Activity rateReviewDetailActivity;

    public static final int CALL_BOOKING = 1002;
    LinearLayout btnBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rateReviewDetailActivity = this;
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
        setContentView(R.layout.rate_review_detail_activity);
        btnBook =  findViewById(R.id.btnBook);
        TextViewSFRegular tvNickname = findViewById(R.id.tvNickname);
        TextViewSFRegular tvRoomName =findViewById(R.id.tvRoomName);
        TextViewSFRegular tvComment =  findViewById(R.id.tvComment);
        TextViewSFRegular tvDate =  findViewById(R.id.tvDate);
        ImageView btnEdit =  findViewById(R.id.btnEdit);
        TextViewSFRegular tvHotelTitle =  findViewById(R.id.tvHotelTitle);
        LinearLayout boxMyReview =  findViewById(R.id.boxMyReview);
        ImageView []stars = new ImageView[5];
        stars[0] =  findViewById(R.id.btnStar1);
        stars[1] = findViewById(R.id.btnStar2);
        stars[2] = findViewById(R.id.btnStar3);
        stars[3] = findViewById(R.id.btnStar4);
        stars[4] =  findViewById(R.id.btnStar5);
        final UserReviewForm userReviewForm = getIntent().getParcelableExtra("UserReviewForm");
        final HotelDetailForm hotelDetailForm  = getIntent().getParcelableExtra("HotelDetailForm");

        tvHotelTitle.setText(hotelDetailForm.getName());

        /*
        * Set Trial
        */
        if(hotelDetailForm.getHotelStatus()== ContractType.CONTRACT.getType() || (hotelDetailForm.getHotelStatus()== ContractType.TRIAL.getType())) {
            tvRoomName.setText(userReviewForm.getRoomTypeName() + "/" + userReviewForm.getRoomName());
        }
        tvComment.setText(userReviewForm.getComment());

        try {
            SimpleDateFormat formatApi = new SimpleDateFormat(getString(R.string.date_format_request));
            SimpleDateFormat formatView = new SimpleDateFormat(getString(R.string.date_format_view));
            Date date = formatApi.parse(userReviewForm.getCreateTime());
            tvDate.setText(formatView.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            tvNickname.setText(userReviewForm.getUserNickName());
        }catch (Exception e){}

        for (int i=0;i<stars.length;i++){
            stars[i].setImageResource(R.drawable.review_star);
        }
        if(userReviewForm.getMark()>0){
            for (int i = 1; i<= userReviewForm.getMark(); i++){
                stars[i-1].setImageResource(R.drawable.review_star_fill);
            }
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(RateReviewDetailActivity.this, UpdateRateReviewActivity.class);
                intent.putExtra("HotelDetailForm", hotelDetailForm);
                intent.putExtra("UserReviewForm", userReviewForm);
                startActivityForResult(intent, 1001);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });

        if(userReviewForm.isAuthor()){
            boxMyReview.setVisibility(View.VISIBLE);
        }else{
            boxMyReview.setVisibility(View.GONE);
        }

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
            }
        });

        /*
        * Set Trial
        */

        if (hotelDetailForm.getHotelStatus() == ContractType.CONTRACT.getType() || (hotelDetailForm.getHotelStatus()== ContractType.TRIAL.getType())) {
            if (hotelDetailForm.getRoomTypeList() != null && hotelDetailForm.getRoomTypeList().size() > 0) {
                btnBook.setVisibility(View.VISIBLE);
            }
        }

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreferenceUtils.getToken(RateReviewDetailActivity.this).equals("")) {
                    Intent intent = new Intent(RateReviewDetailActivity.this, ReservationActivity.class);
                    intent.putExtra("HotelDetailForm", hotelDetailForm);
                    startActivityForResult(intent, CALL_BOOKING);
                    intent.putExtra("RoomTypeIndex", 0);
                    overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                } else {
                    Intent intent = new Intent(RateReviewDetailActivity.this, LoginActivity.class);
                    startActivityForResult(intent, ParamConstants.REQUEST_LOGIN_HOME);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1001){
            Intent intent = new Intent();
            setResult(resultCode, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    public void setScreenName() {
        this.screenName="SHotelReviewDetail";
    }
}
