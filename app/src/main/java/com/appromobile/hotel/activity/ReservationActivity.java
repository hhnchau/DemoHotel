package com.appromobile.hotel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
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
import com.appromobile.hotel.dialog.CallbackDialag;
import com.appromobile.hotel.dialog.Dialag;
import com.appromobile.hotel.enums.BookingMode;
import com.appromobile.hotel.enums.BookingType;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.request.UserBookingDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.CouponIssuedForm;
import com.appromobile.hotel.model.view.HotelDetailForm;
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
import com.appromobile.hotel.widgets.TextViewSFRegular;
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

    private int roomtypeIndex = 0;
    private RoomTypeForm roomTypeForm;

    private BookingMode bookingMode = BookingMode.HOURLY;

    private SimpleDateFormat dateFormatter;
    private TextView btnChooseCoupon, btnChangeCoupon;
    private ArrayList<CouponIssuedForm> couponIssuedForms;
    private TextView btnClearCoupon;
    private int couponIndex = 0;
    private LinearLayout boxCoupon;
    private int startHour, endHour, firstHour;
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

    private LinearLayout boxStamp;
    private TextView tvValueStamp, tvApplyStamp, btnNumberStamp, btnRedeem, btnNotApply;
    private UserStampForm userStampForm;
    private int redeemValue = 0;
    private boolean autoRedeem = false;

    // -----------------------VARIABLE FOR CLOCK-----------------------
    // current hour base on phone
    private int systemHour;
    // current time mode base on phone
    private String systemTimeMode;
    //choosing AM or PM
    private String chooseTimeMode = "";
    // current date boolean
    private boolean isCurrentDay;
    // check-in	 check-out time
    private int checkIncheckOut;
    // postion on clock
    private int fromPosition, toPosition;
    // mode: check-in or check-out
    private int mode;
    //Check-in
    private static final int CHECK_IN = 1;
    //Check-out
    private static final int CHECK_OUT = 2;


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

                //Click Hourly
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

                //Click Daily
                chkDaily.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (bookingMode != BookingMode.DAILY) {

                            bookingModeDaily();

                            statusDaily = false;

                            tvDateFrom.setText(getSystemDay());

                            getCoupon();

                        }
                        MyLog.writeLog("Calculate 321");
                        calculateFee();
                    }
                });


                //Click Overnight
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

                tvDateTo.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //MyLog.writeLog("Calculate 429");
                        //calculateFee();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                tvDateHourly.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Calendar calendar = Calendar.getInstance();
                        int hour, min;
                        hour = calendar.get(Calendar.HOUR_OF_DAY);
                        min = Utils.roundUp(calendar.get(Calendar.MINUTE), 60);
                        if (min >= 60) {
                            min = 0;
                            hour += 1;
                        }
                        if (hour >= 24) {
                            hour = 0;
                        }
                        //String timeLimit = String.format("%02d", hour) + ":" + String.format("%02d", min);
                        //tvTimeFrom.setText(timeLimit);
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
                        if (endHour - startHour < firstHour) {
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

    private void findUserStampDetail(long sn) {
        ControllerApi.getmInstance().findUserStampFormDetail(this, sn, false, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                userStampForm = (UserStampForm) object;
                if (userStampForm == null || userStampForm.getNumToRedeem() <= 0) {
                    boxStamp.setVisibility(View.GONE);
                } else {

                    boxStamp.setVisibility(View.VISIBLE);
                    tvValueStamp.setText(Utils.formatCurrency(userStampForm.getRedeemValue()) + " " + getString(R.string.vnd));
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
                        //Calculate
                        MyLog.writeLog("Calculate 557");
                        calculateFee();
                        return;
                    }

                    int numActive = userStampForm.getNumStampActive();
                    int numRedeem = userStampForm.getNumToRedeem();
                    if (numActive >= numRedeem) {
                        btnRedeem.setVisibility(View.VISIBLE);
                        btnNumberStamp.setVisibility(View.GONE);
                    } else {
                        btnRedeem.setVisibility(View.GONE);
                        btnNumberStamp.setVisibility(View.VISIBLE);
                        btnNumberStamp.setText(String.valueOf(numActive) + "/" + String.valueOf(numRedeem));
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

    //Return Tomorrow
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
            int totalHour = iTimeTo - iTimeFrom;

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
            if (totalHour > roomTypeForm.getFirstHours()) {
                totalFee = roomTypeForm.getPriceFirstHours() + getAdditionalHours(totalHour - roomTypeForm.getFirstHours());//(totalHour - roomTypeForm.getFirstHours()) * roomTypeForm.getPriceAdditionalHours();
            } else {
                totalFee = roomTypeForm.getPriceFirstHours();
            }

            // Update UI
            if (endHour - startHour < firstHour) {
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

                if (couponIssuedForms.get(couponIndex).getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
                    //Discount Percent
                    int percent = (couponIssuedForms.get(couponIndex).getDiscount() * totalFee) / 100;
                    int maxDiscount = couponIssuedForms.get(couponIndex).getMaxDiscount();
                    //Check Coupon MaxDiscount
                    if (maxDiscount == 0 || percent <= maxDiscount) {
                        feeDiscount = percent;
                    } else if (percent > maxDiscount) {
                        feeDiscount = maxDiscount;
                        /*
                        / Show Toast MaxDiscount ---> Remove
                        */
//                        if (couponIndex != NO_COUPON) {
//                            int overDiscount = (maxDiscount * 100) / couponIssuedForms.get(couponIndex).getDiscount();
//
//                            Toast.makeText(ReservationActivity.this, String.format(getString(R.string.msg_3_9_maximun_coupon_value_1), Utils.formatCurrency(overDiscount)) + " " + String.format(getString(R.string.msg_3_9_maximun_coupon_value_2), Utils.formatCurrency(maxDiscount)), Toast.LENGTH_LONG).show();
//                        }
                    }
                } else {
                    // discount money
                    feeDiscount = couponIssuedForms.get(couponIndex).getDiscount();
                }

                bundle.putInt("discountFee", feeDiscount);
            } else {
                bundle.putInt("discountFee", NO_COUPON);
            }
        } else {
            bundle.putInt("discountFee", NO_COUPON);
        }

        // TotalFee
        bundle.putInt("totalFee", totalFee);

        totalFee = totalFee - feeDiscount;

        //Set Stamp Apply
        if (redeemValue > 0 && userStampForm != null) {
            if (bookingMode == BookingMode.HOURLY && userStampForm.isRedeemHourly() ||
                    bookingMode == BookingMode.OVERNIGHT && userStampForm.isRedeemOvernight() ||
                    bookingMode == BookingMode.DAILY && userStampForm.isRedeemDaily()) {
                totalFee = totalFee - redeemValue;
            }
        }
        //Add Redeem
        bundle.putInt("redeemValue", redeemValue);

        //Check total Fee
        if (bookingMode == BookingMode.HOURLY && !guiState) {
            if (endHour == 0) {
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

    //Calculator Additional Hours
    private int getAdditionalHours(int addtition) {
        int addHour;
        if (addtition % roomTypeForm.getAdditionalHours() > 0) {
            addHour = ((addtition / roomTypeForm.getAdditionalHours()) * roomTypeForm.getPriceAdditionalHours()) + roomTypeForm.getPriceAdditionalHours();
        } else {
            addHour = (addtition / roomTypeForm.getAdditionalHours()) * roomTypeForm.getPriceAdditionalHours();
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
                                if (couponIssuedForms.get(i).getCanUse() == ParamConstants.CAN_USE) {
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
                                tvCoupon.setText(couponIssuedForms.get(couponIndex).getPromotionName() + "\n" + String.valueOf(Utils.formatCurrency(couponIssuedForms.get(couponIndex).getDiscount())) + getString(R.string.currency));
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
                        startOverNight = rs.getStartOvernight();
                        endOvernight = rs.getEndOvernight();

                        //Init Data
                        initData();

                    }
                });
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
            //firstHour = hotelDetailForm.getRoomTypeList().get(roomtypeIndex).getFirstHours();
            firstHour = roomTypeForm.getFirstHours();
        }

        final Calendar calendar = Calendar.getInstance();
        int hour, min;
        //Get hour
        hour = calendar.get(Calendar.HOUR_OF_DAY);

        /*
        / Store AM/PM
        */
        systemHour = hour;
        if (systemHour < 12) {
            systemTimeMode = "AM";
        } else {
            systemTimeMode = "PM";
        }

        //Set current day
        isCurrentDay = true;

        //Get min
        min = Utils.roundUp(calendar.get(Calendar.MINUTE), 60);

        /*
        / Round Min
        */
        if (min >= 60) {
            hour += 1;
        }
        if (hour >= 24) {
            hour = 0;
        }

        //Hour

        String timeFromLimit;
        /*
        / Set Default Start Hour
        */
        startHour = hour;

        //Check add "0"
        if (startHour < 10) {
            timeFromLimit = "0" + startHour + ":00";
        } else {
            timeFromLimit = startHour + ":00";
        }

        /*
        / Set Default End Hour
        */
        endHour = startHour + 1;


        String timeToLimit;

        //Check 23hour
        if (startHour == 0 && isCurrentDay && systemHour == 23) {
            timeToLimit = "00:00";
            //Set totalFee = 0
            endHour = 0;
            guiState = false;
            //Hide Make Reservation
            btnReservation.setVisibility(View.GONE);
        } else {

            //endHour = 10
            if (endHour < 10) {
                timeToLimit = "0" + endHour + ":00";
                //endHour = 24
            } else if (endHour == 24) {
                timeToLimit = "00:00";
            } else {
                //Other
                timeToLimit = endHour + ":00";
                //Show Make Reservation
                btnReservation.setVisibility(View.VISIBLE);
            }
        }
        MyLog.writeLog("ResevationActivity ---> initDataMyPageFragment: startHour: " + timeFromLimit + " <-----> endHour: " + timeToLimit);

        //Update View
        tvTimeFrom.setText(timeFromLimit);
        tvTimeTo.setText(timeToLimit);

        //Check hotel limit 2 hour
        if (endHour - startHour < firstHour && startHour != 0 && bookingMode == BookingMode.HOURLY) {
            guiState = hideBtnReservation();
        }

        /*
        / Set current day
        */
        tvDateHourly.setText(dateFormatter.format(calendar.getTime()));

        /*
        / Check Flash Sale
        */
        if (checkRoomTypeFlashSale()) {
            bookingModeOvernight();
            boxCoupon.setVisibility(View.GONE);

            setViewFlashSale();
        } else {
            bookingModeHourly();
            getCoupon();
        }

        MyLog.writeLog("Calculate 1180");
        calculateFee();


        tvDateFrom.setText(dateFormatter.format(calendar.getTime()));
        tvDateOvernight.setText(dateFormatter.format(calendar.getTime()));

        calendar.add(Calendar.DATE, 1);
        tvDateTo.setText(dateFormatter.format(calendar.getTime()));
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
            //TODAY
            case R.id.textView_rerservation_today:

                /*
                / Set Button
                */
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
                normalButtonToday();
                pressButtonTomorrow();
                normalButtonOther();

                checkHourlyReservation(getTomorrowDay());

                getCoupon();

                //Enable calculate
                guiState = true;

                break;
            //OTHER
            case R.id.textView_rerservation_other:

                /*
                / Set Button
                */
                normalButtonToday();
                normalButtonTomorrow();
                pressButtonOther();
                //Show Calendar
                changeDateHourly();

                //Enable calculate
                guiState = true;

                break;
            case R.id.btnTimeFrom:
                //Set mode Check-in
                mode = CHECK_IN;
                if (startHour == 0 && isCurrentDay && systemHour == 23) {
                    Toast.makeText(ReservationActivity.this, getString(R.string.msg_3_9_overnight_time), Toast.LENGTH_LONG).show();
                } else {
                    showClock();
                }
                break;
            case R.id.btnTimeTo:
                //Set Moce Check-out
                mode = CHECK_OUT;
                if (startHour == 0 && isCurrentDay && systemHour == 23) {
                    Toast.makeText(ReservationActivity.this, getString(R.string.msg_3_9_overnight_time), Toast.LENGTH_LONG).show();
                } else if (endHour == 24) {
                    endHour = 0;
                    showClock();
                } else {
                    showClock();
                }
                break;
            case R.id.btnMakeReservation:
                if (hotelDetailForm.getRoomTypeList().get(roomtypeIndex).isLocked()) {
                    Toast.makeText(ReservationActivity.this, getString(R.string.msg_3_1_soldout_room), Toast.LENGTH_LONG).show();
                } else {
                    if (checkStampEnough(userStampForm) && redeemValue == 0 && !checkRoomTypeFlashSale()) {
                        showDialogStamp();
                    } else {
                        checkOtherCondition();
                    }
                }

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
            //OVERNIGHT TODAY
            case R.id.textView_rerservation_overnight_today:
                if (statusRightNow) {

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

                    tvDateOvernight.setText(getSystemDay());
                    tvDateOvernight.setTag("RightNow");

                }
                break;
            //OVERNIGHT TOMORROW
            case R.id.textView_rerservation_overnight_tomorrow:

                /*
                / Set Button
                */
                if (statusRightNow) {
                    normalButtonOvernightToday();
                } else {
                    inactiveButtonvernightToday();
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

                tvDateOvernight.setText(getSystemDay());
                tvDateOvernight.setTag("Tonight");

                break;
            //OVERNIGHT OTHER
            case R.id.textView_rerservation_overnight_other:

                /*
                / Set Button
                */
                if (statusRightNow) {
                    normalButtonOvernightToday();
                } else {
                    inactiveButtonvernightToday();
                }
                normalButtonOvernightTomorrow();
                pressButtonOvernightOther();

                changeDateOvernight();
                tvDateOvernight.setTag("Other");

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

                break;
            //DAILY TODAY TO
//            case R.id.textView_rerservation_daily_to_today:
//                break;
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

                break;
        }
    }

    private void checkOtherCondition() {
        if (bookingMode == BookingMode.HOURLY) {
            if (checkOvernightHourly(startHour)) {
                if (checkCouponCondition()) {
                    postReservation();
                }
            }
        } else {
            if (checkCouponCondition()) {
                postReservation();
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

    private void postReservation() {

        final UserBookingDto userBookingDto = getUserBookingDto();

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
                payNow(userBookingDto);
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
                payNow(userBookingDto);
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
        if (userStampForm != null && userStampForm.getNumStampActive() >= userStampForm.getNumToRedeem()) {
            result = true;
        }
        return result;
    }

    private void showDialogStamp() {
        Dialag.getInstance().show(this, false, true, true, null, getString(R.string.msg_6_12_stamp_redeemed_asking), getString(R.string.txt_6_12_stamp_continue), getString(R.string.txt_6_12_stamp_redeem), null, Dialag.BTN_LEFT, new CallbackDialag() {
            @Override
            public void button1() {
                checkOtherCondition();
            }

            @Override
            public void button2() {
                //Add Redeem
            if (couponIndex == NO_COUPON){
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
                Toast.makeText(ReservationActivity.this, getString(R.string.msg_6_12_stamp_coupon_is_using), Toast.LENGTH_SHORT).show();
                return;
            }

                //Redeem
                redeemValue = userStampForm.getRedeemValue();
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

        ControllerApi.getmInstance().createNewReservation(this, userBookingDto, new ResultApi() {
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

    private void showClock() {
        // Initialize Clock Dialog
        final Dialog dialog = new Dialog(this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.time_picker_dialog);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                String from, to;
                if (endHour <= startHour) {
                    endHour = startHour + 1;
                }
                if (startHour < 10) {
                    from = "0" + startHour + ":00";
                } else {
                    from = startHour + ":00";
                }
                if (endHour < 10) {
                    to = "0" + endHour + ":00";
                } else {
                    if (endHour == 24) {
                        to = "00:00";
                    } else {
                        to = endHour + ":00";
                    }
                }

                tvTimeFrom.setText(from);
                tvTimeTo.setText(to);

                getCoupon();

            }
        });
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }

        final TextViewSFRegular tvCheckIn = dialog.findViewById(R.id.textView_check_in);
        final TextViewSFRegular tvCheckOut = dialog.findViewById(R.id.textView_check_out);
        final TextViewSFRegular btnOK = dialog.findViewById(R.id.btnOk);
        final ImageView chkAPM = dialog.findViewById(R.id.chkAPM);

        final TextViewSFRegular[] numbers = new TextViewSFRegular[12];

        numbers[0] = dialog.findViewById(R.id.tvNumber0);
        numbers[1] = dialog.findViewById(R.id.tvNumber1);
        numbers[2] = dialog.findViewById(R.id.tvNumber2);
        numbers[3] = dialog.findViewById(R.id.tvNumber3);
        numbers[4] = dialog.findViewById(R.id.tvNumber4);
        numbers[5] = dialog.findViewById(R.id.tvNumber5);
        numbers[6] = dialog.findViewById(R.id.tvNumber6);
        numbers[7] = dialog.findViewById(R.id.tvNumber7);
        numbers[8] = dialog.findViewById(R.id.tvNumber8);
        numbers[9] = dialog.findViewById(R.id.tvNumber9);
        numbers[10] = dialog.findViewById(R.id.tvNumber10);
        numbers[11] = dialog.findViewById(R.id.tvNumber11);

        /*
        / Button OK
        */
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.relative_picker_clock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
            }
        });

        /*
        /
        */
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextViewSFRegular btnClicked = (TextViewSFRegular) view;
                //Store click

                    /*
                    / Number Click
                    */

                int timeSelect = Integer.parseInt(btnClicked.getText().toString());
                if (chooseTimeMode.equals("PM")) {
                    timeSelect = timeSelect + 12;
                }

                //CHECK_IN
                if (mode == CHECK_IN) {

                    if (startOverNight > endOvernight) {
                        if (timeSelect >= startOverNight || timeSelect < endOvernight) {
                            Toast.makeText(ReservationActivity.this, R.string.msg_3_9_overnight_time, Toast.LENGTH_LONG).show();
                            return;
                        }
                    } else if (startOverNight < endOvernight) {
                        //startOverNight < endOvernight
                        if (timeSelect >= startOverNight && timeSelect <= endOvernight) {
                            Toast.makeText(ReservationActivity.this, R.string.msg_3_9_overnight_time, Toast.LENGTH_LONG).show();
                            return;
                        }

                    }
                    //CHECK_OUT
                } else {


                    if (startOverNight > endOvernight) {
                        if (timeSelect > startOverNight || timeSelect <= endOvernight) {

                            Toast.makeText(ReservationActivity.this, R.string.msg_3_9_overnight_time, Toast.LENGTH_LONG).show();
                            return;

                        }
                    } else if (startOverNight < endOvernight) {
                        //startOverNight < endOvernight
                        if (timeSelect > startOverNight && timeSelect < endOvernight) {
                            Toast.makeText(ReservationActivity.this, R.string.msg_3_9_overnight_time, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }


                if (mode == CHECK_IN) {
                    numbers[fromPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                    numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                    fromPosition = Integer.parseInt(btnClicked.getText().toString());
                    if (fromPosition == 12) {
                        fromPosition = 0;
                    }
                    if (chooseTimeMode.equals("AM")) {
                        startHour = fromPosition;
                    } else {
                        startHour = fromPosition + 12;
                    }
//                    numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
//                    numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this R.color.wh));
                    tvCheckOut.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.org));
                    tvCheckIn.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.org_20p));

                    mode = CHECK_OUT;
                    if (startHour == 23) {
                        //Check tomorrow
                        chkAPM.setBackgroundResource(R.drawable.am);
                        chooseTimeMode = "AM";
                        chkAPM.setEnabled(false);

                        for (int i = 0; i < 12; i++) {
                            numbers[i].setEnabled(false);
                            numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                        }

                        return;
                    } else if (fromPosition < 11) {
                        toPosition = fromPosition + 1;
                        for (int i = 0; i < toPosition; i++) {
                            numbers[i].setEnabled(false);
                            numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                        }
                        numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg_80);
                        numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                    } else {
                        for (int i = 0; i < 12; i++) {
                            numbers[i].setEnabled(true);
                            numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                        }
                        numbers[fromPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                        toPosition = 0;
                    }

                    if (startHour >= 11) {
                        chkAPM.setBackgroundResource(R.drawable.pm);
                        chooseTimeMode = "PM";
                        chkAPM.setEnabled(false);
                    }
                    numbers[toPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                    numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                    endHour = startHour + 1;
                } else {
                    numbers[toPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                    numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                    toPosition = Integer.parseInt(btnClicked.getText().toString());
                    if (toPosition == 12) {
                        toPosition = 0;
                    }
                    if (chooseTimeMode.equals("AM")) {
                        endHour = toPosition;
                    } else {
                        endHour = toPosition + 12;
                    }
                    numbers[toPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                    numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                }
            }

        };
        /*
        / Check-In
        */
        tvCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode != CHECK_IN) {
                    mode = CHECK_IN;
                    if (systemTimeMode.equals("AM")) {
                        chkAPM.setEnabled(true);
                    }
                    if (!isCurrentDay) {
                        chkAPM.setEnabled(true);
                    }
                    for (int i = 0; i < 12; i++) {
                        numbers[i].setEnabled(true);
                        numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                    }

                    if (startHour <= 11) {
                        chkAPM.setBackgroundResource(R.drawable.am);
                        chooseTimeMode = "AM";
                    } else {
                        chkAPM.setBackgroundResource(R.drawable.pm);
                        chooseTimeMode = "PM";
                    }

                    tvCheckIn.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.org));
                    tvCheckOut.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.org_20p));

                    numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                    numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                    numbers[fromPosition].setEnabled(true);
                    numbers[toPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                    numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));

                    if (systemTimeMode.equals(chooseTimeMode) && isCurrentDay) {
                        int temp;
                        if (systemHour < 12) {
                            temp = systemHour;
                        } else {
                            temp = systemHour - 12;
                        }
                        for (int i = 0; i <= temp; i++) {
                            numbers[i].setEnabled(false);
                            numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                        }
                    }
                }
            }
        });

        /*
        / Check-Out
        */
        tvCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode != CHECK_OUT) {
                    mode = CHECK_OUT;

                    tvCheckIn.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.org_20p));
                    tvCheckOut.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.org));

                    //Set AM/PM
                    if (endHour > 12) {
                        chooseTimeMode = "PM";
                    }

                    if (endHour <= startHour) {
                        endHour = startHour + 1;
                        if (endHour > 11) {
                            toPosition = endHour - 12;
                        } else {
                            toPosition = endHour;
                        }
                    }

                    if ((startHour < 12 && endHour < 12) || (startHour > 11 && endHour > 11)) {
                        for (int i = 0; i <= fromPosition; i++) {
                            numbers[i].setEnabled(false);
                            numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                        }
                        numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg_80);
                        numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                        if (endHour < 12) {
                            chkAPM.setBackgroundResource(R.drawable.am);
                        } else {
                            chkAPM.setBackgroundResource(R.drawable.pm);
                            chkAPM.setEnabled(false);
                        }
                    } else if (startHour < 12 && endHour > 11) {
                        chkAPM.setBackgroundResource(R.drawable.pm);
                        for (int i = 0; i <= fromPosition; i++) {
                            numbers[i].setEnabled(true);
                            numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                        }
                        numbers[fromPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                        numbers[toPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                    }

                    if (endHour > 11) {
                        if (endHour == 24) {
                            return;
                        } else {
                            toPosition = endHour - 12;
                        }
                    } else {
                        toPosition = endHour;
                    }
                    numbers[toPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                    numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                }
            }
        });

        /*
        / btnToggel
        */

        chkAPM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //AM -->PM
                if (chooseTimeMode.equals("AM")) {
                    chkAPM.setBackgroundResource(R.drawable.pm);
                    chooseTimeMode = "PM";

                    /*
                    / CHECk-IN AM-->PM
                    */
                    if (mode == CHECK_IN) {

                        //Enable Select
                        if (systemHour > 12 && isCurrentDay) {
                            for (int i = 0; i < systemHour - 12; i++) {
                                numbers[i].setEnabled(false);
                                numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                            }
                        } else {
                            //show full clocl
                            for (int i = 0; i < 12; i++) {
                                numbers[i].setEnabled(true);
                                numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk));
                            }
                        }

                        //Show number select
                        if (startHour > 12) {
                            numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                            numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                        } else {
                            numbers[fromPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                            numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                        }

                        //Check out AM-->PM----------------------------------------------------------------
                    } else {

                        numbers[fromPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                        //set hour from
                        for (int i = 0; i <= fromPosition; i++) {
                            numbers[i].setEnabled(true);
                            numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                        }

                        //set hour to
                        if (endHour > 12) {
                            numbers[toPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                            numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                        } else {
                            numbers[toPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                            numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                        }
                    }

                    /*
                     / CHECkIN PM-->AM
                     */
                } else {
                    chkAPM.setBackgroundResource(R.drawable.am);
                    chooseTimeMode = "AM";
                    //    Show clock
                    if (mode == CHECK_IN && isCurrentDay) {
                        if (systemHour >= 12) {
                            for (int i = 0; i <= systemHour - 12; i++) {
                                numbers[i].setEnabled(false);
                                numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                            }
                        } else {
                            for (int i = 0; i <= systemHour; i++) {
                                numbers[i].setEnabled(false);
                                numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                            }
                        }

                        if (startHour > 12) {
                            //PM -->Hide clicked
                            numbers[fromPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                            //Check same number
                            if (startHour > systemHour + 12) {
                                numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                            }

                        } else {
                            //AM -->Show
                            numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                            numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                        }

                    } else if (mode == CHECK_IN && !isCurrentDay) {
                        //Check-in tomorrow AM
                        if (startHour > 12) {
                            //PM -->Hide clicked
                            numbers[fromPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                            numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                        } else {
                            //AM -->Show
                            numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                            numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                        }


                        //Check-out PM --> AM
                    } else if (mode == CHECK_OUT) {
                        if (startHour < 12) {

                            for (int i = 0; i <= fromPosition; i++) {
                                numbers[i].setEnabled(false);
                                numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                            }

                            if (startHour > 12) {
                                numbers[toPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                                numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                            } else {
                                numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg_80);
                                numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                            }

                            if (endHour > 12) {
                                numbers[toPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                                numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                            } else {
                                numbers[toPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                                numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                            }

                        }
                    }
                }
            }
        });

        /*
        / Load Default
        */

        if (mode == CHECK_IN) {
            //Check AM/PM
            if (startHour > 11) {
                fromPosition = startHour - 12;
                chooseTimeMode = "PM";
                chkAPM.setBackgroundResource(R.drawable.pm);
                //Disable tog
                if (isCurrentDay && systemHour >= 12) {
                    chkAPM.setEnabled(false);
                }
            } else {
                fromPosition = startHour;
                chooseTimeMode = "AM";
                chkAPM.setBackgroundResource(R.drawable.am);
            }

            //SystemTimeMode initDataMyPageFragment
            //chooseTimeMode showClock
            if (systemTimeMode.equals(chooseTimeMode) && isCurrentDay) {

                //Show number
                if (systemHour < 12) {
                    for (int i = 0; i <= systemHour; i++) {
                        numbers[i].setEnabled(false);
                        numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                    }
                } else {
                    for (int i = 0; i <= systemHour - 12; i++) {
                        numbers[i].setEnabled(false);
                        numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                    }

                }
            }

            //Show number Clock
            numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
            numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));

            //mode == CHECK_OUT
        } else {
            tvCheckIn.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.org_20p));
            tvCheckOut.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.org));

                /*
                / ClockCheckOut press  < 12h
                */
            if (endHour < 12) {
                if (startHour > 11) {
                    fromPosition = startHour - 12;
                } else {
                    fromPosition = startHour;
                }

                toPosition = endHour;
                for (int i = 0; i <= fromPosition; i++) {
                    numbers[i].setEnabled(false);
                    numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                }

                //Disable tog
                if (endHour == 0) {
                    chkAPM.setEnabled(false);
                }

                chkAPM.setBackgroundResource(R.drawable.am);
                chooseTimeMode = "AM";
                if (endHour != 0) {
                    numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg_80);
                    numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                    numbers[toPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                    numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                }

                /*
                / ClockCheckOut press  >12h
                */
            } else {
                chkAPM.setBackgroundResource(R.drawable.pm);
                chooseTimeMode = "PM";

                //Disable tog
                if (startHour > 12) {
                    chkAPM.setEnabled(false);
                }
                if (startHour >= 12) {
                    fromPosition = startHour - 12;
                    toPosition = endHour - 12;
                    for (int i = 0; i <= fromPosition; i++) {
                        numbers[i].setEnabled(false);
                        numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.bk_15p));
                    }
                    numbers[fromPosition].setBackgroundResource(R.drawable.circle_clicked_bg_80);
                    numbers[fromPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                    numbers[toPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                    numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                } else {

                    for (int i = 0; i < 12; i++) {
                        numbers[i].setEnabled(true);
                        numbers[i].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                    }
                    numbers[toPosition].setBackgroundResource(R.drawable.circle_clicked_bg);
                    numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.wh));
                }
                if (endHour > startOverNight) {
                    numbers[toPosition].setBackgroundResource(R.drawable.circle_click_no_bg);
                    numbers[toPosition].setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                }
            }
        }

        for (TextViewSFRegular number : numbers) {
            number.setOnClickListener(listener);
        }

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
        //Check current day
        if (ReservationActivity.this.getSystemDay().equals(date)) {
            isCurrentDay = true;

            if (startHour <= systemHour || endHour <= systemHour) {
                startHour = systemHour + 1;
                endHour = startHour + 1;

                String from, to;

                //Check Add "0"
                if (startHour < 10) {
                    from = "0" + startHour + ":00";
                } else {
                    if (startHour >= 24) {
                        startHour = 0;
                        from = "0" + startHour + ":00";
                    } else {
                        from = startHour + ":00";
                    }
                }

                //Check Add "0"
                if (endHour < 10) {
                    to = "0" + endHour + ":00";
                } else {
                    if (endHour >= 24) {
                        to = "00" + ":00";
                    } else {
                        to = endHour + ":00";
                    }
                }

                //Check 23hour
                if (startHour == 0 && systemHour == 23) {
                    //Set totalFee = 0
                    endHour = 0;
                    guiState = false;
                    //Hide Make Reservation
                    btnReservation.setVisibility(View.GONE);
                }

                tvTimeFrom.setText(from);
                tvTimeTo.setText(to);
            }
        } else {
            isCurrentDay = false;

            //Check sytem hour = 23 ---> endhour + 1 ---->enable booking
            if (startHour == 0 && systemHour == 23) {
                tvTimeTo.setText("01:00");
                endHour = startHour + 1;
            }

            btnReservation.setVisibility(View.VISIBLE);
        }


        //Check hotel limit 2 hour
        if (endHour - startHour < firstHour && startHour != 0 && bookingMode == BookingMode.HOURLY) {
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
                    deleteStamp();
                    initData();
                } else {
                    if (userStampForm == null) {
                        findUserStampDetail(hotelDetailForm.getSn());
                    } else {
                        boxStamp.setVisibility(View.VISIBLE);
                        if (autoRedeem) {
                            redeemValue = userStampForm.getRedeemValue();
                            changeModeBookingForStamp(userStampForm);
                        }
                    }
                    deleteStamp();
                    changeBookingMode();
                    getCoupon();
                    MyLog.writeLog("Calculate 2779");
                    calculateFee();
                }
            }
        } else if (requestCode == CHOOSE_COUPON) {
            if (resultCode == RESULT_OK) {
                couponIndex = data.getIntExtra("CouponIndex", -1);
                try {
                    if (couponIndex >= 0) {
                        //press Change Coupon
                        /*
                        / Check Coupon
                        */
                        if (couponIssuedForms.get(couponIndex).getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
                            tvCoupon.setText(couponIssuedForms.get(couponIndex).getPromotionName() + "\n" + getString(R.string.discount) + " " + String.valueOf(Utils.formatCurrency(couponIssuedForms.get(couponIndex).getDiscount())) + " " + getString(R.string.percent));
                        } else {
                            tvCoupon.setText(couponIssuedForms.get(couponIndex).getPromotionName() + "\n" + String.valueOf(Utils.formatCurrency(couponIssuedForms.get(couponIndex).getDiscount())) + getString(R.string.currency));
                        }
                        btnClearCoupon.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    MyLog.writeLog("couponIndex------------>" + e);
                }
                MyLog.writeLog("Calculate 2804");
                calculateFee();
            }
        } else if (requestCode == LOGIN_RESERVATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                postReservation();
            }
        } else if (requestCode == LOGIN_COUPON_REQUEST) {
            if (resultCode == RESULT_OK) {
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

        /*
        / Set Button
        */
        pressButtonToday();
        normalButtonTomorrow();
        normalButtonOther();

        checkHourlyReservation(getSystemDay());

        /*
        / Set Stamp
        */
        if (redeemValue > 0) {
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
                pressButtonOvernightToday();
                normalButtonOvernightTomorrow();
                normalButtonOvernightOther();

                tvDateOvernight.setText(getSystemDay());
                tvDateOvernight.setTag("RightNow");
                //Set StatusOvernight
                statusRightNow = true; //When:   0 <= HourSelect <= endOvernight
            } else {
                inactiveButtonvernightToday();
                pressButtonOvernightTomorrow();
                normalButtonOvernightOther();

                tvDateOvernight.setText(getSystemDay());
                tvDateOvernight.setTag("Tonight");
                //Set StatusOvernight
                statusRightNow = false;
            }
        } else {
            if (!checkRoomTypeFlashSale() && startOverNight <= hour && hour <= endHour) {
                pressButtonOvernightToday();
                normalButtonOvernightTomorrow();
                normalButtonOvernightOther();

                tvDateOvernight.setText(getSystemDay());
                tvDateOvernight.setTag("RightNow");
                //Set StatusOvernight
                statusRightNow = true; //When:   0 <= HourSelect <= endOvernight
            } else {
                inactiveButtonvernightToday();
                pressButtonOvernightTomorrow();
                normalButtonOvernightOther();

                tvDateOvernight.setText(getSystemDay());
                tvDateOvernight.setTag("Tonight");
                //Set StatusOvernight
                statusRightNow = false;
            }
        }


        /*
        / Set Stamp
        */
        if (redeemValue > 0) {
            setViewStamp(userStampForm);
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
    }

    private boolean checkRoomTypeFlashSale() {
        boolean result = false;
        if (hotelDetailForm.getRoomTypeList().get(roomtypeIndex).isFlashSale()) {
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

    private void changeBookingMode(){
        if (bookingMode == BookingMode.HOURLY) {
            bookingModeHourly();
        }else if (bookingMode == BookingMode.OVERNIGHT) {
            bookingModeOvernight();
        }else if (bookingMode == BookingMode.DAILY) {
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
    }

    private void normalButtonToday() {
        txtToday.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtToday.setTextColor(getResources().getColor(R.color.org));
    }

    private void pressButtonTomorrow() {
        txtTomorrow.setBackgroundResource(R.drawable.button_full_org_bg);
        txtTomorrow.setTextColor(getResources().getColor(R.color.wh));
    }

    private void normalButtonTomorrow() {
        txtTomorrow.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtTomorrow.setTextColor(getResources().getColor(R.color.org));
    }

    private void pressButtonOther() {
        boxOther.setBackgroundResource(R.drawable.button_full_org_bg);
        txtOther.setTextColor(getResources().getColor(R.color.wh));
    }

    private void normalButtonOther() {
        boxOther.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtOther.setTextColor(getResources().getColor(R.color.org));
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

    private void inactiveButtonvernightToday() {
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
    }

    private void normalButtonDailyFromToday() {
        txtDailyFromToday.setBackgroundResource(R.drawable.button_full_wh_bg_org_border);
        txtDailyFromToday.setTextColor(getResources().getColor(R.color.org));
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
}