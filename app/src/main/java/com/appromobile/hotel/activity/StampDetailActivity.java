package com.appromobile.hotel.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.StampIssuedForm;
import com.appromobile.hotel.model.view.UserStampForm;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;

import java.util.List;

/**
 * Created by appro on 25/12/2017.
 */

public class StampDetailActivity extends BaseActivity {
    private ImageView imgBack, imgIconStamp, imgStamp1, imgStamp2, imgStamp3, imgStamp4, imgStamp5, imgStamp6, imgStamp7, imgStamp8, imgStamp9, imgStamp10;
    private TextView tvTitle, tvNumStamp, tvStamp1, tvStamp2, tvStamp3, tvStamp4, tvStamp5, tvStamp6, tvStamp7, tvStamp8, tvStamp9, tvStamp10, tvValueRedeem, tvTemrOfUse, btnRedeem;
    private ImageView[] arrayImgStamp;
    private TextView[] arrayTvStamp;
    private long hotelSn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stamp_detail_activity);

        Init();

        onClick();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            hotelSn = bundle.getLong("hotelSn", 0);
            findUserStampFormDetail(hotelSn);
        }
    }

    private void findUserStampFormDetail(long hotelSn) {
        ControllerApi.getmInstance().findUserStampFormDetail(this, hotelSn, true, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                UserStampForm userStampForm = (UserStampForm) object;
                if (userStampForm != null) {
                    updateView(userStampForm);
                }
            }
        });
    }

    private void updateView(UserStampForm userStampForm) {
        //Set Title
        tvTitle.setText(userStampForm.getHotelName());

        //Set NumOfStamp
        tvNumStamp.setText(String.valueOf(userStampForm.getNumStampActive()) + "/" + String.valueOf(userStampForm.getNumToRedeem()));

        //Set Stamp
        List<StampIssuedForm> stampList = userStampForm.getStampIssuedFormList();
        if (stampList != null) {
            for (int i = 0; i < stampList.size(); i++) {
                StampIssuedForm stamp = stampList.get(i);
                if (stamp.getStatus() == 1) {
                    //org
                    arrayImgStamp[i].setImageResource(R.drawable.icon_stamp_active);
                    arrayTvStamp[i].setText(stamp.getExpireDate());
                } else if (stamp.getStatus() == 3) {
                    //org no fill
                    arrayImgStamp[i].setImageResource(R.drawable.icon_stamp_inactive);
                    arrayTvStamp[i].setText("");
                } else if (stamp.getStatus() == 4) {
                    //gray
                    arrayImgStamp[i].setImageResource(R.drawable.rounded_gray);
                    arrayTvStamp[i].setText("");
                } else if (stamp.getStatus() == 5) {
                    //org no fill
                    arrayImgStamp[i].setImageResource(R.drawable.icon_stamp_inactive);
                    arrayTvStamp[i].setText("");
                }
            }
        }

        //Set Image
        if (userStampForm.getNumStampActive() >= userStampForm.getNumToRedeem()) {
            //Set Icon
            imgIconStamp.setImageResource(R.drawable.icon_stamp_active);
            //btnRedeem
            btnRedeem.setTextColor(getResources().getColor(R.color.org));
        } else {
            //Set Icon
            imgIconStamp.setImageResource(R.drawable.icon_stamp_inactive);
            //btnRedeem
            btnRedeem.setTextColor(getResources().getColor(R.color.lg));
        }

        //Lock Stamp
        if (userStampForm.getNumStampLocked() > 0) {
            //btnRedeem
            btnRedeem.setTextColor(getResources().getColor(R.color.lg));
        }

        //Stamp V3
        //Check Stamp type
        String value;
        if (userStampForm.getRedeemType() == ParamConstants.DISCOUNT_PERCENT) {
            //Set Value Redeem
            value = getString(R.string.discount) + " " + Utils.formatCurrency(userStampForm.getRedeemValue()) + getString(R.string.percent);
            tvValueRedeem.setText(value);
            value = getString(R.string.txt_6_12_stamp_value) + ": " + value + " - " +getString(R.string.max_discount) + " " + Utils.formatCurrency(userStampForm.getMaxRedeem()) + getString(R.string.vnd);
        } else {
            //Set Value Redeem
            value = Utils.formatCurrency(userStampForm.getRedeemValue()) + " " + getString(R.string.vnd);
            tvValueRedeem.setText(value);
            value = getString(R.string.txt_6_12_stamp_value) + ": " + value;
        }

        //Set TermOfUse
        StringBuilder stringBuilder = new StringBuilder(getString(R.string.txt_6_12_stamp_policy_condision)).append("\n");
        stringBuilder.append("- ").append(getString(R.string.txt_6_12_stamp_policy_number)).append(": ").append(userStampForm.getNumToRedeem()).append("\n");
        stringBuilder.append("- ").append(value).append("\n");
        stringBuilder.append("- ").append(getString(R.string.txt_6_12_stamp_policy_condision)).append(":").append("\n");
        stringBuilder.append(userStampForm.isRedeemHourly() ? getString(R.string.txt_2_flashsale_hourly_price) + ", " : "")
                .append(userStampForm.isRedeemDaily() ? getString(R.string.txt_2_flashsale_overnight_price) + ", " : "")
                .append(userStampForm.isRedeemOvernight() ? getString(R.string.txt_2_flashsale_daily_price) + ", " : "");
        tvTemrOfUse.setText(stringBuilder.substring(0, stringBuilder.length() - 2));

        tvTemrOfUse.append("\n" + ("- ") + getString(R.string.txt_6_12_policy_finish_stamp));
    }

    private void onClick() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.stable);
            }
        });

        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnRedeem.getCurrentTextColor() == getResources().getColor(R.color.org)) {
                    gotoReservation();
                }
            }
        });
    }

    private void gotoReservation() {
        ControllerApi.getmInstance().getHotelDetail(this, hotelSn, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                HotelDetailForm hotelDetailForm = (HotelDetailForm) object;
                if (hotelDetailForm != null) {

                    Intent intent = new Intent(StampDetailActivity.this, ReservationActivity.class);

                    intent.setAction("Stamp");
                    intent.putExtra("HotelDetailForm", hotelDetailForm);
                    int roomTypeIndex = 0;

                    //Status flash sale = 0; -1 # Flash Sale
                    if (hotelDetailForm.checkFlashSale() == 0 && hotelDetailForm.getRoomTypeList().size() > 1) {
                        roomTypeIndex++;
                    }
                    if (hotelDetailForm.checkCineJoy() == 0 && hotelDetailForm.getRoomTypeList().size() > 1) {
                        roomTypeIndex++;
                    }
                    intent.putExtra("RoomTypeIndex", roomTypeIndex);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                    finish();

                }
            }
        });
    }

    private void Init() {
        imgBack = findViewById(R.id.btnClose);
        imgIconStamp = findViewById(R.id.imgIconStamp);
        tvTitle = findViewById(R.id.tvTitle);
        tvNumStamp = findViewById(R.id.tvNumStamp);
        tvStamp1 = findViewById(R.id.tvStamp1);
        tvStamp2 = findViewById(R.id.tvStamp2);
        tvStamp3 = findViewById(R.id.tvStamp3);
        tvStamp4 = findViewById(R.id.tvStamp4);
        tvStamp5 = findViewById(R.id.tvStamp5);
        tvStamp6 = findViewById(R.id.tvStamp6);
        tvStamp7 = findViewById(R.id.tvStamp7);
        tvStamp8 = findViewById(R.id.tvStamp8);
        tvStamp9 = findViewById(R.id.tvStamp9);
        tvStamp10 = findViewById(R.id.tvStamp10);
        imgStamp1 = findViewById(R.id.imgStamp1);
        imgStamp2 = findViewById(R.id.imgStamp2);
        imgStamp3 = findViewById(R.id.imgStamp3);
        imgStamp4 = findViewById(R.id.imgStamp4);
        imgStamp5 = findViewById(R.id.imgStamp5);
        imgStamp6 = findViewById(R.id.imgStamp6);
        imgStamp7 = findViewById(R.id.imgStamp7);
        imgStamp8 = findViewById(R.id.imgStamp8);
        imgStamp9 = findViewById(R.id.imgStamp9);
        imgStamp10 = findViewById(R.id.imgStamp10);
        tvValueRedeem = findViewById(R.id.tvValueRedeem);
        tvTemrOfUse = findViewById(R.id.tvTermsOfuse);
        btnRedeem = findViewById(R.id.btnRedeem);
        arrayTvStamp = new TextView[]{tvStamp1, tvStamp2, tvStamp3, tvStamp4, tvStamp5, tvStamp6, tvStamp7, tvStamp8, tvStamp9, tvStamp10};
        arrayImgStamp = new ImageView[]{imgStamp1, imgStamp2, imgStamp3, imgStamp4, imgStamp5, imgStamp6, imgStamp7, imgStamp8, imgStamp9, imgStamp10};
    }

    @Override
    public void setScreenName() {

    }
}
