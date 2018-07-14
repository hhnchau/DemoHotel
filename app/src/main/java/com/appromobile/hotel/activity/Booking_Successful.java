package com.appromobile.hotel.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.PaymentInfoForm;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.Utils;
import com.google.gson.Gson;

public class Booking_Successful extends BaseActivity implements View.OnClickListener {
    private TextView txtPayNow, txtPayLater;
    private int userBookingSn;
    private int minPrice;
    private String methodPayment = "";
    private String FLAG_SHOW_REWARD_CHECKIN = "FLAG_SHOW_REWARD_CHECKIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking__successful);
        setScreenName();
        Init();
    }

    @Override
    public void setScreenName() {
        this.screenName = "SBookingSuccessfully";
    }

    private void Init() {
        userBookingSn = getIntent().getIntExtra("userBookingSn", 0);
        methodPayment = getIntent().getStringExtra("METHOD_PAYMENT");
        txtPayNow = findViewById(R.id.textView_booking_successful_paynow);
        txtPayNow.setOnClickListener(this);
        txtPayLater = findViewById(R.id.textView_booking_successful_paylater);
        txtPayLater.setOnClickListener(this);

        if (HotelApplication.apiSettingForm == null) {
            ControllerApi.getmInstance().findApiSetting(this, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    ApiSettingForm apiSettingForm = (ApiSettingForm) object;
                    minPrice = apiSettingForm.getMinMoney();
                }
            });
        } else {
            minPrice = HotelApplication.apiSettingForm.getMinMoney();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView_booking_successful_paynow) {
            int totalFee = getIntent().getIntExtra("total", 0);
            String minPriceString = " " + Utils.formatCurrency(minPrice) + " VNĐ";
            if (totalFee < minPrice) {
                Toast.makeText(Booking_Successful.this, getString(R.string.msg_3_1_min_price) + minPriceString, Toast.LENGTH_LONG).show();
            } else {
                Gson gson = new Gson();
                PaymentInfoForm paymentInfoForm = gson.fromJson(getIntent().getStringExtra("otherInfo"), PaymentInfoForm.class);
                Intent intent = new Intent(getApplicationContext(), BrowserPaymentActivity.class);
                intent.putExtra("PaymentInfoForm", paymentInfoForm);
                intent.putExtra("userBookingSn", userBookingSn);
                intent.putExtra("METHOD_PAYMENT", methodPayment);
                startActivity(intent);

                //Only Pay_online
                //close Activity Reservation
//                if (ReservationActivity.reservation != null) {
//                    ReservationActivity.reservation.finish();
//                }

                //close Activity HotelDetailActivity
                if (HotelDetailActivity.hotelDetailActivity != null) {
                    HotelDetailActivity.hotelDetailActivity.finish();
                }

                finish();

                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }


        } else if (v.getId() == R.id.textView_booking_successful_paylater) {

            DialogUtils.showLoadingProgress(Booking_Successful.this, false);
            //call Api
            // true = get reware coupon information
            // thanh toán xong mới gọi
            ControllerApi.getmInstance().findUserBookingDetail(Booking_Successful.this, userBookingSn, true, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    //hide loading
                    DialogUtils.hideLoadingProgress();
                    //result
                    UserBookingForm userBookingForm = (UserBookingForm) object;

                    Intent detail = new Intent(Booking_Successful.this, ReservationDetailActivity.class);
                    detail.putExtra("UserBookingForm", userBookingForm);
                    detail.putExtra(FLAG_SHOW_REWARD_CHECKIN, false);
                    startActivity(detail);
                    finish();
                    overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                }
            });

        }
    }
}
