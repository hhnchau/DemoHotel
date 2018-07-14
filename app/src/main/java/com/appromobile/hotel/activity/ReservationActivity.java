package com.appromobile.hotel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appromobile.hotel.HotelApplication;
import com.appromobile.hotel.R;
import com.appromobile.hotel.adapter.CalendarAdapter;
import com.appromobile.hotel.adapter.CellDayClickListener;
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.clock.CallbackClock;
import com.appromobile.hotel.clock.Clock;
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.CallbackInput;
import com.appromobile.hotel.dialog.Dialag;
import com.appromobile.hotel.dialog.DialogInput;
import com.appromobile.hotel.enums.BookingMode;
import com.appromobile.hotel.enums.BookingType;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.enums.CouponStatus;
import com.appromobile.hotel.model.request.UserBookingDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.AppUserForm;
import com.appromobile.hotel.model.view.CouponConditionForm;
import com.appromobile.hotel.model.view.CouponIssuedForm;
import com.appromobile.hotel.model.view.HotelDetailForm;
import com.appromobile.hotel.model.view.MileageRewardForm;
import com.appromobile.hotel.model.view.PaymentInfoForm;
import com.appromobile.hotel.model.view.ReservationSetting;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.RoomTypeForm;
import com.appromobile.hotel.model.view.UserStampForm;
import com.appromobile.hotel.utils.DialogCallback;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.PreferenceUtils;
import com.appromobile.hotel.utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsConstants;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xuan on 7/12/2016.
 */

//ReservationDetailActivity
//Multi_Recent_Booking_Adapter

// ReservationActivity--->billingInformation--->Paid_At_Hotel--->Booking_Successful--->ReservationDetailActivity
//                                      --->BrowserPaymentActivity---->ReservationDetailActivity

public class ReservationActivity extends BaseActivity implements View.OnClickListener {
    private static final int CHOOSE_ROOM_TYPE = 1000;
    private static final int CHOOSE_COUPON = 1001;
    private static final int LOGIN_RESERVATION_REQUEST = 1002;
    private static final int LOGIN_COUPON_REQUEST = 1003;
    private static final int PAYMENT_REQUEST = 1004;
    private ImageView chkHourly, chkDaily, chkOvernight;
    private TextView txtHourly, txtDaily, txtOvernight;
    private LinearLayout btnReservation;
    private TextView tvHotelName, tvAddress, tvRoomName, tvDateHourly, tvTimeFrom, tvTimeTo, tvDateFrom, tvDateTo, tvCoupon, tvFee, tvDateOvernight;
    private LinearLayout boxHourly, boxDaily, boxOvernight;

    private TextView txtToday, txtTomorrow, txtOther, txtOvernightToday, txtOvernightTomorrow, txtOvernightOther, txtDailyFromToday, txtDailyFromTomorrow, txtDailyFromOther, txtDailyToToday, txtDailyToTomorrow, txtDailyToOther;
    private LinearLayout boxOther, boxOvernightOther, boxDailyFromOther, boxDailyToOther;
    private ImageView imgDateOvernight;
    private HotelDetailForm hotelDetailForm;

    private TextView floatButtonLogin, floatButtonPoint;

    private int roomtypeIndex = 0;
    private RoomTypeForm roomTypeForm;

    private BookingMode bookingMode = BookingMode.HOURLY;

    private SimpleDateFormat dateFormatter;
    private TextView btnChooseCoupon, btnChangeCoupon;
    private ArrayList<CouponIssuedForm> couponIssuedForms;
    private TextView btnClearCoupon;
    private int couponIndex = 0;
    private LinearLayout boxCoupon;
    private int firstHour; //startHour, endHour,
    private boolean guiState = true;

    private int minPrice;
    private int totalFee = 0;

    private Bundle bundle;
    public static final int NO_COUPON = -1;
    public static Activity reservation;
    private int startOverNight, endOvernight;

    private boolean statusRightNow;
    private boolean statusDaily = false;
    //For 1 Toast
    private boolean isToast = true;

    private boolean isLogIn = false;

    private static final int VALUE_POINT = 10000;
    private int numPoint = 0;
    private int myPoint = 0;
    private int pointValue = 0;


    private LinearLayout boxStamp;
    private TextView tvValueStamp, tvApplyStamp, btnNumberStamp, btnRedeem, btnNotApply;
    private UserStampForm userStampForm;
    private int redeemValue = 0;
    private boolean autoRedeem = false;

    private int systemHour;
    private String systemDate;
    private boolean isCurrentDay;

    private int lockTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName();

        bundle = new Bundle();
        reservation = this;

        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            MyLog.writeLog("ReservationActivity------------------->" + e);
        }

        setContentView(R.layout.reservation_activity);
        dateFormatter = new SimpleDateFormat(getString(R.string.date_format_view), Locale.ENGLISH);

        /*
        * Get Action
        */
        String action = getIntent().getAction();
        if (action != null && action.equals("Stamp")) {
            //Flag --> No Use Coupon
            autoRedeem = true;
            redeemValue = 1;
        }

        /*
        * Get intent
        */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            hotelDetailForm = bundle.getParcelable("HotelDetailForm");
            if (hotelDetailForm != null) {
                roomtypeIndex = bundle.getInt("RoomTypeIndex", 0);
                //Get RoomTypeForm
                roomTypeForm = hotelDetailForm.getRoomTypeList().get(roomtypeIndex);

                btnReservation = findViewById(R.id.btnMakeReservation);
                floatButtonLogin = findViewById(R.id.float_button_login);
                floatButtonLogin.setOnClickListener(this);
                floatButtonPoint = findViewById(R.id.float_button_point);
                floatButtonPoint.setOnClickListener(this);
                floatButtonPoint.setVisibility(View.GONE);
                boxCoupon = findViewById(R.id.boxCoupon);
                chkHourly = findViewById(R.id.chkHourly);
                txtHourly = findViewById(R.id.txt_hourly);
                chkDaily = findViewById(R.id.chkDaily);
                txtDaily = findViewById(R.id.txt_daily);
                chkOvernight = findViewById(R.id.chkOvernight);
                txtOvernight = findViewById(R.id.txt_overnight);
                btnClearCoupon = findViewById(R.id.btnClearCoupon);
                tvHotelName = findViewById(R.id.tvHotelName);
                tvAddress = findViewById(R.id.tvAddress);
                tvRoomName = findViewById(R.id.tvRoomName);
                tvDateHourly = findViewById(R.id.tvDateHourly);
                tvTimeFrom = findViewById(R.id.tvTimeFrom);
                tvTimeTo = findViewById(R.id.tvTimeTo);
                tvDateFrom = findViewById(R.id.tvDateFrom);//
                tvDateTo = findViewById(R.id.tvDateTo);//
                tvCoupon = findViewById(R.id.tvCoupon);
                tvDateOvernight = findViewById(R.id.tvDateOvernight);
                tvFee = findViewById(R.id.tvFee);
                boxHourly = findViewById(R.id.boxHourly);
                boxDaily = findViewById(R.id.boxDaily);
                boxOvernight = findViewById(R.id.boxOvernight);
                btnChooseCoupon = findViewById(R.id.btnChooseCoupon);
                btnChangeCoupon = findViewById(R.id.btnChangeCoupon);

                //Hourly
                txtToday = findViewById(R.id.textView_rerservation_today);
                txtToday.setOnClickListener(this);
                txtTomorrow = findViewById(R.id.textView_rerservation_tomorrow);
                txtTomorrow.setOnClickListener(this);
                txtOther = findViewById(R.id.textView_rerservation_other);
                txtOther.setOnClickListener(this);
                boxOther = findViewById(R.id.box_reservation_other);
                boxOther.setOnClickListener(this);

                //Overnight
                txtOvernightToday = findViewById(R.id.textView_rerservation_overnight_today);
                txtOvernightToday.setOnClickListener(this);
                txtOvernightTomorrow = findViewById(R.id.textView_rerservation_overnight_tomorrow);
                txtOvernightTomorrow.setOnClickListener(this);
                txtOvernightOther = findViewById(R.id.textView_rerservation_overnight_other);
                txtOvernightOther.setOnClickListener(this);
                boxOvernightOther = findViewById(R.id.box_reservation_overnight_other);
                boxOvernightOther.setOnClickListener(this);
                imgDateOvernight = findViewById(R.id.btnDateOvernight);
                imgDateOvernight.setOnClickListener(this);

                //Daily
                txtDailyFromToday = findViewById(R.id.textView_rerservation_daily_from_today);
                txtDailyFromToday.setOnClickListener(this);
                txtDailyFromTomorrow = findViewById(R.id.textView_rerservation_daily_from_tomorrow);
                txtDailyFromTomorrow.setOnClickListener(this);
                txtDailyFromOther = findViewById(R.id.textView_rerservation_daily_from_other);
                txtDailyFromOther.setOnClickListener(this);
                boxDailyFromOther = findViewById(R.id.box_reservation_daily_from_other);
                boxDailyFromOther.setOnClickListener(this);

                txtDailyToToday = findViewById(R.id.textView_rerservation_daily_to_today);
                txtDailyToToday.setOnClickListener(this);
                txtDailyToTomorrow = findViewById(R.id.textView_rerservation_daily_to_tomorrow);
                txtDailyToTomorrow.setOnClickListener(this);
                txtDailyToOther = findViewById(R.id.textView_rerservation_daily_to_other);
                txtDailyToOther.setOnClickListener(this);
                boxDailyToOther = findViewById(R.id.box_reservation_daily_to_other);
                boxDailyToOther.setOnClickListener(this);

                findViewById(R.id.btnChangeHotel).setOnClickListener(this);
                findViewById(R.id.btnClose).setOnClickListener(this);
                findViewById(R.id.btnChangeRoomType).setOnClickListener(this);
                //findViewById(R.id.btnDateHourly).setOnClickListener(this);
                findViewById(R.id.btnTimeFrom).setOnClickListener(this);   //**  From  **
                findViewById(R.id.btnTimeTo).setOnClickListener(this);      //**  To  **
                findViewById(R.id.btnDateFrom).setOnClickListener(this);
                findViewById(R.id.btnDateTo).setOnClickListener(this);
                findViewById(R.id.btnMakeReservation).setOnClickListener(this);
                findViewById(R.id.btnChooseCoupon).setOnClickListener(this);
                btnChangeCoupon.setOnClickListener(this);

                //Stamp
                boxStamp = findViewById(R.id.boxStamp);
                tvValueStamp = findViewById(R.id.tvValueStamp);
                tvApplyStamp = findViewById(R.id.tvApplyStamp);
                btnNumberStamp = findViewById(R.id.btnNumberStamp);
                btnRedeem = findViewById(R.id.btnRedeem);
                btnNotApply = findViewById(R.id.btnNotApply);

                //Clear Coupon
                btnClearCoupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvCoupon.setText(getString(R.string.choose_coupon));
                        btnClearCoupon.setVisibility(View.GONE);
                        btnChooseCoupon.setVisibility(View.VISIBLE);
                        btnChangeCoupon.setVisibility(View.GONE);
                        couponIndex = -1;
                        MyLog.writeLog("Calculate 299");
                        calculateFee();
                    }
                });

                //
                //Click Hourly
                //
                chkHourly.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (bookingMode != BookingMode.HOURLY) {

                            bookingModeHourly();

                            getCoupon();

                        }
                        MyLog.writeLog("Calculate 285");
                        calculateFee();
                    }
                });


                //
                //Click Overnight
                //
                chkOvernight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (bookingMode != BookingMode.OVERNIGHT) {

                            //Mode Overnight
                            bookingModeOvernight();

                            getCoupon();

                        }
                        MyLog.writeLog("Calculate 340");
                        calculateFee();
                    }
                });


                //
                //Click Daily
                //
                chkDaily.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (bookingMode != BookingMode.DAILY) {

                            bookingModeDaily();

                            statusDaily = false;

                            systemDate = getSystemDay();

                            tvDateFrom.setText(systemDate);

                            getCoupon();

                        }
                        MyLog.writeLog("Calculate 321");
                        calculateFee();
                    }
                });

                //Stamp Click
                //Redeem
                btnRedeem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (couponIndex == NO_COUPON) {

                            if (userStampForm.getNumStampActive() >= userStampForm.getNumToRedeem()) {
                                if (bookingMode == BookingMode.HOURLY && !userStampForm.isRedeemHourly()) {
                                    Toast.makeText(ReservationActivity.this, getString(R.string.msg_6_12_stamp_not_condition), Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (bookingMode == BookingMode.OVERNIGHT && !userStampForm.isRedeemOvernight()) {
                                    Toast.makeText(ReservationActivity.this, getString(R.string.msg_6_12_stamp_not_condition), Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (bookingMode == BookingMode.DAILY && !userStampForm.isRedeemDaily()) {
                                    Toast.makeText(ReservationActivity.this, getString(R.string.msg_6_12_stamp_not_condition), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                setViewStamp(userStampForm);

                                redeemValue = userStampForm.getRedeemValue();
                                btnNotApply.setVisibility(View.VISIBLE);
                                btnNumberStamp.setVisibility(View.GONE);
                                btnRedeem.setVisibility(View.GONE);
                                MyLog.writeLog("Calculate 368");
                                calculateFee();

                            }

                        } else {
                            Toast.makeText(ReservationActivity.this, getString(R.string.msg_6_12_stamp_coupon_is_using), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //Delete Redeem
                btnNotApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (redeemValue > 0) {
                            //Flag Delete Stamp
                            redeemValue = 0;
                            btnNotApply.setVisibility(View.GONE);
                            btnNumberStamp.setVisibility(View.GONE);
                            btnRedeem.setVisibility(View.VISIBLE);
                            //Check again
                            MyLog.writeLog("Calculate 387");
                            calculateFee();

                            deleteStamp();
                        }
                    }
                });

                tvDateFrom.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Calendar minDate = Calendar.getInstance();
                        try {
                            Date date = dateFormatter.parse(tvDateFrom.getText().toString());
                            minDate.setTimeInMillis(date.getTime());
                            minDate.add(Calendar.DATE, 1);
                            tvDateTo.setText(dateFormatter.format(minDate.getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            MyLog.writeLog("tvDateFrom--------------->" + e);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                tvTimeTo.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int start1 = parseTime(tvTimeFrom.getText().toString());
                        int end1 = parseTime(tvTimeTo.getText().toString());
                        if (end1 - start1 < firstHour) {
                            if (guiState) {
                                guiState = hideBtnReservation();
                            }
                        } else {
                            if (!guiState) {
                                guiState = displayGUI();
                            }
                        }
                        //MyLog.writeLog("Calculate 485");
                        //calculateFee();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                /*
                / Get MinPrice
                */
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

                /*
                /Get time overnight from Server- Store startOverNight
                */
                findOverNightHour();

                /*
                / initDataMyPageFragment Move ---> findOverNightHour
                */
                //initDataMyPageFragment();
            }
        }

    }

    private void checkLogIn() {
        if (!PreferenceUtils.getToken(this).equals("")) {
            floatButtonLogin.setVisibility(View.GONE);
            isLogIn = true;
        } else {
            floatButtonLogin.setVisibility(View.VISIBLE);
            isLogIn = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkLogIn();

        if (btnRedeem.getVisibility() == View.VISIBLE)
            redeemValue = 0;

    }

    /*
        / GetStamp
        */
    private void findUserStampDetail(long sn) {
        if (!isLogIn) {
            boxStamp.setVisibility(View.VISIBLE);
            tvValueStamp.setText(getString(R.string.txt_3_9_sign_up_stamp));
            btnRedeem.setVisibility(View.GONE);
            btnNotApply.setVisibility(View.GONE);
            btnNumberStamp.setVisibility(View.GONE);
            return;
        }

        //IsLogIn
        ControllerApi.getmInstance().findUserStampFormDetail(this, sn, false, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                userStampForm = (UserStampForm) object;
                if (userStampForm == null || userStampForm.getNumToRedeem() <= 0) {
                    boxStamp.setVisibility(View.GONE);
                } else {

                    boxStamp.setVisibility(View.VISIBLE);

                    //Stamp V3
                    if (userStampForm.getRedeemType() == ParamConstants.DISCOUNT_PERCENT) {
                        tvValueStamp.setText(Utils.formatCurrency(userStampForm.getRedeemValue()) + " " + getString(R.string.percent) + " - " + getString(R.string.max_discount) + " " + Utils.formatCurrency(userStampForm.getMaxRedeem()) + getString(R.string.vnd));
                    } else {
                        tvValueStamp.setText(Utils.formatCurrency(userStampForm.getRedeemValue()) + " " + getString(R.string.vnd));
                    }

                    StringBuilder stringBuilder = new StringBuilder().append(getString(R.string.apply)).append(": ");
                    stringBuilder.append(userStampForm.isRedeemHourly() ? getString(R.string.txt_2_flashsale_hourly_price) + ", " : "")
                            .append(userStampForm.isRedeemOvernight() ? getString(R.string.txt_2_flashsale_overnight_price) + ", " : "")
                            .append(userStampForm.isRedeemDaily() ? getString(R.string.txt_2_flashsale_daily_price) + ", " : "");
                    tvApplyStamp.setText(stringBuilder.substring(0, stringBuilder.length() - 2));

                    //Check Stamp Intent
                    if (autoRedeem) {
                        //Change Mode
                        changeModeBookingForStamp(userStampForm);
                        //No Coupon
                        couponIndex = NO_COUPON;
                        tvCoupon.setText(getString(R.string.choose_coupon));
                        btnClearCoupon.setVisibility(View.GONE);
                        if (couponIndex == NO_COUPON) {
                            boxCoupon.setVisibility(View.GONE);
                        } else {
                            boxCoupon.setVisibility(View.VISIBLE);
                        }

                        btnChooseCoupon.setVisibility(View.VISIBLE);
                        btnChangeCoupon.setVisibility(View.GONE);
                        //Set Auto Redeem
                        redeemValue = userStampForm.getRedeemValue();
                        btnNotApply.setVisibility(View.VISIBLE);
                        btnRedeem.setVisibility(View.GONE);
                        btnNumberStamp.setVisibility(View.GONE);
                        //Calculate
                        MyLog.writeLog("Calculate 557");
                        calculateFee();
                        return;
                    }

                    int numActive = userStampForm.getNumStampActive();
                    int numRedeem = userStampForm.getNumToRedeem();
                    if (numActive >= numRedeem) {
                        btnRedeem.setVisibility(View.VISIBLE);
                        btnNotApply.setVisibility(View.GONE);
                        btnNumberStamp.setVisibility(View.GONE);
                    } else {
                        btnRedeem.setVisibility(View.GONE);
                        btnNotApply.setVisibility(View.GONE);
                        btnNumberStamp.setVisibility(View.VISIBLE);
                        btnNumberStamp.setText(String.valueOf(numActive) + "/" + String.valueOf(numRedeem));
                    }

                    if (userStampForm.getNumStampLocked() > 0) {
                        btnRedeem.setVisibility(View.VISIBLE);
                        btnRedeem.setEnabled(false);
                        btnRedeem.setTextColor(getResources().getColor(R.color.lg));
                        btnRedeem.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
                    }
                }
            }
        });

    }

    //Return current hour
    private int getSystemHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    //Return current hour
    private int getSystemMin() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    //Reutrn current day
    private String getSystemDay() {
        Calendar cal = Calendar.getInstance();
        return dateFormatter.format(cal.getTime());
    }

    //Return Yesterday
    private String getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return dateFormatter.format(cal.getTime());
    }

    //Return Tomorrow
    private String getTomorrowDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return dateFormatter.format(cal.getTime());
    }

    private void calculateFee() {
        totalFee = 0;
        int totalHour = 0;

        /*
        / Type HOURS
        */
        if (bookingMode == BookingMode.HOURLY && guiState) {

            //get Time
            String[] timeFrom = tvTimeFrom.getText().toString().split(":");
            String[] timeTo = tvTimeTo.getText().toString().split(":");

            //Check length hour
            if (timeFrom.length == 0 || timeTo.length == 0) {
                return;
            }

            //Time To
            int iTimeTo = Integer.parseInt(timeTo[0]);

            if (iTimeTo == 0) {
                iTimeTo = 24;
            }

            //Time From
            int iTimeFrom = Integer.parseInt(timeFrom[0]);

            //Total hour
            totalHour = iTimeTo - iTimeFrom;

            //Check hour
            if (totalHour < 0) {
                totalFee = 0;
                guiState = hideBtnReservation();
                tvFee.setText(Utils.formatCurrency(totalFee) + " " + getString(R.string.currency));
                return;
            }

            /*
            / Get Info Room Type
            */
            if (totalHour > roomTypeForm.getFirstHours()) /*AdditionHours*/ {
                totalFee = roomTypeForm.getPriceFirstHours() + roomTypeForm.getBonusFirstHours() + getAdditionalHours(totalHour - roomTypeForm.getFirstHours());
            } else {
                totalFee = roomTypeForm.getPriceFirstHours() + roomTypeForm.getBonusFirstHours();
            }

            // Update UI
            int start1 = parseTime(tvTimeFrom.getText().toString());
            int end1 = parseTime(tvTimeTo.getText().toString());
            if (end1 - start1 < firstHour) {
                if (guiState) {
                    guiState = hideBtnReservation();
                }
            } else {
                if (!guiState) {
                    guiState = displayGUI();
                }
            }

            /*
            / DAILY
            */
        } else if (bookingMode == BookingMode.DAILY) {   // Type DAILY

            Calendar dateFrom = Calendar.getInstance();
            Calendar dateTo = Calendar.getInstance();
            try {
                Date date = dateFormatter.parse(tvDateFrom.getText().toString());
                dateFrom.setTimeInMillis(date.getTime());
                date = dateFormatter.parse(tvDateTo.getText().toString());
                dateTo.setTimeInMillis(date.getTime());

            } catch (ParseException e) {
                e.printStackTrace();
                MyLog.writeLog("Date date------------------->" + e);
            }

            int totalDays = Math.abs(daysBetween(dateFrom, dateTo));
            totalFee = roomTypeForm.getPriceOneDay() * totalDays;
            btnReservation.setVisibility(View.VISIBLE);

            /*
            / OVERNIGHT
            */
        } else if (bookingMode == BookingMode.OVERNIGHT) {   //  Type OVER NIGHT
            totalFee = roomTypeForm.getPriceOvernight();

            btnReservation.setVisibility(View.VISIBLE);
        }

        int feeDiscount = 0;

        /*
        / Check Coupon
        */
        if (couponIssuedForms != null && couponIssuedForms.size() > 0) {
            if (couponIndex >= 0) {

                feeDiscount = calculateDiscount(totalFee);

                //Check Hours, Day Coupon
                CouponConditionForm couponConditionForm = couponIssuedForms.get(couponIndex).getCouponConditionForm();
                if (couponConditionForm != null){

                    //Coupon Hourly
                    int hoursCoupon = couponConditionForm.getNumHours();
                    if (hoursCoupon > 0 && bookingMode == BookingMode.HOURLY){
                        if (totalHour > hoursCoupon){
                            int priceForHourlyDiscount = roomTypeForm.getPriceFirstHours() + roomTypeForm.getBonusFirstHours() + getAdditionalHours(hoursCoupon - roomTypeForm.getFirstHours());
                            feeDiscount = calculateDiscount(priceForHourlyDiscount);
                        }
                    }

                    //Coupon Daily
                    int daysCoupon = couponConditionForm.getNumDays();
                    if (daysCoupon > 0 && bookingMode == BookingMode.DAILY){
                        int priceForDailyDiscount = getAdditionalDays(daysCoupon);
                        feeDiscount = calculateDiscount(priceForDailyDiscount);

                    }

                }

                bundle.putInt("discountFee", feeDiscount);
            } else {
                bundle.putInt("discountFee", NO_COUPON);
            }
        } else {
            bundle.putInt("discountFee", NO_COUPON);
        }


        //Super Flash Sale
        int superSale = roomTypeForm.getGo2joyFlashSaleDiscount();
        if (superSale > 0)
            totalFee = totalFee - superSale;

        // TotalFee
        bundle.putInt("totalFee", totalFee);

        totalFee = totalFee - feeDiscount;

        //Set Stamp Apply
        if (redeemValue > 0 && userStampForm != null) {
            if (bookingMode == BookingMode.HOURLY && userStampForm.isRedeemHourly() ||
                    bookingMode == BookingMode.OVERNIGHT && userStampForm.isRedeemOvernight() ||
                    bookingMode == BookingMode.DAILY && userStampForm.isRedeemDaily()) {

                if (userStampForm.getRedeemType() == ParamConstants.DISCOUNT_PERCENT) {
                    redeemValue = (totalFee * userStampForm.getRedeemValue()) / 100;
                    if (redeemValue > userStampForm.getMaxRedeem()) {
                        redeemValue = userStampForm.getMaxRedeem();
                    }
                }
                totalFee = totalFee - redeemValue;
            }
        }
        //Add Redeem
        bundle.putInt("redeemValue", redeemValue);


        //Add Point
        totalFee = totalFee - pointValue;
        bundle.putInt("pointValue", pointValue);

        //Check total Fee
        if (bookingMode == BookingMode.HOURLY && !guiState) {
            int end1 = parseTime(tvTimeTo.getText().toString());
            if (end1 == 0) {
                //Set totalFee = 0;
                totalFee = 0;
                tvFee.setText(Utils.formatCurrency(totalFee) + " " + getString(R.string.currency));
            } else {

                //firstHour > 1
                if (firstHour > 1) {
                    tvFee.setText(String.format(getString(R.string.msg_3_9_booking_only), firstHour));
                }

            }
        } else {
            if (totalFee < 0)
                totalFee = 0;
            tvFee.setText(Utils.formatCurrency(totalFee) + " " + getString(R.string.currency));
        }

        // Total
        bundle.putInt("total", totalFee);

        //Facebook Event
        setEventFacebook(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, hotelDetailForm.getSn(), totalFee);

    }

    private int calculateDiscount(int _totalFee){
        int feeDiscount = 0;
        if (couponIssuedForms.get(couponIndex).getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
            //Discount Percent
            int percent = (couponIssuedForms.get(couponIndex).getDiscount() * _totalFee) / 100;
            int maxDiscount = couponIssuedForms.get(couponIndex).getMaxDiscount();
            //Check Coupon MaxDiscount
            if (maxDiscount == 0 || percent <= maxDiscount) {
                feeDiscount = percent;
            } else if (percent > maxDiscount) {
                feeDiscount = maxDiscount;
            }
        } else {
            // discount money
            if (roomTypeForm != null && roomTypeForm.isCinema()) {
                feeDiscount = couponIssuedForms.get(couponIndex).getCineDiscount();
            } else {
                feeDiscount = couponIssuedForms.get(couponIndex).getDiscount();
            }
        }

        return feeDiscount;
    }

    private int getAdditionalDays(int addition){
        return roomTypeForm.getPriceOneDay() * addition;
    }

    //Calculator Additional Hours
    private int getAdditionalHours(int addition) {
        int addHour;
        if (addition % roomTypeForm.getAdditionalHours() > 0) {
            addHour = ((addition / roomTypeForm.getAdditionalHours()) * roomTypeForm.getPriceAdditionalHours()) + roomTypeForm.getPriceAdditionalHours();
            if (roomTypeForm.isCinema()) {
                addHour = ((addition / roomTypeForm.getAdditionalHours()) * roomTypeForm.getBonusAdditionalHours()) + roomTypeForm.getBonusAdditionalHours();
            }
        } else {
            addHour = (addition / roomTypeForm.getAdditionalHours()) * roomTypeForm.getPriceAdditionalHours();
            if (roomTypeForm.isCinema()) {
                addHour = addHour + (addition / roomTypeForm.getAdditionalHours()) * roomTypeForm.getBonusAdditionalHours();
            }
        }
        return addHour;
    }

    public static int daysBetween(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }

    private UserBookingDto getUserBookingDto() {
        final UserBookingDto userBookingDto = new UserBookingDto();

        //RoomType
        userBookingDto.setRoomTypeSn(roomTypeForm.getSn());
        final SimpleDateFormat displayFormat = new SimpleDateFormat(getString(R.string.date_format_view), Locale.ENGLISH);
        final SimpleDateFormat requestFormat = new SimpleDateFormat(getString(R.string.date_format_request), Locale.ENGLISH);

        /*
        / HOURLY
        */
        if (bookingMode == BookingMode.HOURLY) {
            if (!tvDateTo.getText().toString().equals("00:00")) {
                //EndTime
                userBookingDto.setEndTime(tvTimeTo.getText().toString());
            }
            //StartTime
            userBookingDto.setStartTime(tvTimeFrom.getText().toString());


            try {
                Date date = displayFormat.parse(tvDateHourly.getText().toString());
                //Dateplan
                userBookingDto.setGetCheckInDatePlan(requestFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
                MyLog.writeLog("BookingMode.HOURLY------------------------->" + e);
            }
            //Type
            userBookingDto.setType(BookingType.Hourly.getType());

            /*
            / DAILY
            */

        } else if (bookingMode == BookingMode.DAILY) {
            try {
                Date date = displayFormat.parse(tvDateFrom.getText().toString());
                userBookingDto.setGetCheckInDatePlan(requestFormat.format(date));
                date = displayFormat.parse(tvDateTo.getText().toString());
                userBookingDto.setEndDate(requestFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
                MyLog.writeLog("BookingMode.DAILY------------------------->" + e);
            }
            userBookingDto.setType(BookingType.Daily.getType());

            /*
            / OVERNIGHT
            */

        } else if (bookingMode == BookingMode.OVERNIGHT) {
            try {
                Date date;
                if (statusRightNow && tvDateOvernight.getTag().equals("RightNow")) {
                    date = displayFormat.parse(getYesterday());
                } else {


                    date = displayFormat.parse(tvDateOvernight.getText().toString());
                }

                userBookingDto.setGetCheckInDatePlan(requestFormat.format(date));

                userBookingDto.setEndDate(requestFormat.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
                MyLog.writeLog("BookingMode.OVERNIGHT------------------------->" + e);
            }
            userBookingDto.setType(BookingType.Overnight.getType());
        }


        try {
            if (couponIssuedForms != null && couponIssuedForms.size() > 0) {
                if (couponIndex >= 0) {
                    //Coupon
                    userBookingDto.setCouponIssuedSn((long) couponIssuedForms.get(couponIndex).getSn());
                }
            }
        } catch (Exception e) {
            MyLog.writeLog("couponIssuedForms------------------------->" + e);
        }

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm != null) {
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            userBookingDto.setClientip(ip);
        }

        userBookingDto.setRedeemValue(redeemValue);

        return userBookingDto;
    }

    //Check PayInAdvance Get Message From Server
    private void checkPayInAdvance() {
        ControllerApi.getmInstance().checkPayInAdvance(this, getUserBookingDto(), new ResultApi() {
            @Override
            public void resultApi(Object object) {
                RestResult restResult = (RestResult) object;
                if (!restResult.getMessage().equals("")) {

                    Toast.makeText(ReservationActivity.this, restResult.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    private void getCoupon() {
        if (!isLogIn) {
            //Set View Not LogIn
            boxCoupon.setVisibility(View.VISIBLE);
            btnChooseCoupon.setVisibility(View.GONE);
            btnChangeCoupon.setVisibility(View.GONE);
            btnClearCoupon.setVisibility(View.GONE);
            tvCoupon.setText(getString(R.string.txt_3_9_sign_up_coupon));
            calculateFee();
            return;
        }

        //IsLogIn
        Map<String, Object> params = new HashMap<>();
        params.put("hotelSn", hotelDetailForm.getSn());
        params.put("offset", 0);
        params.put("limit", HotelApplication.LIMIT_REQUEST);

        int type = 0;
        String stayAtHotelDate = null, startTime = null, endTime = null;
        if (bookingMode == BookingMode.HOURLY) {
            type = BookingType.Hourly.getType();
            stayAtHotelDate = tvDateHourly.getText().toString();
            startTime = tvTimeFrom.getText().toString();
            endTime = tvTimeTo.getText().toString();
        } else if (bookingMode == BookingMode.OVERNIGHT) {
            type = BookingType.Overnight.getType();
            stayAtHotelDate = tvDateOvernight.getText().toString();
        } else if (bookingMode == BookingMode.DAILY) {
            type = BookingType.Daily.getType();
            stayAtHotelDate = tvDateFrom.getText().toString();
        }

        params.put("type", type);

        String date = Utils.formatDate(this, stayAtHotelDate);
        if (date != null) {
            params.put("stayAtHotelDate", date);
        }

        if (startTime != null) {
            params.put("startTime", startTime);
        }

        if (startTime != null) {
            params.put("endTime", endTime);
        }

        if (roomTypeForm != null) {
            params.put("roomTypeSn", roomTypeForm.getSn());
        }

        DialogUtils.showLoadingProgress(this, false);

        MyLog.writeLog("GetCoupon: ----->" + params.toString());

        HotelApplication.serviceApi.findLimitCouponListCanUsed(params, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<List<CouponIssuedForm>>() {
            @Override
            public void onResponse(Call<List<CouponIssuedForm>> call, Response<List<CouponIssuedForm>> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    couponIssuedForms = (ArrayList<CouponIssuedForm>) response.body();

                    if (couponIssuedForms != null && couponIssuedForms.size() > 0) {
                        //Check Coupon canUse
                        //1: Can use, 2:Hotel Not Accept, 3:Not enough condition to use
                        if (redeemValue == 0) {
                            for (int i = 0; i < couponIssuedForms.size(); i++) {
                                if (couponIssuedForms.get(i).getCanUse() == ParamConstants.CAN_USE &&
                                        couponIssuedForms.get(i).getUsed() != CouponStatus.Temp.ordinal()) {
                                    couponIndex = i;
                                    break;
                                } else {
                                    //Clear indexCoupon
                                    couponIndex = NO_COUPON;
                                }
                            }
                        }

                        // Check Again
                        if (couponIndex != NO_COUPON) {

                            /*
                            / Check Coupon Percent or Current
                            */
                            if (couponIssuedForms.get(couponIndex).getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
                                tvCoupon.setText(couponIssuedForms.get(couponIndex).getPromotionName() + "\n" + getString(R.string.discount) + " " + String.valueOf(Utils.formatCurrency(couponIssuedForms.get(couponIndex).getDiscount())) + " " + getString(R.string.percent));
                            } else {
                                if (roomTypeForm != null && roomTypeForm.isCinema()) {
                                    tvCoupon.setText(couponIssuedForms.get(couponIndex).getPromotionName() + "\n" + String.valueOf(Utils.formatCurrency(couponIssuedForms.get(couponIndex).getCineDiscount())) + getString(R.string.currency));
                                } else {
                                    tvCoupon.setText(couponIssuedForms.get(couponIndex).getPromotionName() + "\n" + String.valueOf(Utils.formatCurrency(couponIssuedForms.get(couponIndex).getDiscount())) + getString(R.string.currency));
                                }
                            }

                            //Show Coupon
                            boxCoupon.setVisibility(View.VISIBLE);

                            //Show btn Clear Coupon
                            btnClearCoupon.setVisibility(View.VISIBLE);

                        } else {
                            //No Coupon
                            tvCoupon.setText(getString(R.string.choose_coupon));
                            btnClearCoupon.setVisibility(View.GONE);
                            //Show Coupon
                            boxCoupon.setVisibility(View.VISIBLE);

                            btnChooseCoupon.setVisibility(View.VISIBLE);

                            btnChangeCoupon.setVisibility(View.GONE);
                        }

                    } else {
                        couponIndex = NO_COUPON;
                        btnChooseCoupon.setVisibility(View.GONE);
                        btnClearCoupon.setVisibility(View.INVISIBLE);
                        tvCoupon.setText("");
                        boxCoupon.setVisibility(View.GONE);
                    }

                    //Check PayInAdvance show Toast when open app
                    if (isToast) {
                        isToast = false;
                        checkPayInAdvance();
                    }

                    MyLog.writeLog("Calculate 1013");
                    calculateFee();

                } else if (response.code() == 401) {
                    DialogUtils.showExpiredDialog(ReservationActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(ReservationActivity.this, LoginActivity.class);
                            startActivityForResult(intent, LOGIN_COUPON_REQUEST);
                        }
                    });
                    tvCoupon.setText("");
                }
            }

            @Override
            public void onFailure(Call<List<CouponIssuedForm>> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                btnChooseCoupon.setVisibility(View.GONE);
                tvCoupon.setText(getString(R.string.dont_have_any_coupon_in_result));
                MyLog.writeLog("Calculate 1033");
                calculateFee();
            }
        });
    }

    private void findOverNightHour() {
        ControllerApi.getmInstance().findReservationSetting(ReservationActivity.this, hotelDetailForm.getSn(),
                new ResultApi() {
                    @Override
                    public void resultApi(Object object) {
                        ReservationSetting rs = (ReservationSetting) object;
                        if (rs != null) {
                            startOverNight = rs.getStartOvernight();
                            endOvernight = rs.getEndOvernight();


                            lockTime = Utils.convertTime(rs.getLockRoomTodayTime());


                            MileageRewardForm mileageRewardForm = rs.getMileageReward();
                            if (mileageRewardForm != null)
                                numPoint = mileageRewardForm.getNumPoint();
                        }

                        findAppUser();

                        checkLogIn();
                        //Init Data
                        initData();

                    }
                });
    }

    private void findAppUser() {
        ControllerApi.getmInstance().findAppUser(this, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                AppUserForm appUserForm = (AppUserForm) object;
                if (appUserForm != null) {
                    myPoint = appUserForm.getMileageAmount();

                    if (myPoint < numPoint || numPoint == 0)
                        floatButtonPoint.setVisibility(View.GONE);
                    else
                        floatButtonPoint.setVisibility(View.VISIBLE);
                    floatButtonPoint.setText(getString(R.string.txt_6_13_use_point));
                }
            }
        });
    }

    private String[] setupTime() {
        int hour = getSystemHour();
        int min = getSystemMin();


        String[] time = new String[2];
        String _starHour;
        String _endHour;
        if (min < 30) {
            _starHour = hour + ":30";
            _endHour = (hour + 1) + ":30";
        } else {
            _starHour = (hour + 1) + ":00";
            _endHour = (hour + 2) + ":00";
        }

        if (parseTime(_starHour) < 10) {
            _starHour = "0" + _starHour;
        }

        if (parseTime(_endHour) < 10) {
            _endHour = "0" + _endHour;
        }

        if (parseTime(_starHour) > 23) {
            _starHour = "00:00";
        }

        if (parseTime(_endHour) > 23) {
            _endHour = "00:00";
        }

        time[0] = _starHour;
        time[1] = _endHour;

        return time;
    }

    private int parseTime(String time) {
        String[] s = time.split(":");

        int t = -1;
        if (s.length > 1) {
            t = Integer.parseInt(s[0]);
        }
        return t;
    }

    private void initData() {
        MyLog.writeLog("ResevationActivity ---> initDataMyPageFragment");

        if (hotelDetailForm != null) {
            //Set View Hotel Name
            tvHotelName.setText(hotelDetailForm.getName());
            //Set View Hotel Address
            tvAddress.setText(hotelDetailForm.getAddress());
            //Set View Room Name
            //String roomName = hotelDetailForm.getRoomTypeList().get(roomtypeIndex).getName();
            String roomName = roomTypeForm.getName();
            if (roomName != null) {
                tvRoomName.setText(roomName);
            }
            //Set Firsthour
            firstHour = roomTypeForm.getFirstHours();
        }

        /*
        / Check Flash Sale
        */
        if (checkRoomTypeFlashSale()) {
            bookingModeOvernight();
            boxCoupon.setVisibility(View.GONE);
            setViewFlashSale();
        } else if (checkRoomTypeCinema()) {
            bookingModeHourly();
            setViewCinema();
            getCoupon();
        } else /*Normal*/ {
            bookingModeHourly();
            getCoupon();
        }

        MyLog.writeLog("Calculate 1180");
        calculateFee();

        /*
        / Set current day
        */
//        final Calendar calendar = Calendar.getInstance();
//        systemDate = getSystemDay();
//        if (checkRoomLock())
//            systemDate = getTomorrowDay();
//        tvDateHourly.setText(systemDate);
//
//        tvDateFrom.setText(systemDate);
//        tvDateOvernight.setText(systemDate);

        //calendar.add(Calendar.DATE, 1);
        //tvDateTo.setText(dateFormatter.format(calendar.getTime()));
          /*
          / GetStamp
         */
        if (!checkRoomTypeFlashSale()) {
            boxStamp.setVisibility(View.VISIBLE);
            findUserStampDetail(hotelDetailForm.getSn());
        } else {
            boxStamp.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.float_button_point:
                if (floatButtonPoint.getText().toString().equals(getString(R.string.txt_6_13_refund))) {
                    floatButtonPoint.setText(getString(R.string.txt_6_13_use_point));
                    pointValue = 0;
                    calculateFee();
                } else {
                    showDialogPoint();
                }
                break;
            case R.id.float_button_login:
                Intent login = new Intent(ReservationActivity.this, LoginActivity.class);
                startActivityForResult(login, LOGIN_COUPON_REQUEST);
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;
            case R.id.btnClose:
                finish();
                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;
            case R.id.btnChangeHotel:
                setResult(RESULT_OK);
                /*
                * btn Change Hotel
                */
                if (HotelPhotoDetailActivity.hotelPhotoDetailActivity != null) {
                    HotelPhotoDetailActivity.hotelPhotoDetailActivity.finish();
                }

                if (RateReviewDetailActivity.rateReviewDetailActivity != null) {
                    RateReviewDetailActivity.rateReviewDetailActivity.finish();
                }

                if (RateReviewListActivity.rateReviewListActivity != null) {
                    RateReviewListActivity.rateReviewListActivity.finish();
                }

                if (HotelDetailActivity.hotelDetailActivity != null) {
                    HotelDetailActivity.hotelDetailActivity.finish();
                }

                if (HotelPhotoRoomTypeDetailActivity.hotelPhotoRoomTypeDetailActivity != null) {
                    HotelPhotoRoomTypeDetailActivity.hotelPhotoRoomTypeDetailActivity.finish();
                }

                finish();

                overridePendingTransition(R.anim.stable, R.anim.left_to_right);
                break;
            case R.id.btnChangeRoomType:
                /*
                * btn Change room type
                */

                guiState = true;
                Intent intent = new Intent(this, ChooseRoomTypeActivity.class);
                intent.putExtra("HotelDetailForm", hotelDetailForm);
                intent.putExtra("selectedIndex", roomtypeIndex);
                startActivityForResult(intent, CHOOSE_ROOM_TYPE);
                overridePendingTransition(R.anim.hotel_slide_up, R.anim.stable);
                break;
            case R.id.btnTimeFrom:
                int start = parseTime(tvTimeFrom.getText().toString());

                if (start == 0 && isCurrentDay && systemHour == 23) {
                    Toast.makeText(ReservationActivity.this, getString(R.string.msg_3_9_overnight_time), Toast.LENGTH_LONG).show();
                } else {
                    showClock();
                }
                break;
            case R.id.btnTimeTo:

                int start1 = parseTime(tvTimeFrom.getText().toString());
                int end1 = parseTime(tvTimeTo.getText().toString());

                if (start1 == 0 && isCurrentDay && systemHour == 23) {
                    Toast.makeText(ReservationActivity.this, getString(R.string.msg_3_9_overnight_time), Toast.LENGTH_LONG).show();
                } else if (end1 == 24) {
                    showClock();
                } else {
                    showClock();
                }
                break;
            case R.id.btnMakeReservation:
                //if (hotelDetailForm.getRoomTypeList().get(roomtypeIndex).isLocked()) {
                //    Toast.makeText(ReservationActivity.this, getString(R.string.msg_3_1_soldout_room), Toast.LENGTH_LONG).show();
                //} else {
                if (!PreferenceUtils.getToken(this).equals("")) {
                    //Is LogIn
                    if (checkStampEnough(userStampForm) && redeemValue == 0 && !checkRoomTypeFlashSale()) {
                        showDialogStamp();
                    } else {
                        checkOtherCondition();
                    }
                } else {
                    //Not LogIn
                    if (bookingMode == BookingMode.HOURLY) {
                        int start2 = parseTime(tvTimeFrom.getText().toString());
                        if (checkOvernightHourly(start2)) {
                            showDialogInputPhone();
                        }
                    } else {
                        showDialogInputPhone();
                    }
                }
                //}

                break;
            case R.id.btnChangeCoupon:
                if (redeemValue > 0) {
                    Toast.makeText(reservation, getString(R.string.msg_6_12_stamp_redeemed), Toast.LENGTH_SHORT).show();
                } else {
                    changeCoupon();
                }
                break;
            case R.id.btnChooseCoupon:

                if (redeemValue > 0) {
                    Toast.makeText(reservation, getString(R.string.msg_6_12_stamp_redeemed), Toast.LENGTH_SHORT).show();
                } else {
                    btnChooseCoupon.setVisibility(View.GONE);
                    btnChangeCoupon.setVisibility(View.VISIBLE);
                    changeCoupon();
                }
                break;
            //TODAY
            case R.id.textView_rerservation_today:

                /*
                / Set Button
                */
                //initData();
                pressButtonToday();
                normalButtonTomorrow();
                normalButtonOther();

                checkHourlyReservation(getSystemDay());

                getCoupon();

                break;
            //TOMORROW
            case R.id.textView_rerservation_tomorrow:

                /*
                / Set Button
                */
                //initData();
                normalButtonToday();
                pressButtonTomorrow();
                normalButtonOther();

                checkHourlyReservation(getTomorrowDay());

                getCoupon();

                //Enable calculate
                guiState = true;

                /*
                / Lock Room
                */
                if (checkRoomLock()) {
                    if (lockTime < systemHour && systemHour < 24) {
                        inactiveButtonToday();
                        pressButtonTomorrow();
                        normalButtonOther();
                    }
                    if (systemHour > 0 && systemHour < lockTime) {
                        isCurrentDay = false;
                        inactiveButtonToday();
                        pressButtonTomorrow();
                        normalButtonOther();
                    }
                }

                break;
            //OTHER
            case R.id.textView_rerservation_other:

                /*
                / Set Button
                */
                //initData();
                normalButtonToday();
                normalButtonTomorrow();
                pressButtonOther();

                //Show Calendar
                changeDateHourly();

                //Enable calculate
                guiState = true;

                /*
                / Lock Room
                */
                if (checkRoomLock()) {
                    if (lockTime < systemHour && systemHour < 24) {
                        inactiveButtonToday();
                        normalButtonTomorrow();
                        pressButtonOther();
                    }
                    if (systemHour > 0 && systemHour < lockTime) {
                        isCurrentDay = false;

                        inactiveButtonToday();
                        normalButtonTomorrow();
                        pressButtonOther();
                    }
                }

                break;
            //OVERNIGHT TODAY
            case R.id.textView_rerservation_overnight_today:
                if (statusRightNow) {

                    tvDateOvernight.setText(getYesterday());
                    tvDateOvernight.setTag("RightNow");

                    /*
                    / Set Button
                    */
                    pressButtonOvernightToday();
                    normalButtonOvernightTomorrow();

                    //Flash Sale
                    if (!checkRoomTypeFlashSale()) {
                        normalButtonOvernightOther();

                        getCoupon();
                    }

                }
                break;
            //OVERNIGHT TOMORROW
            case R.id.textView_rerservation_overnight_tomorrow:

                tvDateOvernight.setText(getSystemDay());
                tvDateOvernight.setTag("Tonight");

                /*
                / Set Button
                */
                if (statusRightNow) {
                    normalButtonOvernightToday();
                } else {
                    inactiveButtonOvernightToday();
                }
                pressButtonOvernightTomorrow();

                //Check button Other
                if (checkRoomTypeFlashSale()) {
                    inactiveButtonOvernightOther();
                } else {
                    normalButtonOvernightOther();
                }

                //Flash Sale
                if (!checkRoomTypeFlashSale()) {
                    normalButtonOvernightOther();

                    getCoupon();
                }

                /*
                / Lock Room
                */
                if (checkRoomLock()) {
                    tvDateOvernight.setText(getTomorrowDay());
                    if (lockTime < systemHour && systemHour < 24) {
                        pressButtonOvernightToday();
                        inactiveButtonOvernightTomorrow();
                        normalButtonOvernightOther();
                    }
                    if (systemHour > 0 && systemHour < lockTime) {
                        inactiveButtonOvernightToday();
                        pressButtonOvernightTomorrow();
                        normalButtonOvernightOther();
                    }
                }


                break;
            //OVERNIGHT OTHER
            case R.id.textView_rerservation_overnight_other:

                /*
                / Set Button
                */
                if (statusRightNow) {
                    normalButtonOvernightToday();
                } else {
                    inactiveButtonOvernightToday();
                }
                normalButtonOvernightTomorrow();
                pressButtonOvernightOther();

                changeDateOvernight();
                tvDateOvernight.setTag("Other");

                /*
                / Lock Room
                */
                if (checkRoomLock()) {
                    tvDateOvernight.setText(getTomorrowDay());
                    if (lockTime < systemHour && systemHour < 24) {
                        inactiveButtonOvernightToday();
                        inactiveButtonOvernightTomorrow();
                        pressButtonOvernightOther();
                    }
                    if (systemHour > 0 && systemHour < lockTime) {
                        inactiveButtonOvernightToday();
                        normalButtonOvernightTomorrow();
                        pressButtonOvernightOther();
                    }
                }

                break;
            //DAILY TODAY FROM
            case R.id.textView_rerservation_daily_from_today:

                /*
                / Set Button
                */
                pressButtonDailyFromToday();
                normalButtonDailyFromTomorrow();
                normalButtonDailyFromOther();

                inactiveButtonDailyToToday();
                pressButtonDailyToTomorrow();
                normalButtonDailyToOther();

                statusDaily = false;

                tvDateFrom.setText(getSystemDay());

                getCoupon();

                break;
            //DAILY TOMORROW FROM
            case R.id.textView_rerservation_daily_from_tomorrow:

                /*
                / Set Button
                */
                normalButtonDailyFromToday();
                pressButtonDailyFromTomorrow();
                normalButtonDailyFromOther();

                inactiveButtonDailyToToday();
                inactiveButtonDailyToTomorrow();
                pressButtonDailyToOther();

                statusDaily = true;

                tvDateFrom.setText(getTomorrowDay());

                getCoupon();

                /*
                / Lock Room
                */
                if (checkRoomLock()) {
                    if (lockTime < systemHour && systemHour < 24) {
                        inactiveButtonDailyFromToday();
                        pressButtonDailyFromTomorrow();
                        normalButtonDailyFromOther();

                        inactiveButtonDailyToToday();
                        inactiveButtonDailyToTomorrow();
                        pressButtonDailyToOther();


                    }
                    if (systemHour > 0 && systemHour < lockTime) {
                        //isCurrentDay = false;
                        //No Change
                        inactiveButtonDailyFromToday();
                        pressButtonDailyFromTomorrow();
                        normalButtonDailyFromOther();

                        inactiveButtonDailyToToday();
                        inactiveButtonDailyToTomorrow();
                        pressButtonDailyToOther();
                    }
                }

                break;
            //DAILY OTHER FROM
            case R.id.textView_rerservation_daily_from_other:

                /*
                / Set Button
                */
                normalButtonDailyFromToday();
                normalButtonDailyFromTomorrow();
                pressButtonDailyFromOther();

                inactiveButtonDailyToToday();
                inactiveButtonDailyToTomorrow();
                pressButtonDailyToOther();

                statusDaily = true;

                changeDateFrom();

                /*
                / Lock Room
                */
                if (checkRoomLock()) {
                    if (lockTime < systemHour && systemHour < 24) {
                        inactiveButtonDailyFromToday();
                        normalButtonDailyFromTomorrow();
                        pressButtonDailyFromOther();

                        inactiveButtonDailyToToday();
                        inactiveButtonDailyToTomorrow();
                        pressButtonDailyToOther();


                    }
                    if (systemHour > 0 && systemHour < lockTime) {
                        //isCurrentDay = false;
                        //No Change
                        inactiveButtonDailyFromToday();
                        normalButtonDailyFromTomorrow();
                        pressButtonDailyFromOther();

                        inactiveButtonDailyToToday();
                        inactiveButtonDailyToTomorrow();
                        pressButtonDailyToOther();

                    }
                }

                break;
            //DAILY TODAY TO
            //  case R.id.textView_rerservation_daily_to_today:
            //     break;
            //DAILY TOMORROW TO
            case R.id.textView_rerservation_daily_to_tomorrow:

                /*
                / Set Button
                */
                pressButtonDailyToTomorrow();
                normalButtonDailyToOther();

                tvDateTo.setText(getTomorrowDay());

                getCoupon();

                break;
            //DAILY OTHER TO
            case R.id.textView_rerservation_daily_to_other:

                /*
                / Set Button
                */
                if (statusDaily) {
                    inactiveButtonDailyToTomorrow();
                } else {
                    normalButtonDailyToTomorrow();
                }
                pressButtonDailyToOther();

                changeDateTo();

                /*
                / Lock Room
                */
                if (checkRoomLock()) {
                    if (lockTime < systemHour && systemHour < 24) {
                        inactiveButtonDailyFromToday();
                        pressButtonDailyFromTomorrow();
                        normalButtonDailyFromOther();

                        inactiveButtonDailyToToday();
                        inactiveButtonDailyToTomorrow();
                        pressButtonDailyToOther();


                    }
                    if (systemHour > 0 && systemHour < lockTime) {
                        //isCurrentDay = false;
                        //No Change
                    }
                }

                break;
        }
    }

    private void checkOtherCondition() {
        if (bookingMode == BookingMode.HOURLY) {
            int start1 = parseTime(tvTimeFrom.getText().toString());
            if (checkOvernightHourly(start1)) {
                if (checkCouponCondition()) {
                    postReservation(null);
                }
            }
        } else {
            if (checkCouponCondition()) {
                postReservation(null);
            }
        }
    }

    private boolean checkCouponCondition() {
        if (couponIssuedForms != null && couponIssuedForms.size() > 0) {
            if (couponIndex >= 0) {

                CouponIssuedForm coupon = couponIssuedForms.get(couponIndex);

                if (coupon.getCanUse() == ParamConstants.NOT_ENOUGH_CONDITION) {
                    String message = "";
                    if (coupon.getCouponMemo().equals("")) {
                        message = getString(R.string.msg_3_9_promotion_time_frame_4);
                    } else {
                        if (HotelApplication.isEnglish) {
                            message = this.getResources().getString(R.string.msg_3_9_promotion_time_frame);
                            message = message.replace("key_time", coupon.getCouponMemo().split("\\|")[1]);
                        } else {
                            message = this.getResources().getString(R.string.msg_3_9_promotion_time_frame);
                            message = message.replace("key_time", coupon.getCouponMemo().split("\\|")[0]);
                        }

                    }

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                    return false;
                } else return true;
            }
        }
        return true;
    }

    private boolean checkOvernightHourly(int time) {
        if (startOverNight > endOvernight) {
            if (time >= startOverNight || time < endOvernight) {
                Toast.makeText(ReservationActivity.this, R.string.msg_3_9_overnight_time, Toast.LENGTH_LONG).show();
                return false;

            }
        } else if (startOverNight < endOvernight) {
            //startOverNight < endOvernight
            if (time >= startOverNight && time <= endOvernight) {
                Toast.makeText(ReservationActivity.this, R.string.msg_3_9_overnight_time, Toast.LENGTH_LONG).show();
                return false;
            }

        }
        return true;
    }


    private void changeDateOvernight() {
        Calendar calendarSelected = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        try {
            Date date = dateFormatter.parse(tvDateOvernight.getText().toString());
            calendarSelected.setTimeInMillis(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            MyLog.writeLog("changeDateOvernight------------------->" + e);
        }
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        ViewPager vpCalendar = dialog.findViewById(R.id.vpCalendar);

        dialog.findViewById(R.id.linear_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        CellDayClickListener cellDayClickListener = new CellDayClickListener() {
            @Override
            public void onCellClick(String date) {
                dialog.dismiss();
                tvDateOvernight.setText(date);

                getCoupon();

            }
        };
        CalendarAdapter calendarAdapter = new CalendarAdapter(this, calendar, calendarSelected, minDate, cellDayClickListener);
        vpCalendar.setAdapter(calendarAdapter);
    }

    private void changeCoupon() {
        /*
        / Show List Coupon
        */
        Intent intent = new Intent(this, ChooseCouponActivity.class);
        //Send Form Coupon
        intent.putParcelableArrayListExtra("CouponIssuedForm", couponIssuedForms);
        //Send Selected
        intent.putExtra("couponIndex", couponIndex);
        startActivityForResult(intent, CHOOSE_COUPON);
        overridePendingTransition(R.anim.hotel_slide_up, R.anim.stable);
    }

    private void postReservation(String phoneNumber) {

        final UserBookingDto userBookingDto = getUserBookingDto();
        if (phoneNumber != null) {
            userBookingDto.setMobile(phoneNumber);
            bundle.putString("MOBILE", phoneNumber);
        } else {
            bundle.putString("MOBILE", null);
        }

        /*
        / Check Before Create Reservation
        */
        DialogUtils.showLoadingProgress(this, false);
        HotelApplication.serviceApi.checkBeforeCreateReservation(userBookingDto, PreferenceUtils.getToken(this), HotelApplication.DEVICE_ID).enqueue(new Callback<RestResult>() {
            @Override
            public void onResponse(Call<RestResult> call, Response<RestResult> response) {
                DialogUtils.hideLoadingProgress();
                if (response.isSuccessful()) {
                    //CHECK OK
                    final RestResult restResult = response.body();
                    if (restResult != null) {
                        if (restResult.getResult() == 1) {

                            //SET EVENT FACEBOOK
                            setEventFacebook(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, hotelDetailForm.getSn(), totalFee);

                            //STORE BUNDLE
                            String mapInfo = "";
                            Map map = restResult.getMapInfo();
                            if (map != null)
                                try {
                                    mapInfo = restResult.getMapInfo().get("couponConditionForm").toString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            bundle.putString("MAP-INFO", mapInfo);
                            bundle.putString("HOTEL_PAYMENT", restResult.getOtherInfo());
                            bundle.putInt("HOTEL_STATUS", hotelDetailForm.getHotelStatus());
                            bundle.putInt("ROOM_TYPE", userBookingDto.getRoomTypeSn());
                            bundle.putString("START_TIME", userBookingDto.getStartTime());
                            bundle.putString("END_TIME", userBookingDto.getEndTime());
                            bundle.putString("END_DATE", userBookingDto.getEndDate());
                            bundle.putString("DATE_PLAN", userBookingDto.getCheckInDatePlan());
                            bundle.putInt("TYPE", userBookingDto.getType());

                            if (userBookingDto.getCouponIssuedSn() == null) {
                                bundle.putLong("COUPON", NO_COUPON);
                            } else {
                                bundle.putLong("COUPON", userBookingDto.getCouponIssuedSn());
                            }

                            if (checkRoomTypeFlashSale()) {
                                bundle.putBoolean("FLASH_SALE", true);
                            } else {
                                bundle.putBoolean("FLASH_SALE", false);
                            }

                            bundle.putString("IP", userBookingDto.getClientip());

                            //
                            //CHECK ORTHER INFO
                            //
                            if (restResult.getOtherInfo() != null) {

                                //Store payment method
                                bundle.putString("METHOD_PAYMENT", restResult.getOtherInfo());

                                switch (restResult.getOtherInfo()) {

                                    case ParamConstants.METHOD_BOTH:
                                        //Both = 1
                                        MyLog.writeLog("Check Reservatiom -------------> 1");

                                        if (totalFee == 0) {
                                            gotoPayAtHotel();
                                        } else {
                                            gotoBillingInfo();
                                        }

                                        break;
                                    case ParamConstants.METHOD_PAY_AT_HOTEL:
                                        MyLog.writeLog("Check Reservatiom -------------> 2");
                                        //Only Pay at Hotel
                                        gotoPayAtHotel();
                                        break;
                                    case ParamConstants.METHOD_ALWAYS_PAY_ONLINE:
                                        MyLog.writeLog("Check Reservatiom -------------> 3");
                                        //Only Paynow
                                        //Check minPrice
                                        checkMinPrice(ParamConstants.METHOD_ALWAYS_PAY_ONLINE, userBookingDto);
                                        //showDialog3(userBookingDto);
                                        break;
                                    case ParamConstants.METHOD_PAY_ONLINE_IN_DAY:
                                        MyLog.writeLog("Check Reservatiom -------------> 4");
                                        //4:Pay online in Weekend
                                        //Check minPrice
                                        checkMinPrice(ParamConstants.METHOD_PAY_ONLINE_IN_DAY, userBookingDto);
                                        //showDialog4(userBookingDto);
                                        break;
                                    case ParamConstants.METHOD_FLASH_SALE:
                                        MyLog.writeLog("Check Reservatiom -------------> 5");
                                        //Flash Sale
                                        gotoBillingInfo();
                                        break;
                                }
                            }

                        } else if (restResult.getResult() == 21) {
                            MyLog.writeLog("Check Reservatiom -------------> 21");
                            showDialog21();
                        } else {
                            MyLog.writeLog("Check Reservatiom -------------> Other");
                            //else 21
                            showMessage(restResult.getMessage());
                        }
                    }

                } else if (response.code() == 401) {
                    DialogUtils.showExpiredDialog(ReservationActivity.this, new DialogCallback() {
                        @Override
                        public void finished() {
                            Intent intent = new Intent(ReservationActivity.this, LoginActivity.class);
                            ReservationActivity.this.startActivityForResult(intent, LOGIN_RESERVATION_REQUEST);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RestResult> call, Throwable t) {
                DialogUtils.hideLoadingProgress();
                Toast.makeText(ReservationActivity.this, getString(R.string.cannot_connect_to_server), Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    / CHECK STATUS
    */

    /*
    * Check MinPrice Pay123
    */
    private void checkMinPrice(String otherInfo, UserBookingDto userBookingDto) {

        int totalFee = bundle.getInt("total");
        if (totalFee < minPrice) {
            //goto Pay At Hotel
            gotoPayAtHotel();

        } else {
            /*
            * check trial hotel
            */
            if (hotelDetailForm.getHotelStatus() == ContractType.TRIAL.getType()) { //Trial hotel
                Toast.makeText(ReservationActivity.this, getString(R.string.msg_can_not_payment_trial), Toast.LENGTH_LONG).show();
            } else {
                if (otherInfo.equals(ParamConstants.METHOD_ALWAYS_PAY_ONLINE)) {
                    //Only Paynow
                    showDialog3(userBookingDto);
                } else {
                    //4:Pay online in Weekend
                    showDialog4(userBookingDto);
                }
            }
        }
    }

    // Orther Info = 1
    private void gotoBillingInfo() {
        /*
         * goto Billing_infomation --->Choose: Pay at Hotel or Pay in Advance
         */
        Intent billing_information = new Intent(ReservationActivity.this, Billing_Information.class);
        billing_information.setAction("New_Payment");
        billing_information.putExtra("InformationBilling", bundle);
        startActivity(billing_information);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    //Orther info = 2
    private void gotoPayAtHotel() {
        /*
         * goto PayAtHotel--->Confirm
         */
        Intent paid_At_Hotel = new Intent(ReservationActivity.this, Paid_At_Hotel.class);
        paid_At_Hotel.putExtra("InformationBilling", bundle);
        startActivity(paid_At_Hotel);
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    //Other Info = 3
    private void showDialog3(final UserBookingDto userBookingDto) {
        Dialag.getInstance().show(this, false, true, true, null, getString(R.string.msg_3_9_pay_online_inday_only), getString(R.string.txt_6_3_1_paynow), getString(R.string.msg_3_9_search_hotel), null, Dialag.BTN_LEFT, new CallbackDialag() {
            @Override
            public void button1() {
                //Pay Now
                //payNow(userBookingDto);
                gotoBillingInfo();
            }

            @Override
            public void button2() {
                //Search Hotel
                searchHotel();
            }

            @Override
            public void button3(Dialog dialog) {
                //Hide
            }
        });
    }

    //Other Info = 21
    private void showDialog21() {
        Dialag.getInstance().show(this, false, true, true, null, getString(R.string.msg_3_9_disbale_booking), getString(R.string.msg_3_9_change_date), getString(R.string.msg_3_9_search_hotel), null, Dialag.BTN_LEFT, new CallbackDialag() {
            @Override
            public void button1() {
                //Change Date
                changeDate();
            }

            @Override
            public void button2() {
                //Search Hotel
                searchHotel();
            }

            @Override
            public void button3(Dialog dialog) {
                //Hide
            }
        });
    }

    //Show Message
    private void showMessage(String msg) {
        Dialag.getInstance().show(this, false, true, true, null, msg, getString(R.string.yes), null, null, Dialag.BTN_LEFT, new CallbackDialag() {
            @Override
            public void button1() {
                //btn Yes
            }

            @Override
            public void button2() {
                //Hide
            }

            @Override
            public void button3(Dialog dialog) {
                //Hide
            }
        });
    }

    //RestResult = 4
    private void showDialog4(final UserBookingDto userBookingDto) {
        Dialag.getInstance().show(this, false, true, true, null, getString(R.string.msg_3_9_pay_online_inday_only), getString(R.string.txt_6_3_1_paynow), getString(R.string.msg_3_9_change_date), getString(R.string.msg_3_9_search_hotel), Dialag.BTN_LEFT, new CallbackDialag() {
            @Override
            public void button1() {
                //PayNow
                //payNow(userBookingDto);
                gotoBillingInfo();
            }

            @Override
            public void button2() {
                //Change Date
                changeDate();
            }

            @Override
            public void button3(Dialog dialog) {
                //Search Hotel
                dialog.dismiss();
                searchHotel();
            }
        });
    }

    private boolean checkStampEnough(UserStampForm userStampForm) {
        boolean result = false;
        if (userStampForm != null && userStampForm.getNumStampActive() >= userStampForm.getNumToRedeem() && userStampForm.getNumToRedeem() > 0) {
            result = true;
        }
        return result;
    }

    private void showDialogPoint() {
        int myBlock = myPoint / numPoint;
        int priceBlock = totalFee / VALUE_POINT;
        int point;
        if (myBlock < priceBlock) {
            pointValue = myBlock * VALUE_POINT;
            point = myBlock * numPoint;
        } else {
            pointValue = priceBlock * VALUE_POINT;
            point = priceBlock * numPoint;
        }
        String message = getString(R.string.msg_6_13_exchange_point_to_money, String.valueOf(point), String.valueOf(pointValue), String.valueOf(totalFee - pointValue));
        String btn1 = getString(R.string.cancel);
        String btn2 = getString(R.string.txt_6_12_stamp_continue);
        Dialag.getInstance().show(this, false, true, true, null, message, btn1, btn2, null, Dialag.BTN_MIDDLE, new CallbackDialag() {
            @Override
            public void button1() {
                pointValue = 0;
            }

            @Override
            public void button2() {
                floatButtonPoint.setText(getString(R.string.txt_6_13_refund));
                //Calculate
                calculateFee();
            }

            @Override
            public void button3(Dialog dialog) {

            }
        });
    }


    private void showDialogStamp() {
        String message = getString(R.string.msg_6_12_stamp_redeemed_asking);
        String btn1 = getString(R.string.txt_6_12_stamp_continue);
        String btn2 = getString(R.string.txt_6_12_stamp_redeem);

        //redeemValue = userStampForm.getRedeemValue();

        if (userStampForm.getNumStampLocked() > 0) {
            redeemValue = 0;
            message = getString(R.string.msg_6_12_redeem_not_yet_checkin);
            btn1 = getString(R.string.btn_6_9_3_cancel);
            btn2 = getString(R.string.txt_6_12_stamp_continue);
        }

        Dialag.getInstance().show(this, false, true, true, null, message, btn1, btn2, null, Dialag.BTN_MIDDLE, new CallbackDialag() {
            @Override
            public void button1() {
                if (userStampForm.getNumStampLocked() > 0) {
                    finish();
                } else {
                    checkOtherCondition();
                }
            }

            @Override
            public void button2() {
                //Add Redeem
                if (couponIndex == NO_COUPON) {
                    if (bookingMode == BookingMode.HOURLY && !userStampForm.isRedeemHourly()) {
                        Toast.makeText(ReservationActivity.this, getString(R.string.msg_6_12_stamp_not_condition), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (bookingMode == BookingMode.OVERNIGHT && !userStampForm.isRedeemOvernight()) {
                        Toast.makeText(ReservationActivity.this, getString(R.string.msg_6_12_stamp_not_condition), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (bookingMode == BookingMode.DAILY && !userStampForm.isRedeemDaily()) {
                        Toast.makeText(ReservationActivity.this, getString(R.string.msg_6_12_stamp_not_condition), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (userStampForm.getNumStampLocked() == 0) {
                        Toast.makeText(ReservationActivity.this, getString(R.string.msg_6_12_stamp_coupon_is_using), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (userStampForm != null)
                    redeemValue = userStampForm.getRedeemValue();

                //Redeem
                bundle.putInt("redeemValue", redeemValue);
                // Total
                bundle.putInt("total", totalFee - redeemValue);
                checkOtherCondition();
            }

            @Override
            public void button3(Dialog dialog) {
            }
        });
    }

    //Search Hotel
    private void searchHotel() {
        if (HotelDetailActivity.hotelDetailActivity != null) {
            HotelDetailActivity.hotelDetailActivity.finish();
        }
        finish();
    }

    //Change Data
    private void changeDate() {
        //dismiss
    }

    //Pay Now
    private void payNow(UserBookingDto userBookingDto) {

        ControllerApi.getmInstance().createNewUserBooking(this, userBookingDto, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                RestResult restResult = (RestResult) object;

                Gson gson = new Gson();
                PaymentInfoForm paymentInfoForm = gson.fromJson(restResult.getOtherInfo(), PaymentInfoForm.class);
                Intent intent = new Intent(ReservationActivity.this, BrowserPaymentActivity.class);
                intent.putExtra("PaymentInfoForm", paymentInfoForm);
                intent.putExtra("userBookingSn", restResult.getSn());
                intent.putExtra("METHOD_PAYMENT", bundle.getString("METHOD_PAYMENT", "default"));
                startActivity(intent);

                //close Activity HotelDetailActivity
                if (HotelDetailActivity.hotelDetailActivity != null) {
                    HotelDetailActivity.hotelDetailActivity.finish();
                }

                //Only Pay_online
                //finish();

                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });
    }

    /*
    /
    */
    private void changeDateTo() {
        Calendar calendarSelected = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();

        try {
            Date min = dateFormatter.parse(tvDateFrom.getText().toString());
            minDate.setTimeInMillis(min.getTime());

            Date date = dateFormatter.parse(tvDateTo.getText().toString());
            calendarSelected.setTimeInMillis(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            MyLog.writeLog("changeDateTo------------->" + e);
        }
        minDate.add(Calendar.DATE, 1);
        calendar.setTimeInMillis(minDate.getTimeInMillis());
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        ViewPager vpCalendar = dialog.findViewById(R.id.vpCalendar);

        dialog.findViewById(R.id.linear_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        CellDayClickListener cellDayClickListener = new CellDayClickListener() {
            @Override
            public void onCellClick(String date) {
                dialog.dismiss();
                tvDateTo.setText(date);

                getCoupon();
            }
        };
        CalendarAdapter calendarAdapter = new CalendarAdapter(this, calendar, calendarSelected, minDate, cellDayClickListener);
        vpCalendar.setAdapter(calendarAdapter);
    }

    private void changeDateFrom() {
//        DateTimeDialogUtils.showDatePickerDialog(this, tvDateFrom, true);
        Calendar calendarSelected = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        try {
            Date date = dateFormatter.parse(tvDateFrom.getText().toString());
            calendarSelected.setTimeInMillis(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            MyLog.writeLog("changeDateFrom------------->" + e);
        }
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        ViewPager vpCalendar = dialog.findViewById(R.id.vpCalendar);

        dialog.findViewById(R.id.linear_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        CellDayClickListener cellDayClickListener = new CellDayClickListener() {
            @Override
            public void onCellClick(String date) {
                dialog.dismiss();
                tvDateFrom.setText(date);

                getCoupon();

            }
        };
        CalendarAdapter calendarAdapter = new CalendarAdapter(this, calendar, calendarSelected, minDate, cellDayClickListener);
        vpCalendar.setAdapter(calendarAdapter);
    }

    private int handleLimit(String time) {
        String[] s = time.split(":");
        int limit = parseTime(time) * 2;
        if (s.length > 1) {
            if (s[1].equals("30")) {
                limit++;
            }
        }
        return limit;
    }

    private void showClock() {
        //Create System Hour
        //Check Overnight Time
        int[] overnight = new int[2];
        overnight[0] = startOverNight;
        overnight[1] = endOvernight;

        //Check CineJoy Time
        int[] cinejoy = new int[0];
        if (checkRoomTypeCinema()) {

            cinejoy = new int[4];

            int start_in_cineJoy = 8;
            int end_in_cineJoy = 18;
            int start_out_cineJoy = 10;
            int end_out_cineJoy = 20;

            cinejoy[0] = start_in_cineJoy;
            cinejoy[1] = end_in_cineJoy;
            cinejoy[2] = start_out_cineJoy;
            cinejoy[3] = end_out_cineJoy;
        }

        //Set Position Select
        int[] position = new int[2];
        position[0] = handleLimit(tvTimeFrom.getText().toString());
        position[1] = handleLimit(tvTimeTo.getText().toString());

        //Show Clock
        Clock.getInstance().show(this, overnight, cinejoy, position, isCurrentDay, firstHour, new CallbackClock() {
            @Override
            public void onValue(String from, String to) {
                if (!TextUtils.isEmpty(from))
                    tvTimeFrom.setText(from);
                if (!TextUtils.isEmpty(to))
                    tvTimeTo.setText(to);

                getCoupon();
            }
        });

    }

    //Show Calendar
    private void changeDateHourly() {
        Calendar calendarSelected = Calendar.getInstance();
        final Calendar calendar = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        try {
            Date date = dateFormatter.parse(tvDateHourly.getText().toString());
            calendarSelected.setTimeInMillis(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            MyLog.writeLog("changeDateHourly------------->" + e);
        }
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.calendar_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        ViewPager vpCalendar = dialog.findViewById(R.id.vpCalendar);

        dialog.findViewById(R.id.linear_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        CellDayClickListener cellDayClickListener = new CellDayClickListener() {
            @Override
            public void onCellClick(String date) {
                dialog.dismiss();
                checkHourlyReservation(date);

                getCoupon();

            }
        };
        CalendarAdapter calendarAdapter = new CalendarAdapter(this, calendar, calendarSelected, minDate, cellDayClickListener);
        vpCalendar.setAdapter(calendarAdapter);
    }

    /*
    / CHECK TODAY OR TOMORROW
    */

    private void checkHourlyReservation(String date) {

        String[] time = setupTime();

        int start = parseTime(time[0]);
        int end = parseTime(time[1]);

        //String[] t = HelperClock.handleStringOvernight(start, startOverNight, endOvernight);

        //Update View
        tvTimeFrom.setText(time[0]);
        tvTimeTo.setText(time[1]);

        systemHour = getSystemHour();

        //Check current day
        if (ReservationActivity.this.getSystemDay().equals(date)) {
            isCurrentDay = true;
        } else {
            isCurrentDay = false;
            btnReservation.setVisibility(View.VISIBLE);
        }

        //Check hotel limit 2 hour
        if (end - start < firstHour && start != 0 && bookingMode == BookingMode.HOURLY) {
            guiState = hideBtnReservation();
        }

        //Set Change day
        tvDateHourly.setText(date);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        * Change room type
        */
        if (requestCode == CHOOSE_ROOM_TYPE) {
            if (resultCode == RESULT_OK) {
                //Get RoomTypeForm
                roomtypeIndex = data.getIntExtra("RoomTypeIndex", 0);
                roomTypeForm = hotelDetailForm.getRoomTypeList().get(roomtypeIndex);
                btnReservation.setVisibility(View.VISIBLE);

                //Set View
                tvRoomName.setText(roomTypeForm.getName());
                firstHour = roomTypeForm.getFirstHours();

                /*
                /Change Room Type return
               */

                //Check Coupon For Flash Sale
                if (checkRoomTypeFlashSale()) {
                    couponIndex = NO_COUPON;
                    redeemValue = 0;
                    btnNotApply.setVisibility(View.GONE);
                    btnNumberStamp.setVisibility(View.GONE);
                    btnRedeem.setVisibility(View.VISIBLE);
                    deleteStamp();
                    initData();
                } else {
                    if (userStampForm == null) {
                        findUserStampDetail(hotelDetailForm.getSn());
                    } else {
                        if (userStampForm.getNumToRedeem() <= 0) {
                            findUserStampDetail(hotelDetailForm.getSn());
                        } else {
                            boxStamp.setVisibility(View.VISIBLE);
                            if (autoRedeem) {
                                redeemValue = userStampForm.getRedeemValue();
                                changeModeBookingForStamp(userStampForm);
                            }
                        }
                    }


                    deleteStamp();
                    changeBookingMode();
                    getCoupon();

                    if (checkRoomTypeCinema()) {
                        bookingModeHourly();
                        setViewCinema();
                    }

                    /*
                    / Lock Room
                    */
                    if (checkRoomLock()) {
                        if (lockTime < systemHour && systemHour < 24) {
                            inactiveButtonToday();
                            pressButtonTomorrow();
                            normalButtonOther();
                        }
                        if (systemHour > 0 && systemHour < lockTime) {
                            isCurrentDay = false;
                        }
                    }

                    MyLog.writeLog("Calculate 2779");
                    calculateFee();

                }
            }
        } else if (requestCode == CHOOSE_COUPON) {
            if (resultCode == RESULT_OK) {
                couponIndex = data.getIntExtra("CouponIndex", -2);
                try {
                    if (couponIndex >= 0) {
                        //press Change Coupon
                        /*
                        / Check Coupon
                        */
                        if (couponIssuedForms.get(couponIndex).getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
                            tvCoupon.setText(couponIssuedForms.get(couponIndex).getPromotionName() + "\n" + getString(R.string.discount) + " " + String.valueOf(Utils.formatCurrency(couponIssuedForms.get(couponIndex).getDiscount())) + " " + getString(R.string.percent));
                        } else {
                            if (roomTypeForm != null && roomTypeForm.isCinema()) {
                                tvCoupon.setText(couponIssuedForms.get(couponIndex).getPromotionName() + "\n" + String.valueOf(Utils.formatCurrency(couponIssuedForms.get(couponIndex).getCineDiscount())) + getString(R.string.currency));
                            } else {
                                tvCoupon.setText(couponIssuedForms.get(couponIndex).getPromotionName() + "\n" + String.valueOf(Utils.formatCurrency(couponIssuedForms.get(couponIndex).getDiscount())) + getString(R.string.currency));
                            }
                        }
                        btnClearCoupon.setVisibility(View.VISIBLE);
                    }else if (couponIndex == -1){
                        /*
                        / Callback Apply Promotion
                        */
                        couponIndex = 0;
                        getCoupon();
                    }
                } catch (Exception e) {
                    MyLog.writeLog("couponIndex------------>" + e);
                }
                MyLog.writeLog("Calculate 2804");
                calculateFee();
            }
        } else if (requestCode == LOGIN_RESERVATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                postReservation(null);
            }
        } else if (requestCode == LOGIN_COUPON_REQUEST) {
            if (resultCode == RESULT_OK) {
                isLogIn = true;
                initData();  //get Coupon fail
            }
        } else if (requestCode == PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public void setScreenName() {
        this.screenName = "SHotelBook";
    }

    private void showDialogInputPhone() {
        Utils.showKeyboard(this);
        DialogInput.getInstance().show(this, new CallbackInput() {
            @Override
            public void onInput(String s) {
                postReservation(s);
                Utils.hideKeyboard(ReservationActivity.this);
            }
        });
    }

    //Setup Hourly
    private void bookingModeHourly() {
        bookingMode = BookingMode.HOURLY;

        boxHourly.setVisibility(View.VISIBLE);
        boxDaily.setVisibility(View.GONE);
        boxOvernight.setVisibility(View.GONE);

        btnReservation.setVisibility(View.VISIBLE);

        chkHourly.setImageResource(R.drawable.checkbox_selected);
        txtHourly.setTextColor(ContextCompat.getColor(this, R.color.bk));
        chkHourly.setEnabled(true);

        txtDaily.setTextColor(ContextCompat.getColor(this, R.color.bk));
        chkDaily.setEnabled(true);
        chkDaily.setImageResource(R.drawable.checkbox);

        chkOvernight.setImageResource(R.drawable.checkbox);
        guiState = true;

        systemDate = getSystemDay();

        /*
        / Set Button
        */
        pressButtonToday();
        normalButtonTomorrow();
        normalButtonOther();

        checkHourlyReservation(systemDate);


        /*
        / Lock Room
        */
        if (checkRoomLock()) {
            if (lockTime < systemHour && systemHour < 24) {

                systemDate = getTomorrowDay();
                isCurrentDay = false;

                inactiveButtonToday();
                pressButtonTomorrow();
                normalButtonOther();
            } else if (systemHour > 0 && systemHour < lockTime) {

                inactiveButtonToday();
                pressButtonTomorrow();
                normalButtonOther();
            }
        }


        //Set Change day
        tvDateHourly.setText(systemDate);


        /*
        / Set Stamp
        */
        if (redeemValue > 0) {
            if (checkRoomTypeCinema() && !userStampForm.isRedeemHourly()) {
                return;
            }
            setViewStamp(userStampForm);
        }
    }

    //Setup Overnight
    private void bookingModeOvernight() {
        bookingMode = BookingMode.OVERNIGHT;
        boxOvernight.setVisibility(View.VISIBLE);
        boxDaily.setVisibility(View.GONE);
        boxHourly.setVisibility(View.GONE);
        btnReservation.setVisibility(View.VISIBLE);
        chkDaily.setImageResource(R.drawable.checkbox);
        chkHourly.setImageResource(R.drawable.checkbox);
        chkOvernight.setImageResource(R.drawable.checkbox_selected);
        chkOvernight.setEnabled(true);

        /*
        / Check Overnight Tonight-Right Now
        */
        int hour = getSystemHour() + 1;
        if (startOverNight > endOvernight) {
            if (!checkRoomTypeFlashSale() && 0 <= hour && hour < endOvernight) {
                pressButtonOvernightToday(); //ON RightNow
                normalButtonOvernightTomorrow();
                normalButtonOvernightOther();

                tvDateOvernight.setText(getYesterday());
                tvDateOvernight.setTag("RightNow");
                //Set StatusOvernight
                statusRightNow = true; //When:   0 <= HourSelect <= endOvernight
            } else /*FlashSale*/ {
                if (checkRoomTypeFlashSale() && 0 <= hour && hour < 6) {
                    pressButtonOvernightToday(); //ON RightNow
                    inactiveButtonOvernightTomorrow();
                    normalButtonOvernightOther();

                    tvDateOvernight.setText(getSystemDay()); //Set System Time
                    tvDateOvernight.setTag("RightNow");
                    //Set StatusOvernight
                    statusRightNow = true;
                } else {
                    inactiveButtonOvernightToday(); //OFF RightNow
                    pressButtonOvernightTomorrow();
                    normalButtonOvernightOther();

                    tvDateOvernight.setText(getSystemDay()); //Set System Time
                    tvDateOvernight.setTag("Tonight");
                    //Set StatusOvernight
                    statusRightNow = false;
                }
            }
        } else /*endOvernight > startOverNight*/ {
            int end = parseTime(tvTimeTo.getText().toString());

            if (!checkRoomTypeFlashSale() && startOverNight <= hour && hour <= end) {
                pressButtonOvernightToday();
                normalButtonOvernightTomorrow();
                normalButtonOvernightOther();

                tvDateOvernight.setText(getSystemDay());
                tvDateOvernight.setTag("RightNow");
                //Set StatusOvernight
                statusRightNow = true; //When:   0 <= HourSelect <= endOvernight
            } else /*FlashSale*/ {

                if (checkRoomTypeFlashSale() && 0 <= hour && hour < 6) {
                    pressButtonOvernightToday(); //ON RightNow
                    inactiveButtonOvernightTomorrow();
                    normalButtonOvernightOther();

                    tvDateOvernight.setText(getSystemDay()); //Set System Time
                    tvDateOvernight.setTag("RightNow");
                    //Set StatusOvernight
                    statusRightNow = true;
                } else {
                    inactiveButtonOvernightToday();
                    pressButtonOvernightTomorrow();
                    normalButtonOvernightOther();

                    tvDateOvernight.setText(getSystemDay());
                    tvDateOvernight.setTag("Tonight");
                    //Set StatusOvernight
                    statusRightNow = false;
                }
            }
        }


        /*
        / Set Stamp
        */
        if (redeemValue > 0) {
            setViewStamp(userStampForm);
        }


        /*
        / Lock Room
        */
        if (checkRoomLock()) {
            tvDateOvernight.setText(getTomorrowDay());
            if (lockTime < systemHour && systemHour < 24) {
                inactiveButtonOvernightToday();
                inactiveButtonOvernightTomorrow();
                pressButtonOvernightOther();
            } else if (systemHour > 0 && systemHour < lockTime) {
                inactiveButtonOvernightToday();
                pressButtonOvernightTomorrow();
                normalButtonOvernightOther();

                tvDateOvernight.setText(getSystemDay());
                tvDateOvernight.setTag("Tonight");
                //Set StatusOvernight
                statusRightNow = false;
            }
        }

    }

    //Setup Daily
    private void bookingModeDaily() {
        bookingMode = BookingMode.DAILY;

        boxDaily.setVisibility(View.VISIBLE);
        boxHourly.setVisibility(View.GONE);
        boxOvernight.setVisibility(View.GONE);
        btnReservation.setVisibility(View.VISIBLE);
        chkOvernight.setImageResource(R.drawable.checkbox);
        chkHourly.setImageResource(R.drawable.checkbox);
        chkDaily.setImageResource(R.drawable.checkbox_selected);

        txtHourly.setTextColor(ContextCompat.getColor(this, R.color.bk));
        chkHourly.setEnabled(true);
        txtDaily.setTextColor(ContextCompat.getColor(this, R.color.bk));
        chkDaily.setEnabled(true);

        pressButtonDailyFromToday();
        normalButtonDailyFromTomorrow();
        normalButtonDailyFromOther();

        inactiveButtonDailyToToday();
        pressButtonDailyToTomorrow();
        normalButtonDailyToOther();


        /*
        / Set Stamp
        */
        if (redeemValue > 0) {
            setViewStamp(userStampForm);
        }

        /*
        / Lock Room
        */
        if (checkRoomLock()) {
            if (lockTime < systemHour && systemHour < 24) {
                inactiveButtonDailyFromToday();
                pressButtonDailyFromTomorrow();
                normalButtonDailyFromOther();

                inactiveButtonDailyToToday();
                inactiveButtonDailyToTomorrow();
                pressButtonDailyToOther();

                tvDateFrom.setText(getSystemDay());


            }
            if (systemHour > 0 && systemHour < lockTime) {
                //isCurrentDay = false;
                //No Change
                inactiveButtonDailyFromToday();
                pressButtonDailyFromTomorrow();
                normalButtonDailyFromOther();

                inactiveButtonDailyToToday();
                inactiveButtonDailyToTomorrow();
                pressButtonDailyToOther();

                tvDateFrom.setText(getTomorrowDay());
            }
        }
    }

    private boolean checkRoomLock() {
        boolean result = false;
        if (hotelDetailForm.getRoomTypeList().get(roomtypeIndex).getStatus() == ParamConstants.LOCK_TODAY)
            result = true;
        return result;
    }

    private boolean checkRoomTypeFlashSale() {
        boolean result = false;
        if (hotelDetailForm.getRoomTypeList().get(roomtypeIndex).isFlashSale()) {
            result = true;
        }
        return result;
    }

    private boolean checkRoomTypeCinema() {
        boolean result = false;
        if (hotelDetailForm.getRoomTypeList().get(roomtypeIndex).isCinema()) {
            result = true;
        }
        return result;
    }

    private void deleteStamp() {
        if (bookingMode == BookingMode.HOURLY) {
            chkHourly.setImageResource(R.drawable.checkbox_selected);
        } else {
            chkHourly.setImageResource(R.drawable.checkbox);
        }
        txtHourly.setTextColor(ContextCompat.getColor(this, R.color.bk));
        chkHourly.setEnabled(true);

        if (bookingMode == BookingMode.OVERNIGHT) {
            chkOvernight.setImageResource(R.drawable.checkbox_selected);
        } else {
            chkOvernight.setImageResource(R.drawable.checkbox);
        }
        txtOvernight.setTextColor(ContextCompat.getColor(this, R.color.bk));
        chkOvernight.setEnabled(true);

        if (bookingMode == BookingMode.DAILY) {
            chkDaily.setImageResource(R.drawable.checkbox_selected);
        } else {
            chkDaily.setImageResource(R.drawable.checkbox);
        }
        txtDaily.setTextColor(ContextCompat.getColor(this, R.color.bk));
        chkDaily.setEnabled(true);
    }

    private void setViewStamp(UserStampForm userStampForm) {
        if (userStampForm != null) {
            if (!userStampForm.isRedeemHourly()) {
                chkHourly.setImageResource(R.drawable.checkbox_transparent);
                txtHourly.setTextColor(ContextCompat.getColor(this, R.color.bk_15p));
                chkHourly.setEnabled(false);
            }
            if (!userStampForm.isRedeemOvernight()) {
                chkOvernight.setImageResource(R.drawable.checkbox_transparent);
                txtOvernight.setTextColor(ContextCompat.getColor(this, R.color.bk_15p));
                chkOvernight.setEnabled(false);
            }
            if (!userStampForm.isRedeemDaily()) {
                chkDaily.setImageResource(R.drawable.checkbox_transparent);
                txtDaily.setTextColor(ContextCompat.getColor(this, R.color.bk_15p));
                chkDaily.setEnabled(false);
            }
        }
    }

    private void changeModeBookingForStamp(UserStampForm userStampForm) {
        if (userStampForm.isRedeemHourly()) {
            bookingModeHourly();
        } else if (userStampForm.isRedeemOvernight()) {
            bookingModeOvernight();
        } else if (userStampForm.isRedeemDaily()) {
            bookingModeDaily();
        }

        setViewStamp(userStampForm);
    }

    private void changeBookingMode() {
        if (bookingMode == BookingMode.HOURLY) {
            bookingModeHourly();
        } else if (bookingMode == BookingMode.OVERNIGHT) {
            bookingModeOvernight();
        } else if (bookingMode == BookingMode.DAILY) {
            bookingModeDaily();
        }
    }

    private void setViewFlashSale() {
        chkHourly.setImageResource(R.drawable.checkbox_transparent);
        txtHourly.setTextColor(ContextCompat.getColor(this, R.color.bk_15p));
        chkHourly.setEnabled(false);

        chkDaily.setImageResource(R.drawable.checkbox_transparent);
        txtDaily.setTextColor(ContextCompat.getColor(this, R.color.bk_15p));
        chkDaily.setEnabled(false);

        inactiveButtonOvernightOther();
    }

    private void setViewCinema() {

        chkOvernight.setImageResource(R.drawable.checkbox_transparent);
        txtOvernight.setTextColor(ContextCompat.getColor(this, R.color.bk_15p));
        chkOvernight.setEnabled(false);

        chkDaily.setImageResource(R.drawable.checkbox_transparent);
        txtDaily.setTextColor(ContextCompat.getColor(this, R.color.bk_15p));
        chkDaily.setEnabled(false);

        //Check tomorrow
        if (!checkRoomLock())
            pressButtonToday();
    }

    public boolean hideBtnReservation() {
        tvFee.setText(String.format(getString(R.string.msg_3_9_booking_only), firstHour));
        btnReservation.setVisibility(View.GONE);
        return false;
    }

    public boolean displayGUI() {
        MyLog.writeLog("Calculate 3024");
        calculateFee();
        btnReservation.setVisibility(View.VISIBLE);
        return true;
    }

    private void pressButtonToday() {
        txtToday.setBackgroundResource(R.drawable.button_full_org_bg);
        txtToday.setTextColor(getResources().getColor(R.color.wh));
        txtToday.setEnabled(true);
    }

    private void normalButtonToday() {
        txtToday.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtToday.setTextColor(getResources().getColor(R.color.org));
        txtToday.setEnabled(true);
    }

    private void inactiveButtonToday() {
        txtToday.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
        txtToday.setTextColor(getResources().getColor(R.color.org_20p));
        txtToday.setEnabled(false);
    }

    private void pressButtonTomorrow() {
        txtTomorrow.setBackgroundResource(R.drawable.button_full_org_bg);
        txtTomorrow.setTextColor(getResources().getColor(R.color.wh));
        txtTomorrow.setEnabled(true);
    }

    private void normalButtonTomorrow() {
        txtTomorrow.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtTomorrow.setTextColor(getResources().getColor(R.color.org));
        txtTomorrow.setEnabled(true);
    }

    private void inactiveButtonTomorrow() {
        txtTomorrow.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
        txtTomorrow.setTextColor(getResources().getColor(R.color.org_20p));
        txtTomorrow.setEnabled(false);
    }

    private void pressButtonOther() {
        boxOther.setBackgroundResource(R.drawable.button_full_org_bg);
        txtOther.setTextColor(getResources().getColor(R.color.wh));
        txtOther.setEnabled(true);
        //imgOther.setImageResource(R.drawable.date);
    }

    private void normalButtonOther() {
        boxOther.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtOther.setTextColor(getResources().getColor(R.color.org));
        txtOther.setEnabled(true);
        //imgDateOvernight.setImageResource(R.drawable.date);
    }

    private void inactiveButtonOther() {
        boxOther.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
        txtOther.setTextColor(getResources().getColor(R.color.org_20p));
        txtOther.setEnabled(false);
        //imgDateOvernight.setImageResource(R.drawable.date_transparent);
    }

    private void pressButtonOvernightToday() {
        txtOvernightToday.setBackgroundResource(R.drawable.button_full_org_bg);
        txtOvernightToday.setTextColor(getResources().getColor(R.color.wh));
    }

    private void normalButtonOvernightToday() {
        txtOvernightToday.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtOvernightToday.setTextColor(getResources().getColor(R.color.org));
        txtOvernightToday.setEnabled(true);
    }

    private void inactiveButtonOvernightToday() {
        txtOvernightToday.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
        txtOvernightToday.setTextColor(getResources().getColor(R.color.org_20p));
        txtOvernightToday.setEnabled(false);
    }

    private void pressButtonOvernightTomorrow() {
        txtOvernightTomorrow.setBackgroundResource(R.drawable.button_full_org_bg);
        txtOvernightTomorrow.setTextColor(getResources().getColor(R.color.wh));
    }

    private void normalButtonOvernightTomorrow() {
        txtOvernightTomorrow.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtOvernightTomorrow.setTextColor(getResources().getColor(R.color.org));
        txtOvernightTomorrow.setEnabled(true);
    }

    private void inactiveButtonOvernightTomorrow() {
        txtOvernightTomorrow.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
        txtOvernightTomorrow.setTextColor(getResources().getColor(R.color.org_20p));
        txtOvernightTomorrow.setEnabled(false);
    }

    private void pressButtonOvernightOther() {
        boxOvernightOther.setBackgroundResource(R.drawable.button_full_org_bg);
        txtOvernightOther.setTextColor(getResources().getColor(R.color.wh));
        txtOvernightOther.setEnabled(true);
        imgDateOvernight.setImageResource(R.drawable.date);
    }

    private void normalButtonOvernightOther() {
        boxOvernightOther.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtOvernightOther.setTextColor(getResources().getColor(R.color.org));
        txtOvernightOther.setEnabled(true);
        imgDateOvernight.setImageResource(R.drawable.date);
    }

    private void inactiveButtonOvernightOther() {
        boxOvernightOther.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
        txtOvernightOther.setTextColor(getResources().getColor(R.color.org_20p));
        txtOvernightOther.setEnabled(false);
        imgDateOvernight.setImageResource(R.drawable.date_transparent);
    }

    private void pressButtonDailyFromToday() {
        txtDailyFromToday.setBackgroundResource(R.drawable.button_full_org_bg);
        txtDailyFromToday.setTextColor(getResources().getColor(R.color.wh));
        txtDailyFromToday.setEnabled(true);
    }

    private void normalButtonDailyFromToday() {
        txtDailyFromToday.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtDailyFromToday.setTextColor(getResources().getColor(R.color.org));
        txtDailyFromToday.setEnabled(true);
    }

    private void inactiveButtonDailyFromToday() {
        txtDailyFromToday.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
        txtDailyFromToday.setTextColor(getResources().getColor(R.color.org_20p));
        txtDailyFromToday.setEnabled(false);
    }

    private void pressButtonDailyFromTomorrow() {
        txtDailyFromTomorrow.setBackgroundResource(R.drawable.button_full_org_bg);
        txtDailyFromTomorrow.setTextColor(getResources().getColor(R.color.wh));
    }

    private void normalButtonDailyFromTomorrow() {
        txtDailyFromTomorrow.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtDailyFromTomorrow.setTextColor(getResources().getColor(R.color.org));
    }

    private void pressButtonDailyFromOther() {
        boxDailyFromOther.setBackgroundResource(R.drawable.button_full_org_bg);
        txtDailyFromOther.setTextColor(getResources().getColor(R.color.wh));
    }

    private void normalButtonDailyFromOther() {
        boxDailyFromOther.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtDailyFromOther.setTextColor(getResources().getColor(R.color.org));
    }

    private void pressButtonDailyToToday() {
        txtDailyToToday.setBackgroundResource(R.drawable.button_full_org_bg);
        txtDailyToToday.setTextColor(getResources().getColor(R.color.wh));
    }

    private void inactiveButtonDailyToToday() {
        txtDailyToToday.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
        txtDailyToToday.setTextColor(getResources().getColor(R.color.org_20p));
    }

    private void pressButtonDailyToTomorrow() {
        txtDailyToTomorrow.setBackgroundResource(R.drawable.button_full_org_bg);
        txtDailyToTomorrow.setTextColor(getResources().getColor(R.color.wh));
        txtDailyToTomorrow.setEnabled(true);
    }

    private void normalButtonDailyToTomorrow() {
        txtDailyToTomorrow.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtDailyToTomorrow.setTextColor(getResources().getColor(R.color.org));
        txtDailyToTomorrow.setEnabled(true);
    }

    private void inactiveButtonDailyToTomorrow() {
        txtDailyToTomorrow.setBackgroundResource(R.drawable.button_full_wh_bg_org_20_border);
        txtDailyToTomorrow.setTextColor(getResources().getColor(R.color.org_20p));
        txtDailyToTomorrow.setEnabled(false);
    }

    private void pressButtonDailyToOther() {
        boxDailyToOther.setBackgroundResource(R.drawable.button_full_org_bg);
        txtDailyToOther.setTextColor(getResources().getColor(R.color.wh));
    }

    private void normalButtonDailyToOther() {
        boxDailyToOther.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtDailyToOther.setTextColor(getResources().getColor(R.color.org));
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (reservation != null)
//            reservation = null;
//    }
}