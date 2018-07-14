package com.appromobile.hotel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.request.UserBookingDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.PaymentInfoForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Billing_Information extends BaseActivity implements View.OnClickListener {
    private ImageView imgBack;
    private TextView txtTitle, txtFee, txtDiscount, txtStamp, txtTotal;
    private LinearLayout lnHotel, lnCard;
    private Bundle bundle;
    public static Activity billingInformation;
    private int minPrice;
    private int hotelStatus;
    private boolean isFlashSale = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing__information);
        setScreenName();
        billingInformation = this;
        Init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setView();
    }

    private void Init() {
        bundle = getIntent().getBundleExtra("InformationBilling");
        hotelStatus = bundle.getInt("HOTEL_STATUS");

        imgBack =  findViewById(R.id.imageView_billing_infomation_back);
        imgBack.setOnClickListener(this);
        txtTitle =  findViewById(R.id.textView_billing_infomation_title);
        txtFee =  findViewById(R.id.textView_billing_infomation_fee);
        txtDiscount = findViewById(R.id.textView_billing_infomation_discount);
        txtStamp = findViewById(R.id.textView_billing_infomation_stamp);
        txtTotal =  findViewById(R.id.textView_billing_infomation_total);
        lnHotel =  findViewById(R.id.linear_billing_infomation_pay_at_hotel);
        lnHotel.setOnClickListener(this);
        lnCard =  findViewById(R.id.linear_billing_infomation_local_card);

        /*
        /Check Flash Sale
        */
        isFlashSale = bundle.getBoolean("FLASH_SALE");
        if (isFlashSale) {

            Toast.makeText(this, getString(R.string.msg_3_9_flashsale_pay_in_advance), Toast.LENGTH_LONG).show();

            setViewFlashSale();
        }

        /*
        * check trial hotel
        */
        if (hotelStatus == ContractType.TRIAL.getType()) {
            lnCard.setAlpha(0.5f);
        }
        lnCard.setOnClickListener(this);

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
    public void setScreenName() {
        this.screenName = "SBillingInformation";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_billing_infomation_pay_at_hotel:
                Intent hotel = new Intent(Billing_Information.this, Paid_At_Hotel.class);
                hotel.putExtra("InformationBilling", bundle);
                startActivity(hotel);
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                break;
            case R.id.linear_billing_infomation_local_card:
                checkMinPriceBeforeMakeReservation();
                break;
            case R.id.imageView_billing_infomation_back:
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;
        }
    }

    /*
    * Check Pay123
    */
    private void checkMinPriceBeforeMakeReservation() {

        /*
        * check trial hotel
        */
        if (hotelStatus == ContractType.TRIAL.getType()) { //Trial hotel
            Toast.makeText(Billing_Information.this, getString(R.string.msg_can_not_payment_trial), Toast.LENGTH_LONG).show();
        } else {
            int totalFee = bundle.getInt("total");
            String minPriceString = " " + Utils.formatCurrency(minPrice) + " VNĐ";
            if (totalFee < minPrice) {
                Toast.makeText(Billing_Information.this, getString(R.string.msg_3_1_min_price) + minPriceString, Toast.LENGTH_LONG).show();
            } else {
                createReservationfor123Pay();
            }
        }

    }


    private void setView() {
        if (bundle != null) {

            //Fee
            txtFee.setText(Utils.formatCurrency(bundle.getInt("totalFee")) + " VNĐ");

            //Discount
            int discountFee = bundle.getInt("discountFee");
            if (discountFee > 0) {
                txtDiscount.setText("-" + Utils.formatCurrency(discountFee) + " VNĐ");
            } else {
                txtDiscount.setText("0 VNĐ");
            }

            //Stamp
            int redeemValue = bundle.getInt("redeemValue");
            if (redeemValue > 0) {
                txtStamp.setText("-" + Utils.formatCurrency(redeemValue) + " VNĐ");
            } else {
                txtStamp.setText("0 VNĐ");
            }

            //Total
            txtTotal.setText(Utils.formatCurrency(bundle.getInt("total")) + " VNĐ");
        }
    }

    private void createReservationfor123Pay() {
        DialogUtils.showLoadingProgress(this, false);

        if (bundle != null) {

            UserBookingDto userBookingDto = new UserBookingDto();

            userBookingDto.setClientip(bundle.getString("IP"));
            userBookingDto.setRoomTypeSn(bundle.getInt("ROOM_TYPE"));
            userBookingDto.setStartTime(bundle.getString("START_TIME"));
            userBookingDto.setEndTime(bundle.getString("END_TIME"));
            userBookingDto.setEndDate(bundle.getString("END_DATE"));
            userBookingDto.setGetCheckInDatePlan(bundle.getString("DATE_PLAN"));
            userBookingDto.setType(bundle.getInt("TYPE"));
            userBookingDto.setRedeemValue(bundle.getInt("redeemValue"));

            if (bundle.getLong("COUPON") != ReservationActivity.NO_COUPON) {
                userBookingDto.setCouponIssuedSn(bundle.getLong("COUPON"));
            }

            ControllerApi.getmInstance().createNewReservation(this, userBookingDto, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    RestResult restResult = (RestResult) object;

                    Gson gson = new Gson();
                    PaymentInfoForm paymentInfoForm = gson.fromJson(restResult.getOtherInfo(), PaymentInfoForm.class);
                    Intent intent = new Intent(Billing_Information.this, BrowserPaymentActivity.class);
                    intent.putExtra("PaymentInfoForm", paymentInfoForm);
                    intent.putExtra("userBookingSn", restResult.getSn());
                    intent.putExtra("METHOD_PAYMENT", bundle.getString("METHOD_PAYMENT", "default"));
                    intent.putExtra("FLASH_SALE", isFlashSale);

                    startActivity(intent);

                    //Only Pay_online
                    //close Activity Reservation
//                    if (ReservationActivity.reservation != null) {
//                        ReservationActivity.reservation.finish();
//                    }

                    //close Activity HotelDetailActivity
                    if (HotelDetailActivity.hotelDetailActivity != null) {
                        HotelDetailActivity.hotelDetailActivity.finish();
                    }
                    finish();
                    overridePendingTransition(R.anim.right_to_left, R.anim.stable);

                }
            });
        }
    }

    private void setViewFlashSale() {
        lnHotel.setAlpha(0.5f);
        lnHotel.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

}
