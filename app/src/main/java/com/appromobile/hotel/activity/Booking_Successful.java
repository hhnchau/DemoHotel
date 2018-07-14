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
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.Utils;
import com.google.gson.Gson;

public class Booking_Successful extends BaseActivity implements View.OnClickListener {
    private TextView txtPayNow, txtPayLater;
    private int userBookingSn;
    private int minPrice;
    private String methodPayment = "";
    public static String FLAG_SHOW_REWARD_CHECKIN = "FLAG_SHOW_REWARD_CHECKIN";
    private boolean paymentPromotion = false;

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
        Bundle bundle = getIntent().getExtras();
        if ((bundle) != null) {
            userBookingSn = bundle.getInt("userBookingSn", 0);
            methodPayment = bundle.getString("METHOD_PAYMENT");

            String mapString = bundle.getString("MAP-INFO");
            if (mapString != null && mapString.equals("")) {
                paymentPromotion = true;
            }
        }


        txtPayNow = findViewById(R.id.textView_booking_successful_paynow);
        txtPayNow.setOnClickListener(this);

        //Hide Paynow
        if (!paymentPromotion)
            txtPayNow.setVisibility(View.GONE);

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
                gotoBillingInfo();
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

    private void gotoPay123() {
        Gson gson = new Gson();
        PaymentInfoForm paymentInfoForm = gson.fromJson(getIntent().getStringExtra("otherInfo"), PaymentInfoForm.class);
        Intent intent = new Intent(getApplicationContext(), BrowserPaymentActivity.class);
        intent.putExtra("PaymentInfoForm", paymentInfoForm);
        intent.putExtra("userBookingSn", userBookingSn);
        intent.putExtra("METHOD_PAYMENT", methodPayment);
        startActivity(intent);

        //close Activity HotelDetailActivity
        if (HotelDetailActivity.hotelDetailActivity != null) {
            HotelDetailActivity.hotelDetailActivity.finish();
        }

        finish();

        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void gotoBillingInfo() {


        ControllerApi.getmInstance().findUserBookingDetail(Booking_Successful.this, userBookingSn, true, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                //hide loading
                DialogUtils.hideLoadingProgress();
                //result
                UserBookingForm userBookingForm = (UserBookingForm) object;

                Bundle bundle = new Bundle();

                bundle.putInt("discountFee", userBookingForm.getDiscount());
                bundle.putInt("totalFee", userBookingForm.getTotalAmount());
                bundle.putInt("total", userBookingForm.getAmountFromUser());
                bundle.putInt("ROOM_TYPE", userBookingForm.getRoomTypeSn());
                bundle.putString("START_TIME", userBookingForm.getStartTime());
                bundle.putString("END_TIME", userBookingForm.getEndTime());
                bundle.putString("END_DATE", userBookingForm.getEndDate());
                bundle.putString("DATE_PLAN", userBookingForm.getCheckInDatePlan());
                bundle.putInt("TYPE", userBookingForm.getType());
                if (userBookingForm.getCouponIssuedSn() == null) {
                    bundle.putLong("COUPON", -1);
                } else {
                    bundle.putLong("COUPON", userBookingForm.getCouponIssuedSn());
                }
                bundle.putBoolean("FLASH_SALE", false);
                bundle.putString("HOTEL_PAYMENT", "2"); //For PayAtHotel
                bundle.putInt("HOTEL_STATUS", userBookingForm.getHotelStatus());
                if (userBookingForm.getPaymentOption() == ParamConstants.PAYMENT_ONLINE) {
                    bundle.putString("METHOD_PAYMENT", ParamConstants.METHOD_ALWAYS_PAY_ONLINE);
                } else {
                    bundle.putString("METHOD_PAYMENT", ParamConstants.METHOD_PAY_AT_HOTEL);
                }

                bundle.putString("IP", Utils.getClientIp());

                //Add UserBookingSn For Old Payment
                bundle.putInt("userBookingSn", userBookingForm.getSn());

                Intent billing_information = new Intent(Booking_Successful.this, Billing_Information.class);
                billing_information.setAction("Old_Payment");
                billing_information.putExtra("InformationBilling", bundle);
                startActivity(billing_information);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);

                //finish();

            }
        });
    }
}
