package com.appromobile.hotel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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
import com.appromobile.hotel.api.controllerApi.ControllerApi;
import com.appromobile.hotel.api.controllerApi.ResultApi;
import com.appromobile.hotel.api.controllerApi.ResultMapApi;
import com.appromobile.hotel.enums.ContractType;
import com.appromobile.hotel.model.request.UserBookingDto;
import com.appromobile.hotel.model.view.ApiSettingForm;
import com.appromobile.hotel.model.view.CouponConditionForm;
import com.appromobile.hotel.model.view.PaymentInfoForm;
import com.appromobile.hotel.model.view.PayooInfoForm;
import com.appromobile.hotel.model.view.RestResult;
import com.appromobile.hotel.model.view.UserBookingForm;
import com.appromobile.hotel.payoo.CreateOrderResponse;
import com.appromobile.hotel.payoo.PayooHandle;
import com.appromobile.hotel.utils.DialogUtils;
import com.appromobile.hotel.utils.MyLog;
import com.appromobile.hotel.utils.ParamConstants;
import com.appromobile.hotel.utils.Utils;
import com.appromobile.hotel.widgets.TextViewSFBold;
import com.appromobile.hotel.widgets.TextViewSFRegular;
import com.google.gson.Gson;

import java.util.Map;

import vn.payoo.paymentsdk.PayooPaymentSDK;
import vn.payoo.paymentsdk.data.model.response.ResponseData;
import vn.payoo.paymentsdk.data.model.response.ResponseObject;
import vn.payoo.paymentsdk.data.model.type.GroupType;


public class Billing_Information extends BaseActivity implements View.OnClickListener {
    private ImageView imgBack;
    private TextView txtTitle, txtFee, txtDiscount, txtStamp, txtPoint, txtTotal, tvPromotionPaymentTitle, tvPromotionPaymentValue;
    private LinearLayout lnHotel, lnCard, lnPayoo, boxPromotionPayment;
    private ImageView imgPromotionPayAtHotel, imgPromotionPay123, imgPromotionPayoo;
    private Bundle bundle;
    public static Activity billingInformation;
    private int minPrice;
    private int hotelStatus;
    private boolean isFlashSale = false;
    private static final int PAY123 = 1;
    private static final int PAYOO = 2;
    private String transactionId;
    private boolean isNewPayment = false;
    private long _userBookingSn = 0;
    private String methodPayment;
    private CouponConditionForm couponConditionForm;

    private String FLAG_SHOW_REWARD_CHECKIN = "FLAG_SHOW_REWARD_CHECKIN";

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
        String action = getIntent().getAction();

        isNewPayment = action != null && action.equals("New_Payment");
        bundle = getIntent().getBundleExtra("InformationBilling");
        hotelStatus = bundle.getInt("HOTEL_STATUS");
        methodPayment = bundle.getString("METHOD_PAYMENT");

        String mapString = bundle.getString("MAP-INFO");
        if (mapString != null && !mapString.equals("")) {
            Gson gson = new Gson();
            couponConditionForm = gson.fromJson(mapString, CouponConditionForm.class);
        }


        imgBack = findViewById(R.id.imageView_billing_infomation_back);
        imgBack.setOnClickListener(this);
        txtTitle = findViewById(R.id.textView_billing_infomation_title);
        txtFee = findViewById(R.id.textView_billing_infomation_fee);
        txtDiscount = findViewById(R.id.textView_billing_infomation_discount);
        txtStamp = findViewById(R.id.textView_billing_infomation_stamp);
        txtPoint = findViewById(R.id.textView_billing_infomation_point);
        txtTotal = findViewById(R.id.textView_billing_infomation_total);
        lnHotel = findViewById(R.id.linear_billing_infomation_pay_at_hotel);
        lnHotel.setOnClickListener(this);

        lnPayoo = findViewById(R.id.linear_billing_infomation_payoo);
        lnPayoo.setOnClickListener(this);
        //lnPayoo.setVisibility(View.GONE);

        boxPromotionPayment = findViewById(R.id.boxPromotionPayment);
        lnCard = findViewById(R.id.linear_billing_infomation_local_card);

        tvPromotionPaymentTitle = findViewById(R.id.tvPromotionPaymentTitle);
        tvPromotionPaymentValue = findViewById(R.id.tvPromotionPaymentValue);
        imgPromotionPayAtHotel = findViewById(R.id.imgPromotionPayAtHotel);
        imgPromotionPay123 = findViewById(R.id.imgPromotionPay123);
        imgPromotionPayoo = findViewById(R.id.imgPromotionPayoo);

        /*
        /Check Flash Sale
        */
        isFlashSale = bundle.getBoolean("FLASH_SALE");
        if (isFlashSale) {

            Toast.makeText(this, getString(R.string.msg_3_9_flashsale_pay_in_advance), Toast.LENGTH_LONG).show();

            hidePayAtHotel();
        }

        /*
        / Check Old Payment
        */
        if (!isNewPayment) {
            hidePayAtHotel();
        }

        /*
        * Check Only PayOnline
        */
        if (methodPayment != null && methodPayment.equals(ParamConstants.METHOD_ALWAYS_PAY_ONLINE) || methodPayment != null && methodPayment.equals(ParamConstants.METHOD_PAY_ONLINE_IN_DAY)) {
            hidePayAtHotel();
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
                if (isNewPayment) {
                    checkMinPriceBeforeMakeReservation(PAY123);
                } else {
                    _userBookingSn = bundle.getInt("userBookingSn");
                    findPaymentInfoFormMap(PAY123, _userBookingSn);
                }
                break;
            case R.id.linear_billing_infomation_payoo:
                if (isNewPayment) {
                    checkMinPriceBeforeMakeReservation(PAYOO);
                } else {
                    _userBookingSn = bundle.getInt("userBookingSn");
                    findPaymentInfoFormMap(PAYOO, _userBookingSn);
                }
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
    private void checkMinPriceBeforeMakeReservation(int type) {

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
                if (type == PAY123) {
                    createReservationforPayOnline(PAY123);
                } else {
                    createReservationforPayOnline(PAYOO);
                }
            }
        }

    }


    private void setView() {
        if (bundle != null) {

            //Fee
            int totalFee = bundle.getInt("totalFee");
            txtFee.setText(Utils.formatCurrency(totalFee) + " VNĐ");

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

            //Point
            int pointValue = bundle.getInt("pointValue");
            if (pointValue > 0) {
                txtPoint.setText("-" + Utils.formatCurrency(pointValue) + " VNĐ");
            } else {
                txtPoint.setText("0 VNĐ");
            }

            //Total
            int total = bundle.getInt("total");
            txtTotal.setText(Utils.formatCurrency(total) + " VNĐ");

            //Promotion Label
            if (couponConditionForm != null && couponConditionForm.getDiscount() > 0) {
                tvPromotionPaymentTitle.setText(handlePromotionPayment(couponConditionForm.getPaymentMethod()));

                tvPromotionPaymentValue.setText(handlePromotionValue(totalFee, total));
            } else {
                boxPromotionPayment.setVisibility(View.GONE);
            }
        }
    }

    private String handlePromotionValue(int totalFee, int total) {
        String value;
        if (couponConditionForm.isAfterDiscount()) {
            value = Utils.formatCurrency(total - handleTypeDiscount(total)) + " VNĐ";
        } else {
            value = Utils.formatCurrency(total - handleTypeDiscount(totalFee)) + " VNĐ";
        }
        return value;
    }

    private int handleTypeDiscount(int price) {
        int discount = 0;
        if (couponConditionForm.getDiscountType() == ParamConstants.DISCOUNT_PERCENT) {
            //Discount Percent
            int percent = (couponConditionForm.getDiscount() * price) / 100;
            int maxDiscount = couponConditionForm.getMaxDiscount();
            //Check Coupon MaxDiscount
            if (maxDiscount == 0 || percent <= maxDiscount) {
                discount = percent;
            } else if (percent > maxDiscount) {
                discount = maxDiscount;
            }
        } else {
            discount = couponConditionForm.getDiscount();
        }
        return discount;
    }

    private String handlePromotionPayment(String s) {
        StringBuilder msg = new StringBuilder().append(getString(R.string.txt_16_by)).append(" ");
        try {
            String[] dataParse = s.split(",");
            for (String aDataParse : dataParse) {
                int value = Integer.parseInt(aDataParse);
                if (value == 0) {
                    msg.append(getString(R.string.txt_16_hotel)).append(", ");
                    imgPromotionPayAtHotel.setVisibility(View.VISIBLE);
                } else if (value == 1) {
                    msg.append(getString(R.string.txt_16_123pay)).append(", ");
                    imgPromotionPay123.setVisibility(View.VISIBLE);
                } else if (value == 2) {
                    msg.append(getString(R.string.txt_16_payoo)).append(", ");
                    imgPromotionPayoo.setVisibility(View.VISIBLE);
                } else if (value == 3) {
                    msg.append("").append(", ");
                } else if (value == 4) {
                    msg.append(getString(R.string.txt_16_payoo_paystore)).append(", ");
                    imgPromotionPayoo.setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {

        }

        return msg.substring(0, msg.length() - 2);
    }

    @SuppressWarnings("unchecked")
    private void createReservationforPayOnline(final int type) {
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

            String mobile = bundle.getString("MOBILE");
            if (mobile != null) {
                userBookingDto.setMobile(mobile);
            }

            long coupon = bundle.getLong("COUPON");
            if (coupon != ReservationActivity.NO_COUPON) {
                userBookingDto.setCouponIssuedSn(coupon);
            }

            if (type == PAY123) {
                userBookingDto.setPaymentMethod(ParamConstants.PAYMENT_METHOD_PAY123);
            } else {
                userBookingDto.setPaymentMethod(ParamConstants.PAYMENT_METHOD_PAYOO);
            }

            ControllerApi.getmInstance().createNewUserBooking(this, userBookingDto, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    RestResult restResult = (RestResult) object;

                    //Set UserBookingSn
                    _userBookingSn = restResult.getSn();

                    handlePacketPayment(type, restResult.getSn(), restResult.getMapInfo());
                }
            });
        }
    }

    private void findPaymentInfoFormMap(final int type, final long userBookingSn) {
        ControllerApi.getmInstance().findPaymentInfoFormMap(this, userBookingSn, Utils.getClientIp(), new ResultMapApi() {
            @Override
            public void map(Map<String, Object> map) {
                handlePacketPayment(type, userBookingSn, map);
            }
        });
    }

    private void handlePacketPayment(int type, long userBookingSn, Map<String, Object> map) {

        if (type == PAY123) {
            String s = map.get("payment123InfoForm").toString();
            gotoBrowserPaymentActivity(userBookingSn, s);
        } else {
            String s = map.get("payooInfoForm").toString();
            Gson gson = new Gson();
            PayooInfoForm payooInfoForm = gson.fromJson(s, PayooInfoForm.class);

            if (payooInfoForm != null) {
                //Set TransactionId
                transactionId = payooInfoForm.getOrderNo();
                //Handle Payoo
                CreateOrderResponse response = new CreateOrderResponse(payooInfoForm.getChecksum(), payooInfoForm.getOrderInfo());
                PayooHandle.getInstance().init(Billing_Information.this, payooInfoForm);
                PayooHandle.getInstance().request(Billing_Information.this, response);
            }
        }
    }


    private void gotoBrowserPaymentActivity(long bookingSn, String infoForm) {
        Gson gson = new Gson();
        PaymentInfoForm paymentInfoForm = gson.fromJson(infoForm, PaymentInfoForm.class);
        Intent intent = new Intent(Billing_Information.this, BrowserPaymentActivity.class);
        intent.putExtra("PaymentInfoForm", paymentInfoForm);
        intent.putExtra("userBookingSn", bookingSn);
        intent.putExtra("METHOD_PAYMENT", bundle.getString("METHOD_PAYMENT", "default"));
        intent.putExtra("FLASH_SALE", isFlashSale);
        intent.putExtra("New_Payment", isNewPayment);
        startActivity(intent);


        //close Activity HotelDetailActivity
        if (HotelDetailActivity.hotelDetailActivity != null) {
            HotelDetailActivity.hotelDetailActivity.finish();
        }
        finish();
        overridePendingTransition(R.anim.right_to_left, R.anim.stable);
    }

    private void hidePayAtHotel() {
        lnHotel.setAlpha(0.5f);
        lnHotel.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.stable, R.anim.left_to_right);
    }

    /*
    / Call Back From Payoo
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PayooPaymentSDK.REQUEST_CODE_PAYOO_PAYMENT_SDK) {
            String message;
            int status;
            if (resultCode == Activity.RESULT_OK) {
                ResponseObject responseObject = (ResponseObject) data.getSerializableExtra(PayooPaymentSDK.EXTRA_RESPONSE_OBJECT);
                GroupType groupType = (GroupType) data.getSerializableExtra(PayooPaymentSDK.EXTRA_GROUP_TYPE);
                switch (groupType) {
                    case SUCCESS:
                        MyLog.writeLog("Payoo - OK");
                        message = getString(R.string.msg_3_9_payment_successful);
                        status = RESULT_OK;
                        //Callback Payoo OK
                        break;
                    case FAILURE:
                    case UNKNOWN:
                    default:
                        MyLog.writeLog("Payoo - Fail");
                        message = getMessageMethodPayment();
                        status = RESULT_CANCELED;
                        break;
                }

                //Update Status to Server
                ResponseData responseData = responseObject.getData();
                if (responseData != null) {
                    if (responseData.getPaymentCode() == null) {
                        updatePayooResult(status, message, null);
                    } else {
                        updatePayooResult(status, message + "\n" + getString(R.string.msg_3_9_pos_payment) + " " + responseData.getPaymentCode(), responseData.getPaymentCode());
                    }
                }

            } else {
                MyLog.writeLog("Payoo - Error");
                message = getMessageMethodPayment();
                status = RESULT_CANCELED;
                updatePayooResult(status, message, null);
            }

        }
    }

    private void updatePayooResult(final int status, final String message, String paymentCode) {
        if (transactionId != null) {
            ControllerApi.getmInstance().updatePayooPaymentResult(this, Utils.getClientIp(), transactionId, paymentCode, new ResultApi() {
                @Override
                public void resultApi(Object object) {
                    RestResult restResult = (RestResult) object;
                    if (restResult.getResult() == 1) {
                        //OK
                        showPopupResult(status, message);
                    } else {
                        //FAIL
                        //Show Popup
                        showPopupResult(RESULT_CANCELED, getMessageMethodPayment());
                    }
                }
            });

        }
    }

    private void checkPaymentMethod(int status) {
        if (status == RESULT_CANCELED && isNewPayment) {
            return;
        }
        //Check Payment always Online
        if (methodPayment != null && methodPayment.equals(ParamConstants.METHOD_ALWAYS_PAY_ONLINE) || methodPayment != null && methodPayment.equals(ParamConstants.METHOD_PAY_ONLINE_IN_DAY) || isFlashSale) {

            if (status == RESULT_OK) {

                //close Activity Reservation
                if (ReservationActivity.reservation != null) {
                    ReservationActivity.reservation.finish();
                }

                gotoReservationDetailActivity();

            } else {
                //Back Press
                if (isFlashSale) {

                    //close Activity Reservation
                    if (ReservationActivity.reservation != null) {
                        ReservationActivity.reservation.finish();
                    }
                    //Goto Home
                    finish();

                } else {

                    //Goto Reservation
                    finish();

                }

            }

            // Check Payment can Pay at Hotel
        } else {

            //close Activity Reservation
            if (ReservationActivity.reservation != null) {
                ReservationActivity.reservation.finish();
            }

            gotoReservationDetailActivity();

        }

    }


    private String getMessageMethodPayment() {
        String msg;
        //Check Payment always online
        if (methodPayment != null && methodPayment.equals(ParamConstants.METHOD_ALWAYS_PAY_ONLINE) || methodPayment != null && methodPayment.equals(ParamConstants.METHOD_PAY_ONLINE_IN_DAY) || isFlashSale) {
            msg = getString(R.string.msg_3_9_payment_process_failed);
        } else {
            msg = getString(R.string.msg_3_9_payment_process_failed);
            //msg = getString(R.string.msg_3_9_payment_unpaid); //Cancel Booking
        }
        return msg;
    }

    private void showPopupResult(final int status, String messsage) {
        final Dialog dialog = new Dialog(Billing_Information.this, R.style.dialog_full_transparent_background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.message_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        }
        TextViewSFRegular tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(messsage);
        TextViewSFBold btnOK = dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPaymentMethod(status);
                dialog.dismiss();

                //close Activity Reservation
                if (ReservationActivity.reservation != null) {
                    ReservationActivity.reservation.finish();
                }

                finish();
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);

            }
        });
    }

    private void gotoReservationDetailActivity() {

        DialogUtils.showLoadingProgress(Billing_Information.this, false);
        ControllerApi.getmInstance().findUserBookingDetail(Billing_Information.this, _userBookingSn, true, new ResultApi() {
            @Override
            public void resultApi(Object object) {
                //hide loading
                DialogUtils.hideLoadingProgress();
                //result
                UserBookingForm userBookingForm = (UserBookingForm) object;

                Intent detail = new Intent(Billing_Information.this, ReservationDetailActivity.class);
                detail.putExtra("UserBookingForm", userBookingForm);
                detail.putExtra(FLAG_SHOW_REWARD_CHECKIN, true);
                startActivity(detail);
                finish();
                overridePendingTransition(R.anim.right_to_left, R.anim.stable);
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (billingInformation != null)
//            billingInformation = null;
//    }
}
