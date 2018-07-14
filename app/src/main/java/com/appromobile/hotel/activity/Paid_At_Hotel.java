package com.appromobile.hotel.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Paid_At_Hotel extends BaseActivity implements View.OnClickListener {
    private ImageView imgBack;
    private TextView txtTitle, txtName, txtPhone, txtFee, txtTotal, txtConfirm;
    private Bundle bundle;
    private int minPrice;
    private String FLAG_SHOW_REWARD_CHECKIN = "FLAG_SHOW_REWARD_CHECKIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid__at__hotel);
        setScreenName();
        Init();
    }

    private void Init() {
        bundle = getIntent().getBundleExtra("InformationBilling");

        imgBack =  findViewById(R.id.imageView_pay_at_hotel_back);
        imgBack.setOnClickListener(this);
        txtConfirm =  findViewById(R.id.textView_pay_at_hotel_confirm);
        txtConfirm.setOnClickListener(this);
        txtName =  findViewById(R.id.textView_pay_at_hotel_name);
        txtPhone =  findViewById(R.id.textView_pay_at_hotel_phone);
        txtFee =  findViewById(R.id.textView_pay_at_hotel_fee);
        txtTotal =  findViewById(R.id.textView_pay_at_hotel_total);

        //get minPrice
        if (HotelApplication.apiSettingForm != null) {
            minPrice = HotelApplication.apiSettingForm.getMinMoney();
        } else {
            ControllerApi.getmInstance().findApiSetting(this, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    ApiSettingForm apiSettingForm = (ApiSettingForm) object;
                    minPrice = apiSettingForm.getMinMoney();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setView();
    }

    @Override
    public void setScreenName() {
        this.screenName = "SPaidAtHotel";
    }

    private void setView() {
        if (bundle != null) {
            txtName.setText(PreferenceUtils.getAppUser(this).getNickName());
            txtPhone.setText(PreferenceUtils.getAppUser(this).getMobile());
            txtFee.setText(Utils.formatCurrency(bundle.getInt("totalFee")) + " VNĐ");
            txtTotal.setText(Utils.formatCurrency(bundle.getInt("total")) + " VNĐ");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_pay_at_hotel_back:
                onBackPressed();
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
                finish();
                break;
            case R.id.textView_pay_at_hotel_confirm:
                create_Reservation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
        finish();
    }

    private void create_Reservation() {
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

                    /*

                        Check minPrice and trial hotel ===> booking successful

                    */


                    if (bundle.getInt("total") < minPrice || bundle.getString("HOTEL_PAYMENT").equals("2") || bundle.getInt("HOTEL_STATUS") == ContractType.TRIAL.getType()) {
                        bookingSuccessfull(restResult.getSn());
                    } else {
                        //go to Booking Successfully
                        Intent confirm = new Intent(Paid_At_Hotel.this, Booking_Successful.class);
                        confirm.putExtra("otherInfo", restResult.getOtherInfo());
                        confirm.putExtra("userBookingSn", restResult.getSn());
                        confirm.putExtra("total", bundle.getInt("total"));
                        confirm.putExtra("METHOD_PAYMENT", bundle.getString("METHOD_PAYMENT", "default"));
                        startActivity(confirm);

                        if (HotelDetailActivity.hotelDetailActivity != null) {
                            HotelDetailActivity.hotelDetailActivity.finish();
                        }

                        if (ReservationActivity.reservation != null) {
                            ReservationActivity.reservation.finish();
                        }
                        if (Billing_Information.billingInformation != null) {
                            Billing_Information.billingInformation.finish();
                        }

                        finish();
                        overridePendingTransition(R.anim.right_to_left, R.anim.stable);

                    }
                }
            });

        }
    }

    /*
    /   Show dialog bookingSuccessfull
    */
    private void bookingSuccessfull(final int userBookingSn) {

        //super.onBackPressed();
        DialogUtils.showDialogReservationSuccessful(this, getString(R.string.msg_3_9_booking_successful), new DialogCallback() {
            @Override
            public void finished() {
                //show loading
                DialogUtils.showLoadingProgress(Paid_At_Hotel.this, false);
                //call Api
                // true = get reware coupon information
                // thanh toán xong mới gọi
                ControllerApi.getmInstance().findUserBookingDetail(Paid_At_Hotel.this, userBookingSn, true, new ResultApi() {
                    @Override
                    public void resultApi(Object object) {
                        //hide loading
                        DialogUtils.hideLoadingProgress();
                        //result
                        UserBookingForm userBookingForm = (UserBookingForm) object;

                        Intent detail = new Intent(Paid_At_Hotel.this, ReservationDetailActivity.class);
                        detail.putExtra(FLAG_SHOW_REWARD_CHECKIN, true);
                        detail.putExtra("UserBookingForm", userBookingForm);
                        startActivity(detail);

                        if (HotelDetailActivity.hotelDetailActivity != null) {
                            HotelDetailActivity.hotelDetailActivity.finish();
                        }

                        if (ReservationActivity.reservation != null) {
                            ReservationActivity.reservation.finish();
                        }
                        if (Billing_Information.billingInformation != null) {
                            Billing_Information.billingInformation.finish();
                        }

                        finish();
                        overridePendingTransition(R.anim.right_to_left, R.anim.stable);

                    }
                });
            }
        });
    }

}
