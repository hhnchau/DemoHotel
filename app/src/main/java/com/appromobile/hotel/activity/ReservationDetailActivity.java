package com.appromobile.hotel.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.Dialag;
import com.appromobile.hotel.enums.BookingType;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.request.CancelBookingDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.CouponForm;
import com.appromobile.hotel.model.view.PaymentInfoForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 12/5/2016.
 */

public class ReservationDetailActivity extends BaseActivity {
    private static final int PAYMENT_REQUEST = 1000;
    private TextViewSFRegular btnCancelBooking;
    private TextViewSFRegular tvCheckinCode, tvAddress, tvRoomType, tvBookingType, tvBookingDate, tvBookingTime,
            tvCheckinTime, tvCheckoutTime, tvPrice, tvCouponDiscount, tvStampDiscount, tvTotalPayment, tvPaymentStatus, tvBookingStatus;
    private TextViewSFRegular tvHotelTitle;
    private UserBookingForm userBookingForm;
    private SimpleDateFormat apiFormat, viewFormat;
    TextViewSFBold btnPaynow;
    private int minPrice;
    private String COUPON_NUMBER = "couponNumber";
    private String COUPON_MONEY = "couponMoney";
    private String FLAG_SHOW_REWARD_CHECKIN = "FLAG_SHOW_REWARD_CHECKIN";
    private boolean isDisplayPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();
        setContentView(R.layout.reservation_detail_activity);

        apiFormat = new SimpleDateFormat(getString(R.string.date_format_request));
        viewFormat = new SimpleDateFormat(getString(R.string.date_format_view));

        btnCancelBooking =  findViewById(R.id.btnCancelBooking);
        tvHotelTitle = findViewById(R.id.tvHotelTitle);
        tvCheckinCode =  findViewById(R.id.tvcheckinCode);
        tvAddress =  findViewById(R.id.tvAddress);
        tvRoomType =  findViewById(R.id.tvRoomType);
        tvBookingType =  findViewById(R.id.tvBookingType);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvBookingTime =  findViewById(R.id.tvBookingTime);
        tvCheckinTime =  findViewById(R.id.tvCheckinTime);
        tvCheckoutTime =  findViewById(R.id.tvCheckoutTime);
        tvPrice =  findViewById(R.id.tvPrice);
        tvCouponDiscount =  findViewById(R.id.tvCouponDiscount);
        tvStampDiscount =  findViewById(R.id.tvStampDiscount);
        tvTotalPayment =  findViewById(R.id.tvTotalPayment);
        tvPaymentStatus =  findViewById(R.id.tvPaymentStatus);
        tvBookingStatus = findViewById(R.id.tvBookingStatus);
        btnPaynow =  findViewById(R.id.btnPaynow);
        /*
        * get Intent
        */
        getIntent().setExtrasClassLoader(UserBookingForm.class.getClassLoader());
        userBookingForm = getIntent().getParcelableExtra("UserBookingForm");
        // check the flag if display popup or not
        // if from BrowserPayment or Booking_Successful, flag = true
        // if from MyBooking, flag = false
        isDisplayPopup = getIntent().getBooleanExtra(FLAG_SHOW_REWARD_CHECKIN, false);
        //
        // check if donateCoupon available, then show popup
        //

        if (isDisplayPopup && userBookingForm != null && userBookingForm.getDonateCoupon() != null) {
            CouponForm donateCoupon = userBookingForm.getDonateCoupon();
            if (donateCoupon.getDiscount() > 0) {
                String money = "";
                if (donateCoupon.getDiscountType() == 1) {
                    money = Utils.formatCurrency(donateCoupon.getDiscount()) + " VNĐ";
                } else if (donateCoupon.getDiscountType() == 2) {
                    money = donateCoupon.getDiscount() + "%";
                }
                String discount = this.getResources().getString(R.string.msg_3_9_reward_after_check_in);
                discount = discount.replace(COUPON_NUMBER, String.valueOf(donateCoupon.getNumOfDonate()));
                discount = discount.replace(COUPON_MONEY, money);
                Dialag.getInstance().show(this, false, false, true, null, discount, getString(R.string.ok), null, null, Dialag.BTN_LEFT, new CallbackDialag() {
                    @Override
                    public void button1() {

                    }

                    @Override
                    public void button2() {

                    }

                    @Override
                    public void button3(Dialog dialog) {

                    }
                });
            }
        }

        if (HotelApplication.apiSettingForm == null) {
            ControllerApi.getmInstance().findApiSetting(ReservationDetailActivity.this, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    ApiSettingForm apiSettingForm = (ApiSettingForm) object;
                    minPrice = apiSettingForm.getMinMoney();
                }
            });
        } else {
            minPrice = HotelApplication.apiSettingForm.getMinMoney();
        }

        btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking();
            }
        });

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnPaynow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogUtils.showLoadingProgress(ReservationDetailActivity.this, false);
                /*
                / Check Booking Status
                */

                ControllerApi.getmInstance().findUserBookingDetail(ReservationDetailActivity.this, userBookingForm.getSn(), false, new ResultApi() {
                    @Override
                    public void resultApi(Object object) {
                        //result
                        UserBookingForm userBookingForm = (UserBookingForm) object;
                        if (userBookingForm.getBookingStatus() == 1) { //Booked
                            //Goto Pay123
                            gotoPay123();
                        } else {
                            //hide loading
                            DialogUtils.hideLoadingProgress();
                            //Cancel
                            Toast.makeText(ReservationDetailActivity.this, getString(R.string.msg_6_3_1_booking_was_canceled), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void gotoPay123() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm != null) {
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            Map<String, Object> params = new HashMap<>();
            params.put("userBookingSn", userBookingForm.getSn());
            params.put("clientip", ip);


            HotelApplication.serviceApi.findPaymentInfoForm(params, PreferenceUtils.getToken(ReservationDetailActivity.this), HotelApplication.DEVICE_ID).enqueue(new Callback<PaymentInfoForm>() {
                @Override
                public void onResponse(Call<PaymentInfoForm> call, Response<PaymentInfoForm> response) {

                    //hide loading
                    DialogUtils.hideLoadingProgress();
                    PaymentInfoForm paymentInfoForm = response.body();

                    if (response.isSuccessful() && paymentInfoForm != null) {
                        if (Integer.parseInt(paymentInfoForm.getTotalAmount()) < minPrice) {
                            String s = " " + Utils.formatCurrency(minPrice) + " VNĐ";
                            Toast.makeText(ReservationDetailActivity.this, getString(R.string.msg_3_1_min_price) + s, Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(ReservationDetailActivity.this, BrowserPaymentActivity.class);
                            intent.putExtra("IS_PAY_NOW", true);
                            intent.putExtra("PaymentInfoForm", response.body());
                            intent.putExtra("userBookingSn", userBookingForm.getSn());

                            if (userBookingForm.getPaymentOption() == ParamConstants.PAYMENT_ONLINE) {
                                intent.putExtra("METHOD_PAYMENT", ParamConstants.METHOD_ALWAYS_PAY_ONLINE);
                            } else {
                                intent.putExtra("METHOD_PAYMENT", ParamConstants.METHOD_PAY_AT_HOTEL);
                            }

                            startActivityForResult(intent, PAYMENT_REQUEST);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PaymentInfoForm> call, Throwable t) {

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        filData();
    }

    private void cancelBooking() {
        DialogUtils.showLoadingProgress(this, false);
        CancelBookingDto cancelBookingDto = new CancelBookingDto();
        cancelBookingDto.setUserBookingSn(userBookingForm.getSn());
        HotelApplication.serviceApi.cancelReservation(cancelBookingDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();

                RestResult restResult = response.body();
                if (response.isSuccessful() && restResult != null) {
                    if (restResult.getResult() == 1) {
                        btnCancelBooking.setVisibility(View.GONE);
                        btnPaynow.setVisibility(View.GONE);
                        tvBookingStatus.setText(getString(R.string.txt_1_4_booking_status) + ": " + getString(R.string.txt_6_3_1_booking_status_cancel));
                        if (userBookingForm.isPrepay()) {
                            tvPaymentStatus.setText(getString(R.string.txt_1_4_payment_status) + ": " + getString(R.string.txt_6_3_1_paid));
                        } else {
                            tvPaymentStatus.setText(getString(R.string.txt_1_4_payment_status) + ": " + getString(R.string.msg_3_9_payment_unpaid));
                        }
                    } else {
                        //Toast.makeText(ReservationDetailActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                        //Change Display popup
                        Dialag.getInstance().show(ReservationDetailActivity.this, false, false, true, null, response.body().getMessage(), getString(R.string.ok), null, null, Dialag.BTN_LEFT, new CallbackDialag() {
                            @Override
                            public void button1() {

                            }

                            @Override
                            public void button2() {

                            }

                            @Override
                            public void button3(Dialog dialog) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
            }
        });
    }

    private void filData() {

         tvHotelTitle.setText(userBookingForm.getHotelName());
        tvAddress.setText(getString(R.string.address) + ": " + userBookingForm.getHotelAddress());
        tvRoomType.setText(getString(R.string.room_type) + ": " + userBookingForm.getRoomTypeName());
        String bookingType = "";
        if (userBookingForm.getType() == BookingType.Hourly.getType()) {
            bookingType = getString(R.string.hourly);
        } else if (userBookingForm.getType() == BookingType.Daily.getType()) {
            bookingType = getString(R.string.daily);
        }
        if (userBookingForm.getType() == BookingType.Overnight.getType()) {
            bookingType = getString(R.string.over_night);
        }
        tvBookingType.setText(getString(R.string.booking_type) + ": " + bookingType);

        //Check in Code
        String checkinCode = userBookingForm.getCheckinCode();
        if (checkinCode != null && !checkinCode.equals("")) {
            tvCheckinCode.setTextColor(ContextCompat.getColor(this, R.color.org));
            tvCheckinCode.setText(getString(R.string.txt_6_3_1_checkin_code) + " " + checkinCode); //Server 78
        } else {
            tvCheckinCode.setTextColor(ContextCompat.getColor(this, R.color.bk));
            tvCheckinCode.setText(getString(R.string.txt_6_3_1_checkin_code) + " " + getString(R.string.txt_6_3_1_checkin_code_message)); //Server 78
        }

        try {
            Date date = apiFormat.parse(userBookingForm.getCheckInDatePlan());
            String endDate = "";
            // check if booking type = daily
            if (userBookingForm.getType() == 3) {
                if (userBookingForm.getEndDate() != null && !userBookingForm.getEndDate().equals("")) {
                    endDate = "~ " + viewFormat.format(apiFormat.parse(userBookingForm.getEndDate()));
                }
            }
            tvBookingDate.setText(getString(R.string.booking_date) + ": " + viewFormat.format(date) + endDate);
        } catch (Exception e) {
        }


        tvBookingTime.setText(getString(R.string.booking_time) + ": " + userBookingForm.getStartTime() + "~" + userBookingForm.getEndTime());

        if (userBookingForm.getCheckInTime() != null && !userBookingForm.getCheckInTime().equals("")) {
            tvCheckinTime.setText(getString(R.string.check_in_time) + ": " + userBookingForm.getCheckInTime());
        } else {
            tvCheckinTime.setText(getString(R.string.check_in_time) + ": N/A");
        }
        tvCheckoutTime.setText(getString(R.string.check_out_time) + ": N/A");

        //price
        tvPrice.setText(getString(R.string.price) + ": " + Utils.formatCurrency(userBookingForm.getTotalAmount()) + getString(R.string.currency));

        //Discount
        if (userBookingForm.getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
            tvCouponDiscount.setText(getString(R.string.txt_1_4_coupon_discount) + ": " + userBookingForm.getDiscount() + getString(R.string.percent));
        } else {
            tvCouponDiscount.setText(getString(R.string.txt_1_4_coupon_discount) + ": " + Utils.formatCurrency(userBookingForm.getDiscount()) + getString(R.string.currency));
        }

        //Stamp
        tvStampDiscount.setText(getString(R.string.txt_6_12_stamp_value) + ": " + Utils.formatCurrency(userBookingForm.getRedeemValue()) + getString(R.string.currency));



        //payment
        tvTotalPayment.setText(getString(R.string.txt_1_4_total_payment) + ": " + Utils.formatCurrency(userBookingForm.getAmountFromUser()) + getString(R.string.currency));


        /*
        * Check payment Status
         */
        if (userBookingForm.getPrepayAmount() > 0) {
            //Payment Online
            tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency)); //Server 78
        } else {
            //Unpaid
            if (!userBookingForm.isInPast()) {

                /*
                * Check min price and trial hotel
                */

                if (userBookingForm.getAmountFromUser() >= minPrice) {
                    //Check trial
                    if (userBookingForm.getHotelStatus() == ContractType.TRIAL.getType()) {
                        // Hide btn payment, status: unpaid
                        tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " +Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
                        btnPaynow.setVisibility(View.GONE);
                    } else {
                        //Show btn payment, enable payment
                        //1:Both, 2:Pay at hotel, 3:Pay online ,
                        if (userBookingForm.getPaymentOption() == 2) {
                            //Hide btn Payment
                            tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
                            btnPaynow.setVisibility(View.GONE);
                        } else {
                            //Show btn Paynow
                            tvPaymentStatus.setText(getString(R.string.txt_1_4_payment_status) + ": ");
                            btnPaynow.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    // Hide btn payment, status: unpaid
                    tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
                    btnPaynow.setVisibility(View.GONE);
                }

            } else {
                tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
                btnPaynow.setVisibility(View.GONE);
            }
        }

        /*
        * Set Booking Status 1: Booked, 2:Check-in. 3:Canceled ,
         */
        if (userBookingForm.getBookingStatus() == 1) {
            tvBookingStatus.setText(getString(R.string.txt_1_4_booking_status) + ": " + getString(R.string.txt_6_3_1_booking_status_booked));
            btnCancelBooking.setVisibility(View.VISIBLE);
        } else if (userBookingForm.getBookingStatus() == 2) {
            /*
            * Check-in and change payment status Paid at Hotel
            */
            tvBookingStatus.setText(getString(R.string.txt_1_4_booking_status) + ": " + getString(R.string.txt_6_3_1_booking_checked_in));
            //Payment Status
            if (!userBookingForm.isPrepay()) {
                tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
            } else {
                tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
            }
            btnPaynow.setVisibility(View.GONE);

            btnCancelBooking.setVisibility(View.GONE);
            /*
            * update payment staus again (Check-in successfull)
            */

            if (userBookingForm.isInPast()) {
                if (!userBookingForm.isPrepay()) {
                    btnPaynow.setVisibility(View.GONE);

                    //Update payment for check-in
                    tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
                }
            }
        } else if (userBookingForm.getBookingStatus() == 3) {
            tvBookingStatus.setText(getString(R.string.txt_1_4_booking_status) + ": " + getString(R.string.txt_6_3_1_booking_status_cancel));
            btnPaynow.setVisibility(View.GONE);
            btnCancelBooking.setVisibility(View.GONE);
            if (!userBookingForm.isPrepay()) {
                tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
                btnPaynow.setVisibility(View.GONE);
            }
        } else {
            tvBookingStatus.setText(getString(R.string.txt_6_3_1_paid) + ": " + getString(R.string.txt_6_3_1_booking_now_show));
            btnPaynow.setVisibility(View.GONE);
            btnCancelBooking.setVisibility(View.GONE);
            if (!userBookingForm.isPrepay()) {
                tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
                tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
            }
        }

        if (userBookingForm.isInPast()) {
            btnCancelBooking.setVisibility(View.GONE);
            btnPaynow.setVisibility(View.GONE);
            if (userBookingForm.isPrepay()) {
                tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
            } else {
                tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
            }

        }

        /*
        / Check minPrice
        */
        if (userBookingForm.getAmountFromUser() <= 0) {
            btnPaynow.setVisibility(View.GONE);
            tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                tvPaymentStatus.setText(getString(R.string.txt_6_3_1_paid_amount) + ": " + Utils.formatCurrency(userBookingForm.getPrepayAmount()) + getString(R.string.currency));
                btnPaynow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SSetHistoryDetail";
    }
}
